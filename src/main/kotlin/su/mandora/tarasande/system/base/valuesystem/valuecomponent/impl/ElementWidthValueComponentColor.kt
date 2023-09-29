package su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import su.mandora.tarasande.util.extension.javaruntime.withAlpha
import su.mandora.tarasande.util.extension.minecraft.render.fill
import su.mandora.tarasande.util.extension.minecraft.render.fillHorizontalGradient
import su.mandora.tarasande.util.extension.minecraft.render.fillVerticalGradient
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.render.helper.DragInfo
import java.awt.Color
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class ElementWidthValueComponentColor(value: Value) : ElementWidthValueComponent<ValueColor>(value) {

    private val wheelDragInfo = DragInfo()
    private val rectDragInfo = DragInfo()
    private val alphaDragInfo = DragInfo()
    private var lastWheelClick = 0L

    private val lockToAccentColorText = "Lock to accent color"

    private val pickerHeight = 55.0

    override fun init() {
    }

    // Make sure Accent Color doesn't handle itself
    private fun isAccent() = value == TarasandeValues.accentColor

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val white = Color.white.let { if (value.isEnabled() && !value.locked) it else it.darker().darker() }
        val unblockedWhite = Color.white.let { if (value.isEnabled()) it else it.darker().darker() }
        val black = Color.black.let { if (value.isEnabled()) it else it.darker().darker() }

        val x1 = width - (pickerHeight - 5) / 2.0 - sin(0.75) * ((pickerHeight - 5) / 2.0 - 5)
        val y1 = (pickerHeight - 5) / 2.0 - sin(0.75) * ((pickerHeight - 5) / 2.0 - 5)

        val x2 = width - (pickerHeight - 5) / 2.0 + cos(0.75) * ((pickerHeight - 5) / 2.0 - 5)
        val y2 = (pickerHeight - 5) / 2.0 + cos(0.75) * ((pickerHeight - 5) / 2.0 - 5)

        if (alphaDragInfo.dragging) {
            value.alpha = 1.0 - MathHelper.clamp((mouseY + (mouseY / (pickerHeight - 5) * 2 - 1) * 5) / (pickerHeight - 5), 0.0, 1.0)
        }
        if (rectDragInfo.dragging) {
            value.sat = MathHelper.clamp((mouseX - x1) / (x2 - x1), 0.0, 1.0)
            value.bri = 1.0 - MathHelper.clamp((mouseY - y1) / (y2 - y1), 0.0, 1.0)
        }
        if (wheelDragInfo.dragging) {
            val mousePos = Vec2f(mouseX.toFloat(), mouseY.toFloat())
            val middle = Vec2f((x1 + (x2 - x1) * 0.5).toFloat(), (y1 + (y2 - y1) * 0.5).toFloat())
            val mouseDir = mousePos.add(middle.multiply(-1F)).normalize() // large subtraction
            value.hue = (atan2(mouseDir.y, mouseDir.x) + PI - PI / 2) / (2 * PI)
        }

        FontWrapper.textShadow(context, value.name, 0F, ((pickerHeight - 5) / 2F - FontWrapper.fontHeight() * 0.5F / 2F).toFloat(), white.rgb, scale = 0.5F, offset = 0.5F)

        val matrix4f = context.matrices.peek()?.positionMatrix!!
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }

        val nextHue = if (value.locked) TarasandeValues.accentColor.hue else value.hue
        val hsb = Color.getHSBColor(nextHue.toFloat(), 1F, 1F).let { if (value.isEnabled()) it else it.darker().darker() }
        context.fill(x1, y1, x2, y2, hsb.rgb)
        context.fillHorizontalGradient(x1, y1, x2, y2, Color.white.withAlpha(0).rgb, unblockedWhite.rgb)
        context.fillVerticalGradient(x1, y1, x2, y2, Color.black.withAlpha(0).rgb, black.rgb)

        if (!isAccent()) {
            FontWrapper.textShadow(context,
                lockToAccentColorText,
                (width - FontWrapper.getWidth(lockToAccentColorText) / 2F).toFloat(),
                pickerHeight.toFloat(),
                (if (!value.locked)
                    white
                else if (value.isEnabled())
                    TarasandeValues.accentColor.getColor()
                else
                    TarasandeValues.accentColor.getColor().darker().darker()).rgb,
                scale = 0.5F,
                offset = 0.5F)
        }

        RenderUtil.outlinedFill(context.matrices, x1, y1, x2, y2, 2F, unblockedWhite.rgb)
        RenderUtil.outlinedCircle(context.matrices, x1 + (x2 - x1) * value.sat, y1 + (y2 - y1) * (1.0 - value.bri), 2.0, 2F, unblockedWhite.rgb)
        RenderUtil.fillCircle(context.matrices, x1 + (x2 - x1) * value.sat, y1 + (y2 - y1) * (1.0 - value.bri), 2.0, Color.getHSBColor(nextHue.toFloat(), value.sat.toFloat(), value.bri.toFloat()).let { if (value.isEnabled()) it else it.darker().darker() }.rgb)
        RenderUtil.outlinedCircle(context.matrices, width - (pickerHeight - 5) / 2.0, (pickerHeight - 5) / 2.0, (pickerHeight - 5) / 2.0 - 5, 2F, white.rgb)
        RenderUtil.outlinedCircle(context.matrices, width - (pickerHeight - 5) / 2.0, (pickerHeight - 5) / 2.0, (pickerHeight - 5) / 2.0, 2F, white.rgb)

        val innerRadius = (pickerHeight - 5) / 2.0 - 5
        val outerRadius = (pickerHeight - 5) / 2.0
        val middleRadius = innerRadius + (outerRadius - innerRadius) * 0.5
        val width = outerRadius - innerRadius
        val cull = GL11.glIsEnabled(GL11.GL_CULL_FACE)
        RenderSystem.disableCull()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR)
        run {
            var circle = 0.0
            while (circle <= 1.01) {
                var hsb2 = Color.getHSBColor((circle.toFloat() + 0.5F) % 1F, 1F, 1F)
                if (!value.isEnabled() || value.locked) hsb2 = hsb2.darker().darker()
                val colors = RenderUtil.colorToRGBA(hsb2.rgb)
                bufferBuilder.vertex(matrix4f, (this.width - (pickerHeight - 5) / 2.0 - sin(circle * PI * 2) * innerRadius).toFloat(), ((pickerHeight - 5) / 2.0 + cos(circle * PI * 2) * innerRadius).toFloat(), 0F).color(colors[0], colors[1], colors[2], colors[3]).next()
                bufferBuilder.vertex(matrix4f, (this.width - (pickerHeight - 5) / 2.0 - sin(circle * PI * 2) * outerRadius).toFloat(), ((pickerHeight - 5) / 2.0 + cos(circle * PI * 2) * outerRadius).toFloat(), 0F).color(colors[0], colors[1], colors[2], colors[3]).next()
                circle += 0.01
            }
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        if(cull)
            RenderSystem.enableCull()

        RenderUtil.outlinedCircle(context.matrices, this.width - (pickerHeight - 5) / 2.0 - sin((value.hue + 0.5F) * PI * 2) * middleRadius, (pickerHeight - 5) / 2.0 + cos((value.hue + 0.5F) * PI * 2) * middleRadius, width / 2.0, 3F, white.rgb)
        RenderUtil.fillCircle(context.matrices, this.width - (pickerHeight - 5) / 2.0 - sin((value.hue + 0.5F) * PI * 2) * middleRadius, (pickerHeight - 5) / 2.0 + cos((value.hue + 0.5F) * PI * 2) * middleRadius, width / 2.0, Color.getHSBColor(value.hue.toFloat(), 1F, 1F).let { if (value.isEnabled() && !value.locked) it else it.darker().darker() }.rgb)
        if (value.alpha != null) {
            val alpha = value.alpha!!
            context.fillVerticalGradient(this.width - (pickerHeight - 5) - 10, 0.0, this.width - (pickerHeight - 5) - 5, pickerHeight - 5, unblockedWhite.rgb, black.rgb)
            RenderUtil.outlinedFill(context.matrices, this.width - (pickerHeight - 5) - 10, 0.0, this.width - (pickerHeight - 5) - 5, pickerHeight - 5, 2F, unblockedWhite.rgb)
            RenderUtil.fillCircle(context.matrices, this.width - (pickerHeight - 5) - 7.5, (pickerHeight - 5) * (1.0 - alpha) + (alpha * 2 - 1) * 2.5, 2.5, Color(alpha.toFloat(), alpha.toFloat(), alpha.toFloat()).let { if (value.isEnabled()) it else it.darker().darker() }.rgb)
            RenderUtil.outlinedCircle(context.matrices, this.width - (pickerHeight - 5) - 7.5, (pickerHeight - 5) * (1.0 - alpha) + (alpha * 2 - 1) * 2.5, 2.5, 2F, unblockedWhite.rgb)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!RenderUtil.isHovered(mouseX, mouseY, 0.0, 0.0, width, getHeight())) return false

        if (!this.isAccent() && RenderUtil.isHovered(mouseX, mouseY, width - FontWrapper.getWidth(lockToAccentColorText) / 2F, pickerHeight, width, pickerHeight + FontWrapper.fontHeight() / 2F)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                val accent = TarasandeValues.accentColor

                value.hue = accent.hue
                value.bri = accent.bri
                value.sat = accent.sat
                return true
            } else {
                value.locked = !value.locked
            }
        }

        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false

        if (value.alpha != null && RenderUtil.isHovered(mouseX, mouseY, width - (pickerHeight - 5) - 10, 0.0, width - (pickerHeight - 5) - 5, pickerHeight - 5)) {
            alphaDragInfo.setDragInfo(true, mouseX, mouseY)
            return true
        }

        val x1 = width - (pickerHeight - 5) / 2.0 - sin(0.75) * ((pickerHeight - 5) / 2.0 - 5)
        val y1 = (pickerHeight - 5) / 2.0 - sin(0.75) * ((pickerHeight - 5) / 2.0 - 5)
        val x2 = width - (pickerHeight - 5) / 2.0 + cos(0.75) * ((pickerHeight - 5) / 2.0 - 5)
        val y2 = (pickerHeight - 5) / 2.0 + cos(0.75) * ((pickerHeight - 5) / 2.0 - 5)
        if (RenderUtil.isHovered(mouseX, mouseY, x1, y1, x2, y2)) {
            rectDragInfo.setDragInfo(true, mouseX, mouseY)
            return true
        }

        if (!value.locked) {
            val innerRadius = (pickerHeight - 5) / 2.0 - 5
            val outerRadius = (pickerHeight - 5) / 2.0
            if (Vec2f(mouseX.toFloat(), mouseY.toFloat()).distanceSquared(Vec2f((x1 + (x2 - x1) * 0.5).toFloat(), (y1 + (y2 - y1) * 0.5).toFloat())) in (innerRadius * innerRadius)..(outerRadius * outerRadius) && !value.locked) {
                if (System.currentTimeMillis() - lastWheelClick < 250L) {
                    value.rainbow = !value.rainbow
                } else {
                    value.rainbow = false
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

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double) = false

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

    override fun getHeight() = pickerHeight + if (!isAccent()) FontWrapper.fontHeight() else 0
}
