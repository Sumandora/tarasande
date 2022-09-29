package de.florianmichael.tarasande.screen.widget

import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.PressAction
import net.minecraft.text.Text

class AllMouseButtonWidget(x: Int, y: Int, width: Int, height: Int, message: Text, onPress: AllMousePressAction) : ButtonWidget(x, y, width, height, message, onPress) {
    private var lastMouseButton = 0

    override fun onClick(mouseX: Double, mouseY: Double) {
        (this.onPress as AllMousePressAction).onPress(this.lastMouseButton, this)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        this.lastMouseButton = button
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun isValidClickButton(button: Int): Boolean {
        return true
    }
}

abstract class AllMousePressAction : PressAction {
    abstract fun onPress(mouseButton: Int, button: ButtonWidget)

    override fun onPress(button: ButtonWidget?) {
        // Kinda Unimplemented
    }
}
