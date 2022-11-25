package net.tarasandedevelopment.tarasande.feature.friend.panel

import com.mojang.authlib.GameProfile
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec2f
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.mixin.accessor.ITextFieldWidget
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentText
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.render.helper.element.ElementWidth
import java.awt.Color
import kotlin.math.min

class ElementWidthPlayer(val gameProfile: GameProfile, width: Double) : ElementWidth(width) {

    private val value = object : ValueText(this, gameProfile.name, "", manage = false) {
        override fun onChange() {
            TarasandeMain.friends().setAlias(gameProfile, value.ifEmpty { null })
        }
    }
    val textField = ElementWidthValueComponentText(value, 1.0f, false)

    private val defaultHeight = FontWrapper.fontHeight() * 1.5 + 2.0
    private var friendTime = 0L

    private val xOffset = 4
    private val yOffset = (this.defaultHeight / 2.0 - FontWrapper.fontHeight() / 2.0 + 1.0).toInt()

    init {
        textField.textFieldWidget.x = xOffset
        textField.textFieldWidget.y = yOffset
    }

    override fun init() {
        textField.init()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val friended = TarasandeMain.friends().isFriend(gameProfile)
        RenderUtil.fill(matrices, 0.0, 0.0, this.width, this.getHeight(), Int.MIN_VALUE)
        if (friended) {
            textField.width = width - 20
            val accessor = textField.textFieldWidget as ITextFieldWidget
            accessor.tarasande_setColor(Color.white)
            matrices?.push()
            textField.render(matrices, mouseX, mouseY, delta)
            matrices?.pop()
            accessor.tarasande_setColor(null)
        } else {
            textField.setFocused(false)
            FontWrapper.textShadow(matrices, gameProfile.name, xOffset.toFloat(), yOffset.toFloat(), -1)
        }

        val toggleAnimation = min((System.currentTimeMillis() - friendTime) / 100.0, 1.0)
        val radius = if (friended) toggleAnimation else 1.0 - toggleAnimation
        RenderUtil.fillCircle(matrices, width - 7, defaultHeight / 2, radius * 4.0, TarasandeMain.clientValues().accentColor.getColor().rgb)
        RenderUtil.outlinedCircle(matrices, width - 7, defaultHeight / 2, 4.0, 2.0f, RenderUtil.colorInterpolate(TarasandeMain.clientValues().accentColor.getColor(), Color.white, radius).rgb)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            return if (RenderUtil.isHovered(mouseX, mouseY, 0.0, 0.0, width, getHeight()) && Vec2f(mouseX.toFloat(), mouseY.toFloat()).distanceSquared(Vec2f((width - 7).toFloat(), (defaultHeight / 2).toFloat())) < 16.0f) {
                TarasandeMain.friends().changeFriendState(gameProfile)
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