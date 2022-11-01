package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed

import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.features.module.render.ModuleNameProtect
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import java.net.InetAddress

private const val text = "タラサンデ"

class PanelWatermark(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Watermark", x, y, RenderUtil.font().getWidth(text) * 5.0, RenderUtil.font().fontHeight() * 5.0, fixed = true) {

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        // Mind the shadows and leave some space
        val xScale = (panelWidth - 4) / (RenderUtil.font().getWidth(text) + 1.0)
        val yScale = (panelHeight - 4) / (RenderUtil.font().fontHeight() + 3.0)

        matrices?.push()
        matrices?.translate(x + 1, y + titleBarHeight + 1, 0.0)
        matrices?.scale(xScale.toFloat(), yScale.toFloat(), 1.0f)
        matrices?.translate(-(x + 1), -(y + titleBarHeight + 1), 0.0)

        RenderUtil.font().textShadow(matrices, text, (x + 1).toFloat(), (y + titleBarHeight + 1).toFloat(), TarasandeMain.get().clientValues.accentColor.getColor().rgb, offset = 0.5F)
        matrices?.pop()

        RenderUtil.font().textShadow(matrices, TarasandeMain.get().name, (x + 1).toFloat(), (y + panelHeight - titleBarHeight).toFloat(), TarasandeMain.get().clientValues.accentColor.getColor().rgb)

        val moduleNameProtect = TarasandeMain.get().managerModule.get(ModuleNameProtect::class.java)

        if (moduleNameProtect.enabled && moduleNameProtect.hidePersonalName.value)
            return

        val userHost = System.getProperty("user.name") + "@" + InetAddress.getLocalHost().hostName
        RenderUtil.font().textShadow(matrices, userHost, (x + panelWidth - RenderUtil.font().getWidth(userHost) - 1).toFloat(), (y + panelHeight - titleBarHeight).toFloat(), TarasandeMain.get().clientValues.accentColor.getColor().rgb)
    }
}