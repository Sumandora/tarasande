package net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements

import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.screen.menu.panel.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

open class PanelElements<T : Element>(title: String, x: Double, y: Double, minWidth: Double, minHeight: Double) : Panel(title, x, y, minWidth, minHeight) {

    internal val elementList = ArrayList<T>()

    override fun init() {
        for (it in elementList) {
            it.init()
        }
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        matrices?.push()
        matrices?.translate(x + 2, y + titleBarHeight + 2, 0.0)
        val x = x + 2
        var y = y + titleBarHeight + 2
        for (it in elementList) {
            it.width = panelWidth - 4
            if (y + it.getHeight() + 2 >= this.y - scrollOffset) it.render(matrices, (mouseX - x).toInt(), (mouseY - y - scrollOffset).toInt(), delta)

            matrices?.translate(0.0, it.getHeight() + 2, 0.0)
            y += it.getHeight() + 2

            if (y > this.y - scrollOffset + panelHeight) break
        }
        matrices?.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val hovered = RenderUtil.isHovered(mouseX, mouseY, x, y + titleBarHeight, x + panelWidth, y + panelHeight)
        val x = x + 2
        var y = y + titleBarHeight + 2
        elementList.forEach {
            it.mouseClicked(if (hovered) mouseX - x else -1.0, if (hovered) mouseY - y - scrollOffset else -1.0, button)
            y += it.getHeight() + 2
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        val x = x + 2
        var y = y + titleBarHeight + 2
        elementList.forEach {
            it.mouseReleased(mouseX - x, mouseY - y - scrollOffset, button)
            y += it.getHeight() + 2
        }
        super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        val x = x + 2
        var y = y + titleBarHeight + 2
        elementList.forEach {
            if (it.mouseScrolled(mouseX - x, mouseY - y - scrollOffset, amount)) return true
            y += it.getHeight() + 2
        }
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        elementList.forEach {
            if (it.keyPressed(keyCode, scanCode, modifiers)) return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        elementList.forEach { it.charTyped(chr, modifiers) }
        super.charTyped(chr, modifiers)
    }

    override fun tick() {
        elementList.forEach { it.tick() }
        super.tick()
    }

    override fun onClose() {
        elementList.forEach { it.onClose() }
        super.onClose()
    }

    override fun getMaxScrollOffset(): Double {
        var height = 0.0
        elementList.forEach { height += it.getHeight() + 2 }
        return if (height > 0.0)
            height - 2
        else
            0.0
    }
}