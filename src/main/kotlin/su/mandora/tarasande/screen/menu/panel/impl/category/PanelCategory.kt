package su.mandora.tarasande.screen.menu.panel.impl.category

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.screen.menu.panel.Panel
import su.mandora.tarasande.util.render.RenderUtil

class PanelCategory(private val moduleCategory: ModuleCategory, x: Double, y: Double) : Panel(moduleCategory.name.first() + moduleCategory.name.substring(1).lowercase(), x, y, 150.0, 100.0) {

	private val moduleElementList = ArrayList<ModuleElement>()

	init {
		TarasandeMain.get().managerModule?.list?.forEach { if (it.category == moduleCategory) moduleElementList.add(ModuleElement(it, 100.0)) }
	}

	override fun init() {
		for (it in moduleElementList) {
			it.init()
		}
	}

	override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
		matrices?.push()
		matrices?.translate(x + 2, y + MinecraftClient.getInstance().textRenderer.fontHeight + 2, 0.0)
		val x = x + 2
		var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
		for (it in moduleElementList) {
			it.width = panelWidth - 4
			if (y + it.getHeight() + 2 >= this.y - scrollOffset)
				it.render(matrices, (mouseX - x).toInt(), (mouseY - y - scrollOffset).toInt(), delta)

			matrices?.translate(0.0, it.getHeight() + 2, 0.0)
			y += it.getHeight() + 2

			if (y > this.y - scrollOffset + panelHeight)
				break
		}
		matrices?.pop()
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if (!RenderUtil.isHovered(mouseX, mouseY, x, y + MinecraftClient.getInstance().textRenderer.fontHeight.toDouble(), x + panelWidth, y + panelHeight))
			return super.mouseClicked(mouseX, mouseY, button)

		val x = x + 2
		var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
		moduleElementList.forEach {
			it.mouseClicked(mouseX - x, mouseY - y - scrollOffset, button)
			y += it.getHeight() + 2
		}
		return super.mouseClicked(mouseX, mouseY, button)
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
		val x = x + 2
		var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
		moduleElementList.forEach {
			it.mouseReleased(mouseX - x, mouseY - y - scrollOffset, button)
			y += it.getHeight() + 2
		}
		super.mouseReleased(mouseX, mouseY, button)
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
		val x = x + 2
		var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
		moduleElementList.forEach {
			if (it.mouseScrolled(mouseX - x, mouseY - y - scrollOffset, amount))
				return true
			y += it.getHeight() + 2
		}
		return super.mouseScrolled(mouseX, mouseY, amount)
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		moduleElementList.forEach {
			if (it.keyPressed(keyCode, scanCode, modifiers))
				return true
		}
		return super.keyPressed(keyCode, scanCode, modifiers)
	}

	override fun charTyped(chr: Char, modifiers: Int) {
		moduleElementList.forEach { it.charTyped(chr, modifiers) }
		super.charTyped(chr, modifiers)
	}

	override fun tick() {
		moduleElementList.forEach { it.tick() }
		super.tick()
	}

	override fun onClose() {
		moduleElementList.forEach { it.onClose() }
		super.onClose()
	}

	override fun getMaxScrollOffset(): Double {
		var height = 0.0
		moduleElementList.forEach { height += it.getHeight() + 2 }
		return if (height > 0.0)
			height - 2
		else
			0.0
	}
}