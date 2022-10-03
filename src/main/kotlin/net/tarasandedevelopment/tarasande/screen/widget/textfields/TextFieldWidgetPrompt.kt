package net.tarasandedevelopment.tarasande.screen.widget.textfields

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.mixin.accessor.ITextFieldWidget

open class TextFieldWidgetPrompt(textRenderer: TextRenderer?, x: Int, y: Int, width: Int, height: Int, text: Text?) : TextFieldWidget(textRenderer, x, y, width, height, text) {
    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val prevText = text
        val accessor = this as ITextFieldWidget
        accessor.tarasande_setForceText(message.string + text + if (System.currentTimeMillis() <= 500L) "_" else "")
        super.render(matrices, mouseX, mouseY, delta)
        accessor.tarasande_setForceText(prevText)
    }
}