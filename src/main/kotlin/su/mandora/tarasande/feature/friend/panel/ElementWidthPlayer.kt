package su.mandora.tarasande.feature.friend.panel

import com.mojang.authlib.GameProfile
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec2f
import su.mandora.tarasande.feature.friend.Friends
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.injection.accessor.ITextFieldWidget
import su.mandora.tarasande.system.base.valuesystem.impl.ValueText
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.impl.ElementWidthValueComponentFocusableText
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.render.helper.element.ElementWidth
import java.awt.Color
import kotlin.math.min

class ElementWidthPlayer(val gameProfile: GameProfile, width: Double) : ElementWidth(width) {

    private val value = object : ValueText(this, gameProfile.name, "", manage = false) {
        override fun onChange(oldText: String?, newText: String) {
            Friends.setAlias(gameProfile, newText.ifEmpty { null })
        }
    }
    val textField = ElementWidthValueComponentFocusableText(value, 1.0F, false)
    private val textFieldAccessor = textField.textFieldWidget as ITextFieldWidget

    private val defaultHeight = FontWrapper.fontHeight() * 1.5 + 2.0
    private var friendTime = 0L

    private val xOffset = 4
    private val yOffset = (this.defaultHeight / 2.0 - FontWrapper.fontHeight() / 2.0 + 1.0).toInt()

    init {
        textFieldAccessor.tarasande_disableSelectionHighlight()

        textField.textFieldWidget.x = xOffset
        textField.textFieldWidget.y = yOffset
    }

    override fun init() {
        textField.init()
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val friended = Friends.isFriend(gameProfile)
        RenderUtil.fill(matrices, 0.0, 0.0, this.width, this.getHeight(), Int.MIN_VALUE)
        if (friended) {
            textField.width = width - 20
            textFieldAccessor.tarasande_setColor(Color.white)
            matrices.push()
            textField.render(matrices, mouseX, mouseY, delta)
            matrices.pop()
            textFieldAccessor.tarasande_setColor(null)
        } else {
            textField.textFieldWidget.isFocused = false
            FontWrapper.textShadow(matrices, gameProfile.name, xOffset.toFloat(), yOffset.toFloat(), -1)
        }

        val toggleAnimation = min((System.currentTimeMillis() - friendTime) / 100.0, 1.0)
        val radius = if (friended) toggleAnimation else 1.0 - toggleAnimation
        RenderUtil.fillCircle(matrices, width - 7, defaultHeight / 2, radius * 4.0, TarasandeValues.accentColor.getColor().rgb)
        RenderUtil.outlinedCircle(matrices, width - 7, defaultHeight / 2, 4.0, 2.0F, RenderUtil.colorInterpolate(TarasandeValues.accentColor.getColor(), Color.white, radius).rgb)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            return if (RenderUtil.isHovered(mouseX, mouseY, 0.0, 0.0, width, getHeight()) && Vec2f(mouseX.toFloat(), mouseY.toFloat()).distanceSquared(Vec2f((width - 7).toFloat(), (defaultHeight / 2).toFloat())) < 16.0F) {
                Friends.changeFriendState(gameProfile)
                textField.textFieldWidget.text = ""
                friendTime = System.currentTimeMillis()
                true
            } else {
                textField.mouseClicked(mouseX, mouseY, button)
            }
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        return textField.mouseReleased((mouseX - xOffset) / 2.0, (mouseY - yOffset) / 2.0, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        return textField.mouseScrolled((mouseX - xOffset) / 2.0, (mouseY - yOffset) / 2.0, amount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return textField.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        textField.charTyped(chr, modifiers)
    }

    override fun tick() {
        textField.tick()
    }

    override fun onClose() {
        textField.onClose()
    }

    override fun getHeight() = defaultHeight
}