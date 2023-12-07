package su.mandora.tarasande.util.screen.widget

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import su.mandora.tarasande.injection.accessor.ITextFieldWidget
import java.awt.Color

open class TextFieldWidgetPlaceholder(textRenderer: TextRenderer?, x: Int, y: Int, width: Int, height: Int, text: Text?) : TextFieldWidget(textRenderer, x, y, width, height, text) {

    protected var placeholderActive = false
        private set
    // We can't use the text replacer, because the text replacement is only invoked when the text isn't empty

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val prevText = text
        val accessor = this as ITextFieldWidget
        if (text.isEmpty() && !isFocused) {
            this.text = message.string
            if (accessor.tarasande_getColor() == null)
                accessor.tarasande_setColor(Color.lightGray)
            placeholderActive = true
        }
        super.render(context, mouseX, mouseY, delta)
        placeholderActive = false
        this.text = prevText
        accessor.tarasande_setColor(null)
    }
}
