package net.tarasandedevelopment.tarasande.screen.menu.panel.impl.fixed.impl

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.menu.panel.Alignment
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.fixed.PanelFixed
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

class PanelFixedNowPlaying(x: Double, y: Double) : PanelFixed("Now playing", x, y, 75.0, resizable = false) {

    private var currTrack: String? = null

    private fun getCurrentTrack(): String {
        return String(
            ProcessBuilder(
                "bash",
                "-c",
                "dbus-send --print-reply --dest=\$(dbus-send --session --dest=org.freedesktop.DBus --type=method_call --print-reply /org/freedesktop/DBus org.freedesktop.DBus.ListNames | grep org.mpris.MediaPlayer2 | sed -e 's/.*\\\"\\(.*\\)\\\"/\\1/' | head -n 1) /org/mpris/MediaPlayer2 org.freedesktop.DBus.Properties.Get string:'org.mpris.MediaPlayer2.Player' string:'Metadata'"
            ).start().inputStream.readAllBytes()
        )
    }

    init {
        val t = Thread {
            while (true) {
                val lines = getCurrentTrack().split("\n")
                val titleLine = lines.indexOfFirst { it.contains("string \"xesam:title\"") }
                currTrack = if (titleLine == -1)
                    null
                else
                    lines[lines.indexOfFirst { it.contains("string \"xesam:title\"") } + 1].split("string \"")[1].let { it.substring(0, it.length - 1) } // this calculation is the most retarded shit I've ever wrote bruh
                Thread.sleep(1000L)
            }
        }
        t.name = "Now playing lookup thread"
        t.start()
    }

    override fun isVisible() = currTrack != null

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (currTrack == null)
            return

        val accent = TarasandeMain.get().clientValues.accentColor.getColor()
        RenderSystem.enableBlend()
        when (alignment) {
            Alignment.LEFT -> RenderUtil.drawWithSmallShadow(matrices, currTrack!!, x.toFloat(), (y + titleBarHeight).toFloat(), accent.rgb)
            Alignment.MIDDLE -> RenderUtil.drawWithSmallShadow(matrices, currTrack!!, x.toFloat() + panelWidth.toFloat() / 2.0f - MinecraftClient.getInstance().textRenderer.getWidth(currTrack).toFloat() / 2.0f, (y + titleBarHeight).toFloat(), accent.rgb)
            Alignment.RIGHT -> RenderUtil.drawWithSmallShadow(matrices, currTrack!!, (x + panelWidth - MinecraftClient.getInstance().textRenderer.getWidth(currTrack)).toFloat(), (y + titleBarHeight).toFloat(), accent.rgb)
        }
    }
}