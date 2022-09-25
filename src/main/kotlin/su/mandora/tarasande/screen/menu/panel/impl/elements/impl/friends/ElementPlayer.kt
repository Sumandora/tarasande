package su.mandora.tarasande.screen.menu.panel.impl.elements.impl.friends

import com.mojang.authlib.GameProfile
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec2f
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.mixin.accessor.ITextFieldWidget
import su.mandora.tarasande.screen.menu.panel.impl.elements.Element
import su.mandora.tarasande.screen.menu.valuecomponent.ValueComponentText
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueText
import java.awt.Color
import kotlin.math.min

class ElementPlayer(val gameProfile: GameProfile, width: Double) : Element(width) {

    private val value = object : ValueText(this, gameProfile.name, "", manage = false) {
        override fun onChange() {
            TarasandeMain.get().friends.setAlias(gameProfile, value.ifEmpty { null })
        }
    }
    private val textField = ValueComponentText(value)

    private val defaultHeight = MinecraftClient.getInstance().textRenderer.fontHeight * 1.5 + 2.0
    private var friendTime = 0L

    private val xOffset = 4.0
    private val yOffset = this.defaultHeight / 2.0 - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0 + 1.0

    override fun init() {
        textField.init()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val friended = TarasandeMain.get().friends.isFriend(gameProfile)
        RenderUtil.fill(matrices, 0.0, 0.0, this.width, this.getHeight(), Int.MIN_VALUE)
        if (friended) {
            matrices?.push()
            matrices?.translate(xOffset - 1.0f, yOffset - 1.0f, 0.0)
            matrices?.scale(2.0f, 2.0f, 1.0f)
            textField.width = (width - 20) / 2.0
            val accessor = textField.textFieldWidget as ITextFieldWidget
            accessor.tarasande_setColor(Color.white)
            textField.render(matrices, (mouseX - xOffset).toInt(), (mouseY - yOffset).toInt(), delta)
            accessor.tarasande_setColor(null)
            matrices?.pop()
        } else {
            textField.setFocused(false)
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, gameProfile.name, xOffset.toFloat(), yOffset.toFloat(), Color.white.rgb)
        }

        val toggleAnimation = min((System.currentTimeMillis() - friendTime) / 100.0, 1.0)
        val radius = if (friended) toggleAnimation else 1.0 - toggleAnimation
        RenderUtil.fillCircle(matrices, width - 7, defaultHeight / 2, radius * 4.0, TarasandeMain.get().clientValues.accentColor.getColor().rgb)
        RenderUtil.outlinedCircle(matrices, width - 7, defaultHeight / 2, 4.0, 2.0f, RenderUtil.colorInterpolate(TarasandeMain.get().clientValues.accentColor.getColor(), Color.white, radius).rgb)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            return if (RenderUtil.isHovered(mouseX, mouseY, 0.0, 0.0, width, getHeight()) && Vec2f(mouseX.toFloat(), mouseY.toFloat()).distanceSquared(Vec2f((width - 7).toFloat(), (defaultHeight / 2).toFloat())) < 16.0f) {
                TarasandeMain.get().friends.changeFriendState(gameProfile)
                textField.textFieldWidget.text = ""
                friendTime = System.currentTimeMillis()
                true
            } else {
                textField.mouseClicked((mouseX - xOffset) / 2.0, (mouseY - yOffset) / 2.0, button)
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