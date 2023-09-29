package su.mandora.tarasande.system.screen.panelsystem.api

import net.minecraft.client.gui.DrawContext
import su.mandora.tarasande.system.screen.panelsystem.Panel
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.helper.element.ElementWidth
import java.util.concurrent.CopyOnWriteArrayList

open class PanelElements<T : ElementWidth>(title: String, minWidth: Double, minHeight: Double) : Panel(title, minWidth, minHeight) {

    val elementList = CopyOnWriteArrayList<T>()

    override fun init() {
        for (it in elementList) {
            it.init()
        }
    }

    override fun renderContent(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        context.matrices.push()
        val x = x + 2
        var y = y + titleBarHeight + 2
        context.matrices.translate(x, y, 0.0)
        for (it in elementList) {
            it.width = panelWidth - 4
            if (y + it.getHeight() + 2 >= this.y - scrollOffset)
                it.render(context, (mouseX - x).toInt(), (mouseY - y - scrollOffset).toInt(), delta)

            context.matrices.translate(0.0, it.getHeight() + 2, 0.0)
            y += it.getHeight() + 2

            if (y > this.y - scrollOffset + panelHeight) break
        }
        context.matrices.pop()
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

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        val x = x + 2
        var y = y + titleBarHeight + 2
        elementList.forEach {
            if (it.mouseScrolled(mouseX - x, mouseY - y - scrollOffset, horizontalAmount, verticalAmount)) return true
            y += it.getHeight() + 2
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
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
        elementList.forEach {
            it.width = panelWidth - 4
            it.tick()
        }
        super.tick()
    }

    override fun onClose() {
        elementList.forEach { it.onClose() }
        super.onClose()
    }

    override fun getMaxScrollOffset(): Double {
        return elementList.sumOf { it.getHeight() + 2 }
    }
}