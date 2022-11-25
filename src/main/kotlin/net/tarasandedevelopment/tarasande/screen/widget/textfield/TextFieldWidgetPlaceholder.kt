package net.tarasandedevelopment.tarasande.screen.widget.textfield

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.injection.accessor.ITextFieldWidget
import java.awt.Color

open class TextFieldWidgetPlaceholder(textRenderer: TextRenderer?, x: Int, y: Int, width: Int, height: Int, text: Text?) : TextFieldWidget(textRenderer, x, y, width, height, text) {
    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val prevText = text
        val accessor = this as ITextFieldWidget
        if (text.isEmpty() && !isFocused) {
            this.text = message.string
            if (accessor.tarasande_getColor() == null)
                accessor.tarasande_setColor(Color.lightGray)
        }
        super.render(matrices, mouseX, mouseY, delta)
        this.text = prevText
        accessor.tarasande_setColor(null)
    }
}