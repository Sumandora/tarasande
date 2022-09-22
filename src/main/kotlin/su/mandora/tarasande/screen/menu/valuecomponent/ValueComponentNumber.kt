package su.mandora.tarasande.screen.menu.valuecomponent

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.valuecomponent.ValueComponent
import su.mandora.tarasande.base.value.Value
import su.mandora.tarasande.screen.menu.utils.DragInfo
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueNumber
import java.awt.Color
import java.lang.Double.parseDouble
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.min

class ValueComponentNumber(value: Value) : ValueComponent(value) {

    private val dragInfo = DragInfo()

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val valueNumber = value as ValueNumber

        matrices?.push()
        matrices?.translate(0.0, getHeight() / 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        matrices?.translate(0.0, -getHeight() / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.name, 0.0F, (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), Color.white.let { if (valueNumber.isEnabled()) it else it.darker().darker() }.rgb)
        matrices?.pop()

        if (dragInfo.dragging) {
            val mousePos = mouseX - (width - 50)
            val increment = BigDecimal(valueNumber.increment)
            // hacky
            val string = BigDecimal(valueNumber.min).add(((BigDecimal(mousePos).divide(BigDecimal(50.0))).multiply(BigDecimal(valueNumber.max).subtract(BigDecimal(valueNumber.min))))).divide(increment, 0, RoundingMode.HALF_UP).multiply(increment).toPlainString()
            // even more hacky
            valueNumber.value = MathHelper.clamp(parseDouble(string.substring(0..min(string.length - 1, 7))), valueNumber.min, valueNumber.max)
            if (valueNumber.value == -0.0) valueNumber.value = 0.0 // bruh
            valueNumber.onChange()
        }

        val sliderPos = (valueNumber.value - valueNumber.min) / (valueNumber.max - valueNumber.min)

        var accentColor = TarasandeMain.get().clientValues.accentColor.getColor()
        var color = Color(accentColor.red, accentColor.green, accentColor.blue, 255 / 4)
        var otherColor = Color(255, 255, 255, 255 / 4)

        if (!value.isEnabled()) {
            accentColor = accentColor.darker().darker()
            color = color.darker().darker()
            otherColor = otherColor.darker().darker()
        }

        RenderUtil.fillHorizontalGradient(matrices, width - 50, getHeight() * 0.25, width - (1.0 - sliderPos) * 50, getHeight() * 0.75, otherColor.rgb, color.rgb)
        RenderUtil.outlinedHorizontalGradient(matrices, width - 50, getHeight() * 0.25, width, getHeight() * 0.75, 2.0F, Color.white.let { if (valueNumber.isEnabled()) it else it.darker().darker() }.rgb, accentColor.rgb)

        matrices?.push()
        matrices?.translate(width - 50 / 2, getHeight() / 2.0, 0.0)
        matrices?.scale(0.5F, 0.5F, 1.0F)
        matrices?.translate(-(width - 50 / 2), -getHeight() / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.value.toString(), (width - 50 / 2.0F - MinecraftClient.getInstance().textRenderer.getWidth(value.value.toString()) / 2.0F).toFloat(), (getHeight() / 2.0F - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0F).toFloat(), Color.white.let { if (valueNumber.isEnabled()) it else it.darker().darker() }.rgb)
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

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) = false

    override fun charTyped(chr: Char, modifiers: Int) {
    }

    override fun tick() {
    }

    override fun onClose() {
        dragInfo.setDragInfo(false, 0.0, 0.0)
    }

    override fun getHeight() = MinecraftClient.getInstance().textRenderer.fontHeight * 2.0
}