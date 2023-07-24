package su.mandora.tarasande.util.screen.widget

import net.minecraft.client.font.TextRenderer
import net.minecraft.text.Text

class TextFieldWidgetPlaceholderPassword(textRenderer: TextRenderer?, x: Int, y: Int, width: Int, height: Int, text: Text?) : TextFieldWidgetPlaceholder(textRenderer, x, y, width, height, text) {
    init {
        setRenderTextProvider { t, _ -> Text.of(if (holdingPlace) t else "*".repeat(t.length)).asOrderedText() }
    }
}
