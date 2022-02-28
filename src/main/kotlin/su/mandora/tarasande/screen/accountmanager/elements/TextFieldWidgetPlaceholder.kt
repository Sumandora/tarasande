package su.mandora.tarasande.screen.accountmanager.elements

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import su.mandora.tarasande.mixin.accessor.ITextFieldWidget

open class TextFieldWidgetPlaceholder(textRenderer: TextRenderer?, x: Int, y: Int, width: Int, height: Int, text: Text?) : TextFieldWidget(textRenderer, x, y, width, height, text) {
    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val prevText = text
        if (text.isEmpty() && !isFocused)
            (this as ITextFieldWidget).setForceText(message.asString())
        super.render(matrices, mouseX, mouseY, delta)
        (this as ITextFieldWidget).setForceText(prevText)
    }
}