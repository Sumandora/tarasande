package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.impl.terminal

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.valuecomponent.ValueComponent
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.PanelElements
import net.tarasandedevelopment.tarasande.screen.cheatmenu.valuecomponent.ValueComponentText
import net.tarasandedevelopment.tarasande.value.ValueSpacer
import net.tarasandedevelopment.tarasande.value.ValueText
import org.lwjgl.glfw.GLFW

class PanelElementsTerminal(x: Double, y: Double, val screenCheatMenu: ScreenCheatMenu) : PanelElements<ValueComponent>("Terminal", x, y, 150.0, 100.0) {

    private val value = ValueText(this, "Prompt", "", manage = false)
    private val textField = ValueComponentText(value, MinecraftClient.getInstance().textRenderer.fontHeight / 2.0).also { screenCheatMenu.managerValueComponent.instances.add(it) }

    init {
        elementList.add(textField)
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        if (textField.isFocused()) {
            scrollOffset = -getMaxScrollOffset()
        }
        super.charTyped(chr, modifiers)
    }

    fun add(input: String) {
        elementList.add(elementList.size - 1, screenCheatMenu.managerValueComponent.newInstance(ValueSpacer(this, input)))
        scrollSpeed -= 1.0
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (textField.isFocused()) {
            when (keyCode) {
                GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                    screenCheatMenu.managerCommand.execute(textField.textFieldWidget.text, this)
                    value.value = ""
                    textField.textFieldWidget.text = ""
                    return true
                }

                GLFW.GLFW_KEY_TAB -> {} //TODO
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}
