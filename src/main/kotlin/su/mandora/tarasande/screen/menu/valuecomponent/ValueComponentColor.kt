package su.mandora.tarasande.screen.menu.valuecomponent

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f
import su.mandora.tarasande.base.screen.menu.valuecomponent.ValueComponent
import su.mandora.tarasande.base.value.Value
import su.mandora.tarasande.screen.menu.utils.DragInfo
import su.mandora.tarasande.util.render.RenderUtil.fillCircle
import su.mandora.tarasande.util.render.RenderUtil.fillVerticalGradient
import su.mandora.tarasande.util.render.RenderUtil.isHovered
import su.mandora.tarasande.util.render.RenderUtil.outlinedCircle
import su.mandora.tarasande.util.render.RenderUtil.outlinedFill
import su.mandora.tarasande.value.ValueColor
import java.awt.Color
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class ValueComponentColor(value: Value) : ValueComponent(value) {

    private val wheelDragInfo = DragInfo()
    private val rectDragInfo = DragInfo()
    private val alphaDragInfo = DragInfo()
    private var lastWheelClick = 0L

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val valueColor = value as ValueColor

        val x1 = width - (getHeight() - 5) / 2.0 - sin(0.75) * ((getHeight() - 5) / 2.0 - 5)
        val y1 = (getHeight() - 5) / 2.0 - sin(0.75) * ((getHeight() - 5) / 2.0 - 5)
        val x2 = width - (getHeight() - 5) / 2.0 + cos(0.75) * ((getHeight() - 5) / 2.0 - 5)
        val y2 = (getHeight() - 5) / 2.0 + cos(0.75) * ((getHeight() - 5) / 2.0 - 5)

        if (alphaDragInfo.dragging) {
            valueColor.alpha = 1.0f - MathHelper.clamp((mouseY + (mouseY / (getHeight() - 5) * 2 - 1) * 5) / (getHeight() - 5), 0.0, 1.0).toFloat()
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

        matrices?.push()
        matrices?.translate(0.0, (getHeight() - 5) / 2.0, 0.0)
        matrices?.scale(0.5f, 0.5f, 1.0f)
        matrices?.translate(0.0, -(getHeight() - 5) / 2.0, 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, value.name, 0.0f, ((getHeight() - 5) / 2.0f - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0f).toFloat(), Color.white.let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
        matrices?.pop()
        val matrix4f = matrices?.peek()?.positionMatrix!!
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        val precision = 0.01
        val tileWidth = (x2 - x1) * precision
        val tileHeight = (y2 - y1) * precision
        run {
            var s = 0.0
            while (s <= 1.0) {
                var b = 0.0
                while (b <= 1.0) {
                    var hsb = Color.getHSBColor(valueColor.hue, s.toFloat(), b.toFloat())
                    if (!valueColor.isEnabled()) hsb = hsb.darker().darker()
                    val f = (hsb.rgb shr 24 and 0xFF) / 255.0f
                    val g = (hsb.rgb shr 16 and 0xFF) / 255.0f
                    val h = (hsb.rgb shr 8 and 0xFF) / 255.0f
                    val k = (hsb.rgb and 0xFF) / 255.0f
                    val tileX1 = x1 + (x2 - x1) * s
                    val tileY1 = y1 + (y2 - y1) * (1.0 - b) - tileHeight
                    val tileX2 = x1 + (x2 - x1) * s + tileWidth
                    val tileY2 = y1 + (y2 - y1) * (1.0 - b)
                    bufferBuilder.vertex(matrix4f, tileX1.toFloat(), tileY1.toFloat(), 0.0f).color(g, h, k, f).next()
                    bufferBuilder.vertex(matrix4f, tileX1.toFloat(), tileY2.toFloat(), 0.0f).color(g, h, k, f).next()
                    bufferBuilder.vertex(matrix4f, tileX2.toFloat(), tileY2.toFloat(), 0.0f).color(g, h, k, f).next()
                    bufferBuilder.vertex(matrix4f, tileX2.toFloat(), tileY1.toFloat(), 0.0f).color(g, h, k, f).next()
                    b += precision
                }
                s += precision
            }
        }
        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
        outlinedFill(matrices, x1, y1, x2, y2, 2.0f, Color.white.let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
        outlinedCircle(matrices, x1 + (x2 - x1) * valueColor.sat, y1 + (y2 - y1) * (1.0 - valueColor.bri), 2.0, 2.0f, Color.white.let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
        fillCircle(matrices, x1 + (x2 - x1) * valueColor.sat, y1 + (y2 - y1) * (1.0 - valueColor.bri), 2.0, Color.getHSBColor(valueColor.hue, valueColor.sat, valueColor.bri).let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
        outlinedCircle(matrices, width - (getHeight() - 5) / 2.0, (getHeight() - 5) / 2.0, (getHeight() - 5) / 2.0 - 5, 2.0f, Color.white.let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
        outlinedCircle(matrices, width - (getHeight() - 5) / 2.0, (getHeight() - 5) / 2.0, (getHeight() - 5) / 2.0, 2.0f, Color.white.let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
        val innerRadius = (getHeight() - 5) / 2.0 - 5
        val outerRadius = (getHeight() - 5) / 2.0
        val middleRadius = innerRadius + (outerRadius - innerRadius) * 0.5
        val width = outerRadius - innerRadius
        RenderSystem.disableCull()
        RenderSystem.disableDepthTest()
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR)
        run {
            var circle = 0.0
            while (circle <= 1.1) {
                var hsb2 = Color.getHSBColor((circle.toFloat() + 0.5f) % 1.0f, 1.0f, 1.0f)
                if (!valueColor.isEnabled()) hsb2 = hsb2.darker().darker()
                val f2 = (hsb2.rgb shr 24 and 0xFF) / 255.0f
                val g2 = (hsb2.rgb shr 16 and 0xFF) / 255.0f
                val h2 = (hsb2.rgb shr 8 and 0xFF) / 255.0f
                val i = (hsb2.rgb and 0xFF) / 255.0f
                bufferBuilder.vertex(matrix4f, (this.width - (getHeight() - 5) / 2.0 - sin(circle * PI * 2) * innerRadius).toFloat(), ((getHeight() - 5) / 2.0 + cos(circle * PI * 2) * innerRadius).toFloat(), 0.0f).color(g2, h2, i, f2).next()
                bufferBuilder.vertex(matrix4f, (this.width - (getHeight() - 5) / 2.0 - sin(circle * PI * 2) * outerRadius).toFloat(), ((getHeight() - 5) / 2.0 + cos(circle * PI * 2) * outerRadius).toFloat(), 0.0f).color(g2, h2, i, f2).next()
                circle += 0.01
            }
        }
        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
        outlinedCircle(matrices, this.width - (getHeight() - 5) / 2.0 - sin((valueColor.hue + 0.5f) * PI * 2) * middleRadius, (getHeight() - 5) / 2.0 + cos((valueColor.hue + 0.5f) * PI * 2) * middleRadius, width / 2.0, 3.0f, Color.white.let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
        fillCircle(matrices, this.width - (getHeight() - 5) / 2.0 - sin((valueColor.hue + 0.5f) * PI * 2) * middleRadius, (getHeight() - 5) / 2.0 + cos((valueColor.hue + 0.5f) * PI * 2) * middleRadius, width / 2.0, Color.getHSBColor(valueColor.hue, 1.0f, 1.0f).let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
        if (valueColor.alpha != null) {
            val alpha = valueColor.alpha!!
            fillVerticalGradient(matrices, this.width - (getHeight() - 5) - 10, 0.0, this.width - (getHeight() - 5) - 5, getHeight() - 5, Color.white.let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb, Color.black.let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
            outlinedFill(matrices, this.width - (getHeight() - 5) - 10, 0.0, this.width - (getHeight() - 5) - 5, getHeight() - 5, 2.0f, Color.white.let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
            fillCircle(matrices, this.width - (getHeight() - 5) - 7.5, (getHeight() - 5) * (1.0 - alpha) + (alpha * 2 - 1) * 2.5, 2.5, Color(alpha, alpha, alpha).let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
            outlinedCircle(matrices, this.width - (getHeight() - 5) - 7.5, (getHeight() - 5) * (1.0 - alpha) + (alpha * 2 - 1) * 2.5, 2.5, 2.0f, Color.white.let { if (valueColor.isEnabled()) it else it.darker().darker() }.rgb)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return false
        if ((value as ValueColor).alpha != -1.0f && isHovered(mouseX, mouseY, width - (getHeight() - 5) - 10, 0.0, width - (getHeight() - 5) - 5, getHeight() - 5)) {
            alphaDragInfo.setDragInfo(true, mouseX, mouseY)
            return true
        }
        val x1 = width - (getHeight() - 5) / 2.0 - sin(0.75) * ((getHeight() - 5) / 2.0 - 5)
        val y1 = (getHeight() - 5) / 2.0 - sin(0.75) * ((getHeight() - 5) / 2.0 - 5)
        val x2 = width - (getHeight() - 5) / 2.0 + cos(0.75) * ((getHeight() - 5) / 2.0 - 5)
        val y2 = (getHeight() - 5) / 2.0 + cos(0.75) * ((getHeight() - 5) / 2.0 - 5)
        if (isHovered(mouseX, mouseY, x1, y1, x2, y2)) {
            rectDragInfo.setDragInfo(true, mouseX, mouseY)
            return true
        }
        val innerRadius = (getHeight() - 5) / 2.0 - 5
        val outerRadius = (getHeight() - 5) / 2.0
        if (Vec2f(mouseX.toFloat(), mouseY.toFloat()).distanceSquared(Vec2f((x1 + (x2 - x1) * 0.5).toFloat(), (y1 + (y2 - y1) * 0.5).toFloat())) in (innerRadius * innerRadius)..(outerRadius * outerRadius)) {
            val valueColor = value
            if (System.currentTimeMillis() - lastWheelClick < 250L) {
                valueColor.rainbow = !valueColor.rainbow
            } else {
                valueColor.rainbow = false
            }
            wheelDragInfo.setDragInfo(true, mouseX, mouseY)
            lastWheelClick = System.currentTimeMillis()
            return true
        }
        return false
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

    override fun getHeight() = 55.0
}