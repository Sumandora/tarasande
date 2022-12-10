package net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.fixed

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import java.net.InetAddress

private const val JAPANESE_NAME = "タラサンデ"

class PanelWatermark : Panel("Watermark", FontWrapper.getWidth(JAPANESE_NAME) * 5.0, FontWrapper.fontHeight() * 5.0, true) {

    private val hidePersonalName = ValueBoolean(this, "Hide personal name", true)
    private val messageOfTheDay = ValueText(this, "Message of the day", TarasandeMain.get().name)

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        // Mind the shadows and leave some space
        val xScale = (panelWidth - 4) / (FontWrapper.getWidth(JAPANESE_NAME) + 1.0)
        val yScale = (panelHeight - 4) / (FontWrapper.fontHeight() + 3.0)

        matrices?.push()
        matrices?.translate(x + 1, y + titleBarHeight + 1, 0.0)
        matrices?.scale(xScale.toFloat(), yScale.toFloat(), 1.0F)
        matrices?.translate(-(x + 1), -(y + titleBarHeight + 1), 0.0)

        FontWrapper.textShadow(matrices, JAPANESE_NAME, (x + 1).toFloat(), (y + titleBarHeight + 1).toFloat(), TarasandeMain.clientValues().accentColor.getColor().rgb, offset = 0.5F)
        matrices?.pop()

        val userHost = " " + System.getProperty("user.name") + "@" + InetAddress.getLocalHost().hostName
        val userHostWidth = if(hidePersonalName.value) 0 else FontWrapper.getWidth(userHost)

        val motdWidth = FontWrapper.getWidth(messageOfTheDay.value + " ")
        if(motdWidth > panelWidth - userHostWidth) {
            matrices?.push()
            GlStateManager._enableScissorTest()
            val scaleFactor = MinecraftClient.getInstance().window?.scaleFactor!!.toInt()
            GlStateManager._scissorBox(
                (x * scaleFactor).toInt(),
                (MinecraftClient.getInstance()?.window?.height!! - (y + panelHeight) * scaleFactor).toInt(),
                ((panelWidth - userHostWidth) * scaleFactor).toInt(),
                (FontWrapper.fontHeight() * scaleFactor)
            )
            matrices?.translate(-(System.currentTimeMillis() / 50.0) % motdWidth, 0.0, 0.0)
            FontWrapper.textShadow(matrices, messageOfTheDay.value + " " + messageOfTheDay.value, (x + 1).toFloat(), (y + panelHeight - FontWrapper.fontHeight()).toFloat(), TarasandeMain.clientValues().accentColor.getColor().rgb)
            GlStateManager._disableScissorTest()
            matrices?.pop()
        } else {
            FontWrapper.textShadow(matrices, messageOfTheDay.value, (x + 1).toFloat(), (y + panelHeight - FontWrapper.fontHeight()).toFloat(), TarasandeMain.clientValues().accentColor.getColor().rgb)
        }

        if (hidePersonalName.value)
            return

        FontWrapper.textShadow(matrices, userHost, (x + panelWidth - userHostWidth - 1).toFloat(), (y + panelHeight - FontWrapper.fontHeight()).toFloat(), TarasandeMain.clientValues().accentColor.getColor().rgb)
    }
}