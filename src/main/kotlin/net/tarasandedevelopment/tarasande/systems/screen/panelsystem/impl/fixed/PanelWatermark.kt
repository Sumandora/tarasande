package net.tarasandedevelopment.tarasande.systems.screen.panelsystem.impl.fixed

import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleNameProtect
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import java.net.InetAddress

private const val text = "タラサンデ"

class PanelWatermark : Panel("Watermark", FontWrapper.getWidth(text) * 5.0, FontWrapper.fontHeight() * 5.0, true) {

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        // Mind the shadows and leave some space
        val xScale = (panelWidth - 4) / (FontWrapper.getWidth(text) + 1.0)
        val yScale = (panelHeight - 4) / (FontWrapper.fontHeight() + 3.0)

        matrices?.push()
        matrices?.translate(x + 1, y + titleBarHeight + 1, 0.0)
        matrices?.scale(xScale.toFloat(), yScale.toFloat(), 1.0f)
        matrices?.translate(-(x + 1), -(y + titleBarHeight + 1), 0.0)

        FontWrapper.textShadow(matrices, text, (x + 1).toFloat(), (y + titleBarHeight + 1).toFloat(), TarasandeMain.clientValues().accentColor.getColor().rgb, offset = 0.5F)
        matrices?.pop()

        FontWrapper.textShadow(matrices, TarasandeMain.get().name, (x + 1).toFloat(), (y + panelHeight - titleBarHeight).toFloat(), TarasandeMain.clientValues().accentColor.getColor().rgb)

        val moduleNameProtect = TarasandeMain.managerModule().get(ModuleNameProtect::class.java)

        if (moduleNameProtect.enabled && moduleNameProtect.hidePersonalName.value)
            return

        val userHost = System.getProperty("user.name") + "@" + InetAddress.getLocalHost().hostName
        FontWrapper.textShadow(matrices, userHost, (x + panelWidth - FontWrapper.getWidth(userHost) - 1).toFloat(), (y + panelHeight - titleBarHeight).toFloat(), TarasandeMain.clientValues().accentColor.getColor().rgb)
    }
}