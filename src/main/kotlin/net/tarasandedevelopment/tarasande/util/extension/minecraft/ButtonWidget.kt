package net.tarasandedevelopment.tarasande.util.extension.minecraft

import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text

// Hacky constructor
fun ButtonWidget(x: Int, y: Int, width: Int, height: Int, message: Text?, onPress: ButtonWidget.PressAction): ButtonWidget {
    return ButtonWidget.builder(message, onPress).dimensions(x, y, width, height).build()
}