package su.mandora.tarasande.screen.menu.valuecomponent

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.valuecomponent.ValueComponent
import su.mandora.tarasande.base.value.Value
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueMode
import java.awt.Color
import kotlin.math.min

class ValueComponentMode(value: Value) : ValueComponent(value) {

	private val animations = hashMapOf<String, Long>()

	override fun init() {
	}

	override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
		matrices?.push()
		matrices?.translate(0.0, getHeight() / 2.0, 0.0)
		matrices?.scale(0.5F, 0.5F, 1.0F)
		matrices?.translate(0.0, -getHeight() / 2.0, 0.0)
		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.name, 0.0F, (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), -1)
		matrices?.pop()

		matrices?.push()
		matrices?.translate(width, getHeight() / 2.0, 0.0)
		matrices?.scale(0.5F, 0.5F, 1.0F)
		matrices?.translate(-width, -getHeight() / 2.0, 0.0)
		val valueMode = value as ValueMode
		for ((index, setting) in valueMode.settings.withIndex()) {
			val animation = min((System.currentTimeMillis() - animations.getOrPut(setting) { 0L }) / 100.0, 1.0)
			val fade = (if (valueMode.selected.contains(setting)) animation else 1.0 - animation)
			MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, setting, (width - MinecraftClient.getInstance().textRenderer.getWidth(setting)).toFloat(), (getHeight() / 2.0F + (index - valueMode.settings.size / 2.0) * MinecraftClient.getInstance().textRenderer.fontHeight - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), RenderUtil.colorInterpolate(Color.white, TarasandeMain.get().clientValues?.accentColor?.getColor()!!, fade).rgb)
		}
		matrices?.pop()
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		val valueMode = value as ValueMode
		for ((index, setting) in valueMode.settings.withIndex()) {
			val x = width - MinecraftClient.getInstance().textRenderer.getWidth(setting) / 2
			val y = getHeight() / 2.0F + (index - valueMode.settings.size / 2.0) * (MinecraftClient.getInstance().textRenderer.fontHeight / 2) - MinecraftClient.getInstance().textRenderer.fontHeight / 4.0F
			if (RenderUtil.isHovered(mouseX, mouseY, x, y, width, y + MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F)) {
				valueMode.select(index)
				valueMode.onChange()
				return true
			}
		}
		return false
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) = false

	override fun charTyped(chr: Char, modifiers: Int) {
	}

	override fun tick() {
	}

	override fun onClose() {
	}

	override fun getHeight() = ((value as ValueMode).settings.size + 1) * MinecraftClient.getInstance().textRenderer.fontHeight.toDouble() / 2
}