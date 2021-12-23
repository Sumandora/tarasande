package su.mandora.tarasande.screen.menu.valuecomponent

import net.minecraft.block.Block
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.registry.Registry
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.valuecomponent.ValueComponent
import su.mandora.tarasande.base.value.Value
import su.mandora.tarasande.mixin.accessor.ITextFieldWidget
import su.mandora.tarasande.screen.accountmanager.elements.TextFieldWidgetPlaceholder
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueBlock
import java.util.concurrent.CopyOnWriteArrayList

class ValueComponentBlock(value: Value) : ValueComponent(value) {
	private val textFieldWidget = TextFieldWidgetPlaceholder(MinecraftClient.getInstance().textRenderer, 0, 0, 40 * 2, MinecraftClient.getInstance().textRenderer.fontHeight * 2 - 1, Text.of("Search"))

	private val searchResults = CopyOnWriteArrayList<Block>()

	init {
		textFieldWidget.setMaxLength(Int.MAX_VALUE)
		textFieldWidget.setDrawsBackground(false)
		updateSearchResults()
	}

	override fun init() {
	}

	override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
		matrices?.push()
		matrices?.translate(0.0, getHeight() / 2.0, 0.0)
		matrices?.scale(0.5F, 0.5F, 1.0F)
		matrices?.translate(0.0, -getHeight() / 2.0, 0.0)
		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.name, 0.0F, (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), -1)
		matrices?.pop()

		val valueBlock = value as ValueBlock

		for ((index, block) in valueBlock.list.withIndex()) {
			matrices?.push()
			matrices?.scale(0.5F, 0.5F, 1.0F)
			MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, block.name, (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(block.name) / 2.0F) * 2.0F, MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (index + 0.5F) * 2.0F, if (RenderUtil.isHovered(mouseX.toDouble(), mouseY.toDouble(), (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(block.name) / 2.0F).toDouble(), (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (index + 0.5F)).toDouble(), width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((index + 1) + 0.5F)).toDouble())) TarasandeMain.get().clientValues?.accentColor?.getColor()!!.rgb else -1)
			matrices?.pop()
		}
		RenderUtil.fill(matrices, width.toFloat() - 25.0, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (valueBlock.list.size + 0.5F)).toDouble() + 1.0, width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (valueBlock.list.size + 0.5F)).toDouble() + 1.5, -1)

		matrices?.push()
		matrices?.translate(width - 40, MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (valueBlock.list.size + 0.5F) + 2.0, 0.0)
		matrices?.scale(0.5F, 0.5F, 1.0F)
		if (textFieldWidget.isFocused)
			(textFieldWidget as ITextFieldWidget).setColor(TarasandeMain.get().clientValues?.accentColor?.getColor()!!)
		textFieldWidget.render(matrices, mouseX, mouseY, delta)
		(textFieldWidget as ITextFieldWidget).setColor(null)
		matrices?.pop()

		for ((index, block) in searchResults.withIndex()) {
			matrices?.push()
			matrices?.scale(0.5F, 0.5F, 1.0F)
			MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, block.name, (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(block.name) / 2.0F) * 2.0F, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((valueBlock.list.size + 1) + index + 0.5F) + 2.0F) * 2.0F, if (RenderUtil.isHovered(mouseX.toDouble(), mouseY.toDouble(), (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(block.name) / 2.0F).toDouble(), (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((valueBlock.list.size + 1) + index + 0.5F) + 2.0F).toDouble(), width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((valueBlock.list.size + 1) + index + 0.5F) + 2.0F + MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toDouble())) TarasandeMain.get().clientValues?.accentColor?.getColor()!!.rgb else -1)
			matrices?.pop()
		}
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		val valueBlock = value as ValueBlock

		if (RenderUtil.isHovered(mouseX, mouseY, width - 40, MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (valueBlock.list.size + 0.25) + 2.0, width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * valueBlock.list.size + MinecraftClient.getInstance().textRenderer.fontHeight).toDouble())) { // hacky fix for size hacks
			textFieldWidget.mouseClicked(40.0 * 2 - 1.0, MinecraftClient.getInstance().textRenderer.fontHeight + 0.5, button)
		} else {
			textFieldWidget.mouseClicked(-1.0, -1.0, button)
			textFieldWidget.setCursorToEnd()
		}

		for ((index, block) in valueBlock.list.withIndex()) {
			if (RenderUtil.isHovered(mouseX, mouseY, (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(block.name) / 2.0F).toDouble(), (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (index + 0.5F)).toDouble(), width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((index + 1) + 0.5F)).toDouble())) {
				valueBlock.list.remove(block)
				valueBlock.onChange()
				updateSearchResults()
			}
		}

		for ((index, block) in searchResults.withIndex()) {
			if (RenderUtil.isHovered(mouseX, mouseY, (width.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(block.name) / 2.0F).toDouble(), (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((valueBlock.list.size + 1) + index + 0.5F) + 2.0F).toDouble(), width, (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * ((valueBlock.list.size + 1) + index + 0.5F) + 2.0F + MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toDouble())) {
				if (!valueBlock.list.contains(block)) {
					valueBlock.list.add(block)
					valueBlock.onChange()
				}
				updateSearchResults()
			}
		}

		return false
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (textFieldWidget.isFocused && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
			textFieldWidget.setTextFieldFocused(false)
			textFieldWidget.setCursorToEnd()
			return true
		} else {
			textFieldWidget.keyPressed(keyCode, scanCode, modifiers)
			updateSearchResults()
		}
		return false
	}

	override fun charTyped(chr: Char, modifiers: Int) {
		textFieldWidget.charTyped(chr, modifiers)
		updateSearchResults()
	}

	override fun tick() {
	}

	override fun onClose() {
		textFieldWidget.setTextFieldFocused(false)
		textFieldWidget.setCursorToEnd()
	}

	override fun getHeight(): Double {
		val valueBlock = value as ValueBlock
		return (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F * (valueBlock.list.size + 1 + searchResults.size + 0.5F) + 2.0F).toDouble()
	}

	private fun updateSearchResults() {
		val valueBlock = value as ValueBlock

		searchResults.clear()
		var count = 0
		for (block in Registry.BLOCK) {
			if (count >= 4)
				break
			if (valueBlock.filter(block) && block.name.string.contains(textFieldWidget.text, true) && !valueBlock.list.contains(block)) {
				searchResults.add(block)
				count++
			}
		}
	}
}