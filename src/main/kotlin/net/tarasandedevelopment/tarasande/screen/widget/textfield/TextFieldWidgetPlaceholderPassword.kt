package net.tarasandedevelopment.tarasande.screen.widget.textfield

import com.google.common.base.Strings
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class TextFieldWidgetPlaceholderPassword(textRenderer: TextRenderer?, x: Int, y: Int, width: Int, height: Int, text: Text?) : TextFieldWidgetPlaceholder(textRenderer, x, y, width, height, text) {

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val prevText = text
        if (text.isNotEmpty())
            this.text = Strings.repeat("*", text.length)

        super.render(matrices, mouseX, mouseY, delta)
        this.text = prevText
    }

    override fun eraseWords(wordOffset: Int) {
        text = ""
        setCursorToStart()
    }

    override fun mouseClicked(p_mouseClicked_1_: Double, p_mouseClicked_3_: Double, p_mouseClicked_5_: Int): Boolean {
        val prevText = text
        if (text.isNotEmpty())
            this.text = Strings.repeat("*", text.length)
        val b = super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)
        this.text = prevText
        return b
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (!this.isActive) {
            false
        } else {
            this.selecting = Screen.hasShiftDown()
            if (Screen.isSelectAll(keyCode)) {
                setCursorToEnd()
                setSelectionEnd(0)
                true
            } else if (Screen.isCopy(keyCode)) {
                MinecraftClient.getInstance().keyboard.clipboard = this.selectedText
                true
            } else if (Screen.isPaste(keyCode)) {
                if (this.isEditable) {
                    write(MinecraftClient.getInstance().keyboard.clipboard)
                }
                true
            } else if (Screen.isCut(keyCode)) {
                MinecraftClient.getInstance().keyboard.clipboard = this.selectedText
                if (this.isEditable) {
                    write("")
                }
                true
            } else {
                when (keyCode) {
                    259 -> {
                        if (this.isEditable) {
                            this.selecting = false
                            this.erase(-1)
                            this.selecting = Screen.hasShiftDown()
                        }
                        true
                    }

                    260, 264, 265, 266, 267 -> false
                    261 -> {
                        if (this.isEditable) {
                            this.selecting = false
                            this.erase(1)
                            this.selecting = Screen.hasShiftDown()
                        }
                        true
                    }

                    262 -> {
                        if (Screen.hasControlDown()) {
                            this.setCursorToEnd()
                        } else {
                            moveCursor(1)
                        }
                        true
                    }

                    263 -> {
                        if (Screen.hasControlDown()) {
                            this.setCursorToStart()
                        } else {
                            moveCursor(-1)
                        }
                        true
                    }

                    268 -> {
                        setCursorToStart()
                        true
                    }

                    269 -> {
                        setCursorToEnd()
                        true
                    }

                    else -> false
                }
            }
        }
    }
}
