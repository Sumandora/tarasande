package net.tarasandedevelopment.tarasande.screen.menu.panel.impl.fixed.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.module.render.ModuleNameProtect
import net.tarasandedevelopment.tarasande.screen.menu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.fixed.PanelFixed
import java.net.InetAddress

private const val text = "タラサンデ"

class PanelFixedWatermark(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : PanelFixed("Watermark", x, y, MinecraftClient.getInstance().textRenderer.getWidth(text) * 5.0, MinecraftClient.getInstance().textRenderer.fontHeight * 5.0, true, true) {

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        // Mind the shadows and leave some space
        val xScale = (panelWidth - 4) / (MinecraftClient.getInstance().textRenderer.getWidth(text) + 1.0)
        val yScale = (panelHeight - 4) / (MinecraftClient.getInstance().textRenderer.fontHeight + 3.0)

        matrices?.push()
        matrices?.translate(x + 1, y + titleBarHeight + 1, 0.0)
        matrices?.scale(xScale.toFloat(), yScale.toFloat(), 1.0f)
        matrices?.translate(-(x + 1), -(y + titleBarHeight + 1), 0.0)

        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, text, (x + 1).toFloat(), (y + titleBarHeight + 1).toFloat(), TarasandeMain.get().clientValues.accentColor.getColor().rgb)
        matrices?.pop()

        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, TarasandeMain.get().name, (x + 1).toFloat(), (y + panelHeight - titleBarHeight).toFloat(), TarasandeMain.get().clientValues.accentColor.getColor().rgb)

        val moduleNameProtect = TarasandeMain.get().managerModule.get(ModuleNameProtect::class.java)

        if (moduleNameProtect.enabled && moduleNameProtect.hidePersonalName.value)
            return

        val userHost = System.getProperty("user.name") + "@" + InetAddress.getLocalHost().hostName
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, userHost, (x + panelWidth - MinecraftClient.getInstance().textRenderer.getWidth(userHost) - 1).toFloat(), (y + panelHeight - titleBarHeight).toFloat(), TarasandeMain.get().clientValues.accentColor.getColor().rgb)
    }
}