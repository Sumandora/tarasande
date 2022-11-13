package net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api

import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.helper.element.ElementWidth
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.floor

open class PanelElements<T : ElementWidth>(title: String, minWidth: Double, minHeight: Double, fixed: Boolean = false, scissor: Boolean = false) : Panel(title, minWidth, minHeight, fixed = fixed, scissor = scissor) {

    // For sidebar panels
    constructor(title: String, width: Double): this(title, width, 0.0, scissor = true)

    val elementList = CopyOnWriteArrayList<T>()

    override fun init() {
        for (it in elementList) {
            it.init()
        }
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        matrices?.push()
        val x = x + 2
        var y = y + titleBarHeight + 2
        matrices?.translate(x, y, 0.0)
        val hovered = RenderUtil.isHovered(mouseX.toDouble(), mouseY.toDouble(), x, y + titleBarHeight, x + panelWidth, y + panelHeight)
        for (it in elementList) {
            it.width = panelWidth - 4
            if (y + it.getHeight() + 2 >= this.y - scrollOffset)
                it.render(matrices, (if (hovered) mouseX - x else -1.0).toInt(), (if (hovered) mouseY - y - scrollOffset else -1.0).toInt(), delta)

            matrices?.translate(0.0, it.getHeight() + 2, 0.0)
            y += it.getHeight() + 2

            if (y > this.y - scrollOffset + panelHeight) break
        }
        matrices?.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val mouseX = floor(mouseX) // fix for render only getting integer mouse position
        val mouseY = floor(mouseY)
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
        var height = 0.0
        elementList.forEach { height += it.getHeight() + 2 }
        return if (height > 0.0)
            height - 2
        else
            0.0
    }
}