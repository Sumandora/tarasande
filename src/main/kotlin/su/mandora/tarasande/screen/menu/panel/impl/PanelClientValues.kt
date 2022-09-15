package su.mandora.tarasande.screen.menu.panel.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.valuecomponent.ValueComponent
import su.mandora.tarasande.screen.menu.panel.Panel
import su.mandora.tarasande.util.render.RenderUtil

class PanelClientValues(x: Double, y: Double) : Panel("Client Values", x, y, 150.0, 100.0) {

    private val elements = ArrayList<ValueComponent>()

    override fun init() {
        if (elements.isEmpty()) {
            for (it in TarasandeMain.get().managerValue?.getValues(TarasandeMain.get().clientValues!!)!!) {
                elements.add(TarasandeMain.get().screens?.screenMenu?.managerValueComponent?.newInstance(it)!!)
            }
        }
        elements.forEach(ValueComponent::init)
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        matrices?.push()
        matrices?.translate(x + 2, y + MinecraftClient.getInstance().textRenderer.fontHeight + 2, 0.0)
        val x = x + 2
        var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
        for (it in elements) {
            it.width = panelWidth - 4
            if (y + it.getHeight() >= this.y - scrollOffset)
                it.render(matrices, (mouseX - x).toInt(), (mouseY - y - scrollOffset).toInt(), delta)

            matrices?.translate(0.0, it.getHeight(), 0.0)
            y += it.getHeight()

            if (y > this.y - scrollOffset + panelHeight)
                break
        }
        matrices?.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!RenderUtil.isHovered(mouseX, mouseY, x, y + MinecraftClient.getInstance().textRenderer.fontHeight, x + panelWidth, y + panelHeight))
            return super.mouseClicked(mouseX, mouseY, button)

        val x = x + 2
        var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
        elements.forEach {
            if (it.value.isEnabled()) {
                it.mouseClicked(mouseX - x, mouseY - y - scrollOffset, button)
            }
            y += it.getHeight()
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        val x = x + 2
        var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
        elements.forEach {
            if (it.value.isEnabled()) {
                it.mouseReleased(mouseX - x, mouseY - y - scrollOffset, button)
            }
            y += it.getHeight()
        }
        super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        val x = x + 2
        var y = y + MinecraftClient.getInstance().textRenderer.fontHeight + 2
        elements.forEach {
            if (it.value.isEnabled()) {
                if (it.mouseScrolled(mouseX - x, mouseY - y - scrollOffset, amount))
                    return true
            }
            y += it.getHeight()
        }
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        elements.forEach {
            if (it.value.isEnabled()) {
                if (it.keyPressed(keyCode, scanCode, modifiers))
                    return true
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        elements.forEach {
            if (it.value.isEnabled()) {
                it.charTyped(chr, modifiers)
            }
        }
        super.charTyped(chr, modifiers)
    }

    override fun tick() {
        elements.forEach {
            if (it.value.isEnabled()) {
                it.tick()
            }
        }
        super.tick()
    }

    override fun onClose() {
        elements.forEach {
            if (it.value.isEnabled()) {
                it.onClose()
            }
        }
        super.onClose()
    }

    override fun getMaxScrollOffset(): Double {
        var height = 0.0
        elements.forEach {
            height += it.getHeight()
        }
        return height
    }
}