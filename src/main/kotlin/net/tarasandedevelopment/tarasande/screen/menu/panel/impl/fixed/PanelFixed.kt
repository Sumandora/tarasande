package net.tarasandedevelopment.tarasande.screen.menu.panel.impl.fixed

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventRender2D
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.screen.menu.panel.Alignment
import net.tarasandedevelopment.tarasande.screen.menu.panel.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import java.awt.Color

open class PanelFixed(title: String, x: Double, y: Double, width: Double, height: Double = MinecraftClient.getInstance().textRenderer.fontHeight.toDouble(), resizable: Boolean = true, background: Boolean = false) : Panel(title, x, y, width, height, if (resizable) null else width, if (resizable) null else height, background) {

    init {
        TarasandeMain.get().managerEvent.add { event ->
            when (event) {
                is EventRender2D -> {
                    if (isVisible() && opened) {
                        if (MinecraftClient.getInstance().currentScreen != TarasandeMain.get().screenCheatMenu) {
                            event.matrices.push()
                            render(event.matrices, -1, -1, MinecraftClient.getInstance().tickDelta)
                            event.matrices.pop()
                        }
                    }
                }

                is EventTick -> {
                    if (event.state == EventTick.State.PRE) {
                        if (MinecraftClient.getInstance().currentScreen != TarasandeMain.get().screenCheatMenu) {
                            tick()
                        }
                    }
                }
            }
        }
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (x + panelWidth / 2 <= MinecraftClient.getInstance().window.scaledWidth * 0.33) alignment = Alignment.LEFT
        else if (x + panelWidth / 2 > MinecraftClient.getInstance().window.scaledWidth * 0.33 && x + panelWidth / 2 < MinecraftClient.getInstance().window.scaledWidth * 0.66) alignment = Alignment.MIDDLE
        else if (x + panelWidth / 2 > MinecraftClient.getInstance().window.scaledWidth * 0.66) alignment = Alignment.RIGHT

        TarasandeMain.get().blur.bind(true)
        RenderUtil.fill(matrices, x, y, x + panelWidth, y + (if (opened && isVisible()) panelHeight else titleBarHeight).toDouble(), Color.white.rgb)
        MinecraftClient.getInstance().framebuffer.beginWrite(true)

        super.render(matrices, mouseX, mouseY, delta)
    }

    open fun isVisible() = true

}