package su.mandora.tarasande.system.screen.panelsystem.impl.fixed

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.gui.DrawContext
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueText
import su.mandora.tarasande.system.screen.panelsystem.Panel
import su.mandora.tarasande.util.render.font.FontWrapper
import java.net.InetAddress

private const val JAPANESE_NAME = "タラサンデ"

class PanelWatermark : Panel("Watermark", 150.0, 50.0, true) {

    private val hidePersonalName = ValueBoolean(this, "Hide personal name", true)
    private val messageOfTheDay = ValueText(this, "Message of the day", TARASANDE_NAME)

    private val localHost = System.getProperty("user.name") + run {
        try {
            "@" + InetAddress.getLocalHost().hostName
        } catch (t: Throwable) {
            "" // certain systems are unable to supply a proper host name
        }
    }

    override fun renderContent(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        // Mind the shadows and leave some space
        val xScale = (panelWidth - 4) / (FontWrapper.getWidth(JAPANESE_NAME) + 1.0)
        val yScale = (panelHeight - 4) / (FontWrapper.fontHeight() + 3.0)

        context.matrices.push()
        context.matrices.translate(x + 1, y + titleBarHeight + 1, 0.0)
        context.matrices.scale(xScale.toFloat(), yScale.toFloat(), 1F)
        context.matrices.translate(-(x + 1), -(y + titleBarHeight + 1), 0.0)

        FontWrapper.textShadow(context, JAPANESE_NAME, (x + 1).toFloat(), (y + titleBarHeight + 1).toFloat(), TarasandeValues.accentColor.getColor().rgb, offset = 0.5F)
        context.matrices.pop()

        val userHost = " $localHost"
        val userHostWidth = if (hidePersonalName.value) 0 else FontWrapper.getWidth(userHost)

        val motdWidth = FontWrapper.getWidth(messageOfTheDay.value + " ")
        if (motdWidth > panelWidth - userHostWidth) {
            context.matrices.push()
            GlStateManager._enableScissorTest()
            val scaleFactor = mc.window.scaleFactor.toInt()
            GlStateManager._scissorBox(
                (x * scaleFactor).toInt(),
                (mc.window.height - (y + panelHeight) * scaleFactor).toInt(),
                ((panelWidth - userHostWidth) * scaleFactor).toInt(),
                (FontWrapper.fontHeight() * scaleFactor)
            )
            context.matrices.translate(-(System.currentTimeMillis() / 50.0) % motdWidth, 0.0, 0.0)
            FontWrapper.textShadow(context, messageOfTheDay.value + " " + messageOfTheDay.value, (x + 1).toFloat(), (y + panelHeight - FontWrapper.fontHeight()).toFloat(), TarasandeValues.accentColor.getColor().rgb)
            GlStateManager._disableScissorTest()
            context.matrices.pop()
        } else {
            FontWrapper.textShadow(context, messageOfTheDay.value, (x + 1).toFloat(), (y + panelHeight - FontWrapper.fontHeight()).toFloat(), TarasandeValues.accentColor.getColor().rgb)
        }

        if (hidePersonalName.value)
            return

        FontWrapper.textShadow(context, userHost, (x + panelWidth - userHostWidth - 1).toFloat(), (y + panelHeight - FontWrapper.fontHeight()).toFloat(), TarasandeValues.accentColor.getColor().rgb)
    }
}