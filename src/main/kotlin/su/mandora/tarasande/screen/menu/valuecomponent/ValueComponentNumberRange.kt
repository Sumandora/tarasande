package su.mandora.tarasande.screen.menu.valuecomponent

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.valuecomponent.ValueComponent
import su.mandora.tarasande.base.value.Value
import su.mandora.tarasande.screen.menu.utils.DragInfo
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueNumberRange
import java.awt.Color
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ValueComponentNumberRange(value: Value) : ValueComponent(value) {

	private val minDragInfo = DragInfo()
	private val maxDragInfo = DragInfo()

	override fun init() {
	}

	override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
		matrices?.push()
		matrices?.translate(0.0, getHeight() / 2.0, 0.0)
		matrices?.scale(0.5F, 0.5F, 1.0F)
		matrices?.translate(0.0, -getHeight() / 2.0, 0.0)
		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.name, 0.0F, (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), -1)
		matrices?.pop()

		val valueNumber = value as ValueNumberRange

		if (minDragInfo.dragging) {
			val mousePos = mouseX - (width - 50)
			val increment = BigDecimal(valueNumber.increment)
			// hacky
			val string = BigDecimal(valueNumber.min).add(((BigDecimal(mousePos).divide(BigDecimal(50.0))).multiply(BigDecimal(valueNumber.max).subtract(BigDecimal(valueNumber.min))))).divide(increment, 0, RoundingMode.HALF_UP).multiply(increment).toPlainString()
			// even more hacky
			valueNumber.minValue = MathHelper.clamp(java.lang.Double.parseDouble(string.substring(0..min(string.length - 1, 7))), valueNumber.min, valueNumber.maxValue)
			if (valueNumber.minValue == -0.0) valueNumber.minValue = 0.0 // bruh
			valueNumber.onChange()
		}

		if (maxDragInfo.dragging) {
			val mousePos = mouseX - (width - 50)
			val increment = BigDecimal(valueNumber.increment)
			// hacky
			val string = BigDecimal(valueNumber.min).add(((BigDecimal(mousePos).divide(BigDecimal(50.0))).multiply(BigDecimal(valueNumber.max).subtract(BigDecimal(valueNumber.min))))).divide(increment, 0, RoundingMode.HALF_UP).multiply(increment).toPlainString()
			// even more hacky
			valueNumber.maxValue = MathHelper.clamp(java.lang.Double.parseDouble(string.substring(0..min(string.length - 1, 7))), valueNumber.minValue, valueNumber.max)
			if (valueNumber.maxValue == -0.0) valueNumber.maxValue = 0.0 // bruh
			valueNumber.onChange()
		}

		val minSliderPos = (valueNumber.minValue - valueNumber.min) / (valueNumber.max - valueNumber.min)
		val maxSliderPos = (valueNumber.maxValue - valueNumber.min) / (valueNumber.max - valueNumber.min)

		val accentColor = TarasandeMain.get().clientValues?.accentColor?.getColor()!!

		if (minSliderPos == maxSliderPos) {
			RenderUtil.fillHorizontalGradient(matrices, max(width - (1.0 - minSliderPos) * 50 - 1, width - 50), getHeight() * 0.25, min(width - (1.0 - maxSliderPos) * 50 + 1, width), getHeight() * 0.75, Color(255, 255, 255, 255 / 4).rgb, Color(accentColor.red, accentColor.green, accentColor.blue, 255 / 4).rgb)
		} else {
			RenderUtil.fillHorizontalGradient(matrices, width - (1.0 - minSliderPos) * 50, getHeight() * 0.25, width - (1.0 - maxSliderPos) * 50, getHeight() * 0.75, Color(255, 255, 255, 255 / 4).rgb, Color(accentColor.red, accentColor.green, accentColor.blue, 255 / 4).rgb)
		}
		RenderUtil.outlinedHorizontalGradient(matrices, width - 50, getHeight() * 0.25, width, getHeight() * 0.75, 2.0F, Color.white.rgb, TarasandeMain.get().clientValues?.accentColor?.getColor()!!.rgb)

		matrices?.push()
		matrices?.translate(width - 50 / 2, getHeight() / 2.0, 0.0)
		matrices?.scale(0.5F, 0.5F, 1.0F)
		matrices?.translate(-(width - 50 / 2), -getHeight() / 2.0, 0.0)
		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.minValue.toString() + "-" + value.maxValue.toString(), (width - 50 / 2.0F - MinecraftClient.getInstance().textRenderer.getWidth(value.minValue.toString() + "-" + value.maxValue.toString()) / 2.0F).toFloat(), (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), -1)
		matrices?.pop()
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if (RenderUtil.isHovered(mouseX, mouseY, width - 50, getHeight() * 0.25, width, getHeight() * 0.75)) {
			val valueNumber = value as ValueNumberRange

			val minSliderPos = (valueNumber.minValue - valueNumber.min) / (valueNumber.max - valueNumber.min)
			val maxSliderPos = (valueNumber.maxValue - valueNumber.min) / (valueNumber.max - valueNumber.min)

			val mousePos = (mouseX - (width - 50)) / 50.0

			val minDelta = abs(minSliderPos - mousePos)
			val maxDelta = abs(maxSliderPos - mousePos)

			if (minDelta == maxDelta) {
				if (mousePos < minSliderPos)
					minDragInfo.setDragInfo(true, mouseX - (width - 50), mouseY - getHeight() * 0.25)
				if (mousePos > maxSliderPos)
					maxDragInfo.setDragInfo(true, mouseX - (width - 50), mouseY - getHeight() * 0.25)
			} else if (minDelta < maxDelta)
				minDragInfo.setDragInfo(true, mouseX - (width - 50), mouseY - getHeight() * 0.25)
			else if (maxDelta < minDelta)
				maxDragInfo.setDragInfo(true, mouseX - (width - 50), mouseY - getHeight() * 0.25)
			return true
		}
		return false
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
		minDragInfo.setDragInfo(false, 0.0, 0.0)
		maxDragInfo.setDragInfo(false, 0.0, 0.0)
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) = false

	override fun charTyped(chr: Char, modifiers: Int) {
	}

	override fun tick() {
	}

	override fun onClose() {
		minDragInfo.setDragInfo(false, 0.0, 0.0)
		maxDragInfo.setDragInfo(false, 0.0, 0.0)
	}

	override fun getHeight() = MinecraftClient.getInstance().textRenderer.fontHeight * 2.0
}