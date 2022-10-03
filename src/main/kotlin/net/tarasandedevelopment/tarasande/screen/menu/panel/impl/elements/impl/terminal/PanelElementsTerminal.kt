package net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.impl.terminal

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.menu.valuecomponent.ValueComponent
import net.tarasandedevelopment.tarasande.screen.menu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.PanelElements
import net.tarasandedevelopment.tarasande.screen.menu.valuecomponent.ValueComponentText
import net.tarasandedevelopment.tarasande.value.ValueSpacer
import net.tarasandedevelopment.tarasande.value.ValueText
import org.lwjgl.glfw.GLFW

class PanelElementsTerminal(x: Double, y: Double, val screenCheatMenu: ScreenCheatMenu) : PanelElements<ValueComponent>("Terminal", x, y, 150.0, 100.0) {

    private val value = ValueText(this, "Prompt", "", manage = false)
    val textField = ValueComponentText(value, MinecraftClient.getInstance().textRenderer.fontHeight / 2.0).also { screenCheatMenu.managerValueComponent.instances.add(it) }

    init {
        elementList.add(textField)
    }

    override fun init() {
        if (TarasandeMain.get().clientValues.autoFocusTerminal.value)
            textField.setFocused(true)
    }

    private fun resetScrolling() {
        scrollSpeed = 0.0
        scrollOffset = -getMaxScrollOffset()
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        if (textField.isFocused()) {
            resetScrolling()
        }
        super.charTyped(chr, modifiers)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (textField.isFocused()) {
            when (keyCode) {
                GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                    println(textField.textFieldWidget.text)
                    elementList.add(elementList.size - 1, screenCheatMenu.managerValueComponent.newInstance(ValueSpacer(this, "Executed '" + textField.textFieldWidget.text + "'"))!!)
                    value.value = ""
                    textField.textFieldWidget.text = ""
                    resetScrolling()
                    return true
                }

                GLFW.GLFW_KEY_TAB -> {} //TODO
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

}