package su.mandora.tarasande.screen.menu.panel

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.screen.menu.utils.DragInfo
import su.mandora.tarasande.screen.menu.utils.IElement
import su.mandora.tarasande.util.render.RenderUtil
import kotlin.math.min

open class Panel(val title: String, var x: Double, var y: Double, val minWidth: Double, val minHeight: Double, val maxWidth: Double? = null, val maxHeight: Double? = null, private val background: Boolean = true) : IElement {

	internal val dragInfo = DragInfo()
	internal val resizeInfo = DragInfo()
	var panelWidth = minWidth
	var panelHeight = minHeight

	protected var scrollOffset = 0.0
	private var scrollSpeed = 0.0

	protected var alignment: Alignment = Alignment.LEFT
	var opened = false

	override fun init() {
	}

	override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
		if (opened) {
			if (background) {
				matrices?.push()
				RenderUtil.fill(matrices, x, y + MinecraftClient.getInstance().textRenderer.fontHeight, x + panelWidth, y + panelHeight, Int.MIN_VALUE)
				matrices?.pop()
			}

			matrices?.push()
			matrices?.translate(0.0, scrollOffset, 0.0)
			renderContent(matrices, mouseX, mouseY, delta)
			matrices?.pop()
		}

		matrices?.push()
		RenderUtil.fill(matrices, x, y, x + panelWidth, y + MinecraftClient.getInstance().textRenderer.fontHeight, TarasandeMain.get().clientValues?.accentColor?.getColor()!!.rgb)
		when (alignment) {
			Alignment.LEFT -> MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, title, x.toFloat() + 1, y.toFloat() + 0.5F, -1)
			Alignment.MIDDLE -> MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, title, x.toFloat() + panelWidth.toFloat() / 2.0f - MinecraftClient.getInstance().textRenderer.getWidth(title).toFloat() / 2.0F, y.toFloat() + 0.5F, -1)
			Alignment.RIGHT -> MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, title, x.toFloat() + panelWidth.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(title).toFloat(), y.toFloat() + 0.5F, -1)
		}

		matrices?.pop()

		if (dragInfo.dragging) {
			x = mouseX - dragInfo.xOffset
			y = mouseY - dragInfo.yOffset
		}

		x = MathHelper.clamp(x, 0.0, MinecraftClient.getInstance().window.scaledWidth.toDouble() - panelWidth)
		y = MathHelper.clamp(y, 0.0, MinecraftClient.getInstance().window.scaledHeight.toDouble() - panelHeight)

		if (resizeInfo.dragging) {
			panelWidth = MathHelper.clamp(mouseX + resizeInfo.xOffset, 0.0, MinecraftClient.getInstance().window.scaledWidth.toDouble()) - x
			panelHeight = MathHelper.clamp(mouseY + resizeInfo.yOffset, 0.0, MinecraftClient.getInstance().window.scaledHeight.toDouble()) - y
		}

		panelWidth = MathHelper.clamp(panelWidth, minWidth, maxWidth ?: MinecraftClient.getInstance().window.scaledWidth.toDouble())
		panelHeight = MathHelper.clamp(panelHeight, minHeight, maxHeight ?: MinecraftClient.getInstance().window.scaledHeight.toDouble())

		scrollOffset = MathHelper.clamp(scrollOffset + scrollSpeed, min(-(getMaxScrollOffset() - (panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight - 5)), 0.0), 0.0)
		scrollSpeed -= scrollSpeed * 0.2
	}

	open fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + (if(opened) panelHeight else MinecraftClient.getInstance().textRenderer.fontHeight.toDouble()))) {
			if (button == 0) {
				if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + MinecraftClient.getInstance().textRenderer.fontHeight)) {
					dragInfo.setDragInfo(true, mouseX - x, mouseY - y)
				}
				if (RenderUtil.isHovered(mouseX, mouseY, x + panelWidth - 2, y + panelHeight - 2, x + panelWidth + 2, y + panelHeight + 2)) {
					resizeInfo.setDragInfo(true, mouseX - (x + panelWidth - 2), mouseY - (y + panelHeight - 2))
				}
			} else if (button == 1) {
				if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + MinecraftClient.getInstance().textRenderer.fontHeight))
					opened = !opened
			}
			return true
		}
		return false
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
		if (button == 0) {
			dragInfo.setDragInfo(false, 0.0, 0.0)
			resizeInfo.setDragInfo(false, 0.0, 0.0)
		}
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
		if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + panelHeight)) {
			scrollSpeed += amount
			return true
		}
		return false
	}

	open fun getMaxScrollOffset(): Double = 0.0

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		return false
	}

	override fun charTyped(chr: Char, modifiers: Int) {
	}

	override fun tick() {
	}

	override fun onClose() {
		dragInfo.setDragInfo(false, 0.0, 0.0)
	}

	override fun getHeight() = 0.0 // never used
}

enum class Alignment {
	LEFT, MIDDLE, RIGHT
}