package net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.impl

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueColor
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.util.extension.withAlpha
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.render.helper.DragInfo
import org.lwjgl.glfw.GLFW
import java.awt.Color
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class ElementValueComponentColor(value: Value) : ElementValueComponent(value) {

    private val wheelDragInfo = DragInfo()
    private val rectDragInfo = DragInfo()
    private val alphaDragInfo = DragInfo()
    private var lastWheelClick = 0L

    private val lockToAccentColorText = "Lock to accent color"

    override fun init() {
    }

    // Make sure Accent Color doesn't handle itself
    private fun isAccent() = value == TarasandeMain.instance.clientValues.accentColor

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val valueColor = value as ValueColor
        val white = Color.white.let { if (valueColor.isEnabled() && !valueColor.locked) it else it.darker().darker() }
        val unblockedWhite = Color.white.let { if (valueColor.isEnabled()) it else it.darker().darker() }
        val black = Color.black.let { if (valueColor.isEnabled()) it else it.darker().darker() }

        val x1 = width - (getPickerHeight() - 5) / 2.0 - sin(0.75) * ((getPickerHeight() - 5) / 2.0 - 5)
        val y1 = (getPickerHeight() - 5) / 2.0 - sin(0.75) * ((getPickerHeight() - 5) / 2.0 - 5)

        val x2 = width - (getPickerHeight() - 5) / 2.0 + cos(0.75) * ((getPickerHeight() - 5) / 2.0 - 5)
        val y2 = (getPickerHeight() - 5) / 2.0 + cos(0.75) * ((getPickerHeight() - 5) / 2.0 - 5)

        if (alphaDragInfo.dragging) {
            valueColor.alpha = 1.0f - MathHelper.clamp((mouseY + (mouseY / (getPickerHeight() - 5) * 2 - 1) * 5) / (getPickerHeight() - 5), 0.0, 1.0).toFloat()
            valueColor.onChange()
        }
        if (rectDragInfo.dragging) {
            valueColor.sat = MathHelper.clamp((mouseX - x1) / (x2 - x1), 0.0, 1.0).toFloat()
            valueColor.bri = 1.0f - MathHelper.clamp((mouseY - y1) / (y2 - y1), 0.0, 1.0).toFloat()
            valueColor.onChange()
        }
        if (wheelDragInfo.dragging) {
            val mousePos = Vec2f(mouseX.toFloat(), mouseY.toFloat())
            val middle = Vec2f((x1 + (x2 - x1) * 0.5).toFloat(), (y1 + (y2 - y1) * 0.5).toFloat())
            val mouseDir = mousePos.add(middle.multiply(-1.0f)).normalize() // large subtraction
            valueColor.hue = ((atan2(mouseDir.y, mouseDir.x) + PI - PI / 2) / (2 * PI)).toFloat()
            valueColor.onChange()
        }

        FontWrapper.textShadow(matrices, value.name, 0.0f, ((getPickerHeight() - 5) / 2.0f - FontWrapper.fontHeight() / 2.0f).toFloat(), white.rgb, scale = 0.5f, offset = 0.5F)

        val matrix4f = matrices?.peek()?.positionMatrix!!
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }

        val nextHue = if (valueColor.locked) TarasandeMain.instance.clientValues.accentColor.hue else valueColor.hue
        val hsb = Color.getHSBColor(nextHue, 1.0f, 1.0f).let { if (valueColor.isEnabled()) it else it.darker().darker() }
        RenderUtil.fill(matrices, x1, y1, x2, y2, hsb.rgb)
        RenderUtil.fillHorizontalGradient(matrices, x1, y1, x2, y2, Color.white.withAlpha(0).rgb, unblockedWhite.rgb)
        RenderUtil.fillVerticalGradient(matrices, x1, y1, x2, y2, Color.black.withAlpha(0).rgb, black.rgb)

        if (!isAccent()) {
            FontWrapper.textShadow(matrices,
                lockToAccentColorText,
                (width - FontWrapper.getWidth(lockToAccentColorText) / 2f).toFloat(),
                getPickerHeight().toFloat(),
                (if (!valueColor.locked)
                    white
                else if (valueColor.isEnabled())
                    TarasandeMain.instance.clientValues.accentColor.getColor()
                else
                    TarasandeMain.instance.clientValues.accentColor.getColor().darker().darker()).rgb,
                scale = 0.5F,
                offset = 0.5F)
        }

        RenderUtil.outlinedFill(matrices, x1, y1, x2, y2, 2.0f, unblockedWhite.rgb)
        RenderUtil.outlinedCircle(matrices, x1 + (x2 - x1) * valueColor.sat, y1 + (y2 - y1) * (1.0 - valueColor.bri), 2.0, 2.0f, unblockedWhite.rgb)
        RenderUtil.fillCircle(matrices, x1 + (x2 - x1) * valueColor.sat, y1 + (y2 - y1) * (1.0 - valueColor.bri), 2.0, Color.getHSBColor(nextHue, valueColor.sat, valueColor.bri).let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
        RenderUtil.outlinedCircle(matrices, width - (getPickerHeight() - 5) / 2.0, (getPickerHeight() - 5) / 2.0, (getPickerHeight() - 5) / 2.0 - 5, 2.0f, white.rgb)
        RenderUtil.outlinedCircle(matrices, width - (getPickerHeight() - 5) / 2.0, (getPickerHeight() - 5) / 2.0, (getPickerHeight() - 5) / 2.0, 2.0f, white.rgb)

        val innerRadius = (getPickerHeight() - 5) / 2.0 - 5
        val outerRadius = (getPickerHeight() - 5) / 2.0
        val middleRadius = innerRadius + (outerRadius - innerRadius) * 0.5
        val width = outerRadius - innerRadius
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR)
        run {
            var circle = 0.0
            while (circle <= 1.01) {
                var hsb2 = Color.getHSBColor((circle.toFloat() + 0.5f) % 1.0f, 1.0f, 1.0f)
                if (!valueColor.isEnabled() || valueColor.locked) hsb2 = hsb2.darker().darker()
                val f2 = (hsb2.rgb shr 24 and 0xFF) / 255.0f
                val g2 = (hsb2.rgb shr 16 and 0xFF) / 255.0f
                val h2 = (hsb2.rgb shr 8 and 0xFF) / 255.0f
                val i = (hsb2.rgb and 0xFF) / 255.0f
                bufferBuilder.vertex(matrix4f, (this.width - (getPickerHeight() - 5) / 2.0 - sin(circle * PI * 2) * innerRadius).toFloat(), ((getPickerHeight() - 5) / 2.0 + cos(circle * PI * 2) * innerRadius).toFloat(), 0.0f).color(g2, h2, i, f2).next()
                bufferBuilder.vertex(matrix4f, (this.width - (getPickerHeight() - 5) / 2.0 - sin(circle * PI * 2) * outerRadius).toFloat(), ((getPickerHeight() - 5) / 2.0 + cos(circle * PI * 2) * outerRadius).toFloat(), 0.0f).color(g2, h2, i, f2).next()
                circle += 0.01
            }
        }
        BufferRenderer.drawWithShader(bufferBuilder.end())

        RenderUtil.outlinedCircle(matrices, this.width - (getPickerHeight() - 5) / 2.0 - sin((valueColor.hue + 0.5f) * PI * 2) * middleRadius, (getPickerHeight() - 5) / 2.0 + cos((valueColor.hue + 0.5f) * PI * 2) * middleRadius, width / 2.0, 3.0f, white.rgb)
        RenderUtil.fillCircle(matrices, this.width - (getPickerHeight() - 5) / 2.0 - sin((valueColor.hue + 0.5f) * PI * 2) * middleRadius, (getPickerHeight() - 5) / 2.0 + cos((valueColor.hue + 0.5f) * PI * 2) * middleRadius, width / 2.0, Color.getHSBColor(valueColor.hue, 1.0f, 1.0f).let { if (valueColor.isEnabled() && !valueColor.locked) it else it.darker().darker() }.rgb)
        if (valueColor.alpha != null) {
            val alpha = valueColor.alpha!!
            RenderUtil.fillVerticalGradient(matrices, this.width - (getPickerHeight() - 5) - 10, 0.0, this.width - (getPickerHeight() - 5) - 5, getPickerHeight() - 5, unblockedWhite.rgb, black.rgb)
            RenderUtil.outlinedFill(matrices, this.width - (getPickerHeight() - 5) - 10, 0.0, this.width - (getPickerHeight() - 5) - 5, getPickerHeight() - 5, 2.0f, unblockedWhite.rgb)
            RenderUtil.fillCircle(matrices, this.width - (getPickerHeight() - 5) - 7.5, (getPickerHeight() - 5) * (1.0 - alpha) + (alpha * 2 - 1) * 2.5, 2.5, Color(alpha, alpha, alpha).let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
            RenderUtil.outlinedCircle(matrices, this.width - (getPickerHeight() - 5) - 7.5, (getPickerHeight() - 5) * (1.0 - alpha) + (alpha * 2 - 1) * 2.5, 2.5, 2.0f, unblockedWhite.rgb)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!RenderUtil.isHovered(mouseX, mouseY, 0.0, 0.0, width, getHeight())) return false

        val valueColor = value as ValueColor

        if (!this.isAccent() && RenderUtil.isHovered(mouseX, mouseY, width - FontWrapper.getWidth(lockToAccentColorText) / 2.0f, getPickerHeight(), width, getPickerHeight() + FontWrapper.fontHeight() / 2.0f)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                val accent = TarasandeMain.instance.clientValues.accentColor

                valueColor.hue = accent.hue
                valueColor.bri = accent.bri
                valueColor.sat = accent.sat
                return true
            } else {
                valueColor.locked = !valueColor.locked
            }
        }

        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false

        if (valueColor.alpha != null && RenderUtil.isHovered(mouseX, mouseY, width - (getPickerHeight() - 5) - 10, 0.0, width - (getPickerHeight() - 5) - 5, getPickerHeight() - 5)) {
            alphaDragInfo.setDragInfo(true, mouseX, mouseY)
            return true
        }

        val x1 = width - (getPickerHeight() - 5) / 2.0 - sin(0.75) * ((getPickerHeight() - 5) / 2.0 - 5)
        val y1 = (getPickerHeight() - 5) / 2.0 - sin(0.75) * ((getPickerHeight() - 5) / 2.0 - 5)
        val x2 = width - (getPickerHeight() - 5) / 2.0 + cos(0.75) * ((getPickerHeight() - 5) / 2.0 - 5)
        val y2 = (getPickerHeight() - 5) / 2.0 + cos(0.75) * ((getPickerHeight() - 5) / 2.0 - 5)
        if (RenderUtil.isHovered(mouseX, mouseY, x1, y1, x2, y2)) {
            rectDragInfo.setDragInfo(true, mouseX, mouseY)
            return true
        }

        if (!valueColor.locked) {
            val innerRadius = (getPickerHeight() - 5) / 2.0 - 5
            val outerRadius = (getPickerHeight() - 5) / 2.0
            if (Vec2f(mouseX.toFloat(), mouseY.toFloat()).distanceSquared(Vec2f((x1 + (x2 - x1) * 0.5).toFloat(), (y1 + (y2 - y1) * 0.5).toFloat())) in (innerRadius * innerRadius)..(outerRadius * outerRadius) && !valueColor.locked) {
                if (System.currentTimeMillis() - lastWheelClick < 250L) {
                    valueColor.rainbow = !valueColor.rainbow
                } else {
                    valueColor.rainbow = false
                }
                wheelDragInfo.setDragInfo(true, mouseX, mouseY)
                lastWheelClick = System.currentTimeMillis()
                return true
            }
        }
        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        this.wheelDragInfo.setDragInfo(false, 0.0, 0.0)
        this.rectDragInfo.setDragInfo(false, 0.0, 0.0)
        this.alphaDragInfo.setDragInfo(false, 0.0, 0.0)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double) = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) = false

    override fun charTyped(chr: Char, modifiers: Int) {
    }

    override fun tick() {
    }

    override fun onClose() {
        this.wheelDragInfo.setDragInfo(false, 0.0, 0.0)
        this.rectDragInfo.setDragInfo(false, 0.0, 0.0)
        this.alphaDragInfo.setDragInfo(false, 0.0, 0.0)
    }

    private fun getPickerHeight() = 55.0
    override fun getHeight() = getPickerHeight() + if (!isAccent()) FontWrapper.fontHeight() else 0
}
