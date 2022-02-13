package su.mandora.tarasande.screen.menu.panel.impl.fixed

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventRender2D
import su.mandora.tarasande.event.EventTick
import su.mandora.tarasande.screen.menu.panel.Alignment
import su.mandora.tarasande.screen.menu.panel.Panel
import su.mandora.tarasande.util.render.RenderUtil

open class PanelFixed(title: String, x: Double, y: Double, width: Double, height: Double = MinecraftClient.getInstance().textRenderer.fontHeight.toDouble(), resizable: Boolean = true, background: Boolean = false) : Panel(title, x, y, width, height, if (resizable) null else width, if (resizable) null else height, background) {

	init {
		TarasandeMain.get().managerEvent?.add { event ->
			if (event is EventRender2D) {
				if (isVisible() && opened) {
					if (MinecraftClient.getInstance().currentScreen != TarasandeMain.get().screens?.screenMenu) {
						event.matrices.push()
						render(event.matrices, -1, -1, MinecraftClient.getInstance().tickDelta)
						event.matrices.pop()
					}
				}
			} else if (event is EventTick) {
				if (event.state == EventTick.State.PRE) {
					if (MinecraftClient.getInstance().currentScreen != TarasandeMain.get().screens?.screenMenu) {
						tick()
					}
				}
			}
		}
	}

	override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
		if (x + panelWidth / 2 <= MinecraftClient.getInstance().window.scaledWidth * 0.33)
			alignment = Alignment.LEFT
		else if (x + panelWidth / 2 > MinecraftClient.getInstance().window.scaledWidth * 0.33 && x + panelWidth / 2 < MinecraftClient.getInstance().window.scaledWidth * 0.66)
			alignment = Alignment.MIDDLE
		else if (x + panelWidth / 2 > MinecraftClient.getInstance().window.scaledWidth * 0.66)
			alignment = Alignment.RIGHT

		TarasandeMain.get().blur?.bind(true)
		RenderUtil.fill(matrices, x, y, x + panelWidth, y + panelHeight, -1)
		MinecraftClient.getInstance().framebuffer.beginWrite(true)

		super.render(matrices, mouseX, mouseY, delta)
	}

	open fun isVisible() = true

}