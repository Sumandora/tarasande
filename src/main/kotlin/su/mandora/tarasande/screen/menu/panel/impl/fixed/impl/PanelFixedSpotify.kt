package su.mandora.tarasande.screen.menu.panel.impl.fixed.impl

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.screen.menu.panel.Alignment
import su.mandora.tarasande.screen.menu.panel.impl.fixed.PanelFixed
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.spotify.Spotify

class PanelFixedSpotify(x: Double, y: Double) : PanelFixed("Spotify", x, y, 75.0, resizable = false) {

    private var currTrack: String? = null

    init {
        Spotify.addCallback {
            currTrack = it
        }
    }


    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if(currTrack == null) return

        val accent = TarasandeMain.get().clientValues?.accentColor?.getColor()!!
        RenderSystem.enableBlend()
        when (alignment) {
            Alignment.LEFT -> RenderUtil.drawWithSmallShadow(matrices, currTrack!!, (x - (MinecraftClient.getInstance().textRenderer.getWidth(currTrack!!))).toFloat(), (y + MinecraftClient.getInstance().textRenderer.fontHeight).toFloat(), accent.rgb)
            Alignment.MIDDLE -> RenderUtil.drawWithSmallShadow(matrices, currTrack!!, x.toFloat() + panelWidth.toFloat() / 2.0f - MinecraftClient.getInstance().textRenderer.getWidth(currTrack!!).toFloat() / 2.0f, (y + MinecraftClient.getInstance().textRenderer.fontHeight).toFloat(), accent.rgb)
            Alignment.RIGHT -> RenderUtil.drawWithSmallShadow(matrices, currTrack!!, (x + panelWidth - MinecraftClient.getInstance().textRenderer.getWidth(currTrack!!)).toFloat(), (y + MinecraftClient.getInstance().textRenderer.fontHeight).toFloat(), accent.rgb)
        }
    }
}