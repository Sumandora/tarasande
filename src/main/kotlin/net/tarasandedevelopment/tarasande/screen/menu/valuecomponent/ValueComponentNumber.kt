package net.tarasandedevelopment.tarasande.screen.menu.valuecomponent

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f
import org.lwjgl.glfw.GLFW
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.menu.valuecomponent.ValueComponent
import net.tarasandedevelopment.tarasande.base.value.Value
import net.tarasandedevelopment.tarasande.screen.menu.utils.DragInfo
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.awt.Color
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.roundToInt

class ValueComponentNumber(value: Value) : ValueComponent(value) {

    private val dragInfo = DragInfo()

    private var lastMousePos: Vec2f? = null

    private fun setValue(value: Double, clamp: Boolean) {
        val valueNumber = this.value as ValueNumber
        var value = (value / valueNumber.increment).roundToInt() * valueNumber.increment

        val sevenDecimalPlaces = BigDecimal(10.0).pow(7)
        value = BigDecimal(value).multiply(sevenDecimalPlaces).round(MathContext.DECIMAL32).divide(sevenDecimalPlaces).toDouble()

        if (clamp)
            value = MathHelper.clamp(value, valueNumber.min, valueNumber.max)

        if (value == -0.0)
            value = 0.0

        valueNumber.value = value
        valueNumber.onChange()
    }

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        lastMousePos = Vec2f(mouseX.toFloat(), mouseY.toFloat())
        val valueNumber = value as ValueNumber

        if (dragInfo.dragging) {
            val mousePos = mouseX - (width - 50)
            val value = valueNumber.min + mousePos / 50.0 * (valueNumber.max - valueNumber.min)
            setValue(value, !Screen.hasShiftDown())
        }

        val sliderPos = MathHelper.clamp((valueNumber.value - valueNumber.min) / (valueNumber.max - valueNumber.min), 0.0, 1.0)

        var white = Color.white
        var accentColor = TarasandeMain.get().clientValues.accentColor.getColor()
        var color = Color(accentColor.red, accentColor.green, accentColor.blue, 255 / 4)
        var otherColor = Color(255, 255, 255, 255 / 4)

        if (!value.isEnabled()) {
            white = white.darker().darker()
            accentColor = accentColor.darker().darker()
            color = color.darker().darker()
            otherColor = otherColor.darker().darker()
        }

        matrices?.push()
        matrices?.translate(0.0, getHeight() / 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        matrices?.translate(0.0, -getHeight() / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.name, 0.0F, (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), white.rgb)
        matrices?.pop()

        RenderUtil.fillHorizontalGradient(matrices, width - 50, getHeight() * 0.25, width - (1.0 - sliderPos) * 50, getHeight() * 0.75, otherColor.rgb, color.rgb)
        RenderUtil.outlinedHorizontalGradient(matrices, width - 50, getHeight() * 0.25, width, getHeight() * 0.75, 2.0F, white.rgb, accentColor.rgb)

        matrices?.push()
        matrices?.translate(width - 50 / 2, getHeight() / 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        matrices?.translate(-(width - 50 / 2), -getHeight() / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices,
            (
                    if (value.value !in valueNumber.min..valueNumber.max)
                        (if (value.isEnabled()) {
                            Formatting.RED
                        } else {
                            Formatting.DARK_RED
                        }).toString()
                    else
                        ""
                    ) + value.value.toString(),
            (width - 50 / 2.0F - MinecraftClient.getInstance().textRenderer.getWidth(value.value.toString()) / 2.0F).toFloat(),
            (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(),
            white.rgb
        )
        matrices?.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && RenderUtil.isHovered(mouseX, mouseY, width - 50, getHeight() * 0.25, width, getHeight() * 0.75)) {
            dragInfo.setDragInfo(true, mouseX - (width - 50), mouseY - getHeight() * 0.25)
            return true
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        dragInfo.setDragInfo(false, 0.0, 0.0)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (lastMousePos != null && RenderUtil.isHovered(lastMousePos?.x?.toDouble()!!, lastMousePos?.y?.toDouble()!!, width - 50, getHeight() * 0.25, width, getHeight() * 0.75)) {
            val valueNumber = value as ValueNumber
            val increment = valueNumber.increment *
                    when (keyCode) {
                        GLFW.GLFW_KEY_LEFT -> -1
                        GLFW.GLFW_KEY_RIGHT -> 1
                        else -> 0
                    }
            setValue(valueNumber.value + increment, false)
            return keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT
        }
        return false
    }

    override fun charTyped(chr: Char, modifiers: Int) {
    }

    override fun tick() {
    }

    override fun onClose() {
        dragInfo.setDragInfo(false, 0.0, 0.0)
    }

    override fun getHeight() = MinecraftClient.getInstance().textRenderer.fontHeight * 2.0
}