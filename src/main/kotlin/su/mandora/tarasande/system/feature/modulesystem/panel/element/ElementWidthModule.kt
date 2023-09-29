package su.mandora.tarasande.system.feature.modulesystem.panel.element

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec2f
import org.lwjgl.opengl.GL11
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.valuesystem.ManagerValue
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.util.extension.minecraft.render.fill
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.RenderUtil.isHovered
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.render.helper.element.ElementWidth
import java.awt.Color
import kotlin.math.min

class ElementWidthModule(private val module: Module, width: Double) : ElementWidth(width) {

    private val defaultHeight = FontWrapper.fontHeight() * 1.5 + 2.0
    private var toggleTime = 0L
    private var expansionTime = 0L
    private var expanded = false

    val components = ArrayList<ElementWidthValueComponent<*>>()

    override fun init() {
        if (components.isEmpty()) {
            components.addAll(ManagerValue.getValues(module).mapNotNull { it.createValueComponent() })
        }
        components.forEach(ElementWidthValueComponent<*>::init)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        context.fill(0.0, 0.0, this.width, this.getHeight(), Int.MIN_VALUE)

        val white = Color.white

        FontWrapper.textShadow(context,
            module.name,
            2F,
            (this.defaultHeight * 0.25F - FontWrapper.fontHeight() * 0.25F).toFloat(),
            white.rgb,
            scale = 0.75F,
            offset = 0.5F
        )
        FontWrapper.textShadow(context,
            this.module.description,
            2F,
            (this.defaultHeight * 0.75F - FontWrapper.fontHeight() * 0.25F).toFloat(),
            Color.lightGray.rgb,
            scale = 0.5F,
            offset = 0.5F
        )

        val toggleAnimation = min((System.currentTimeMillis() - toggleTime) / 100.0, 1.0)
        val radius = if (module.enabled.value) toggleAnimation else 1.0 - toggleAnimation
        RenderUtil.fillCircle(context.matrices, width - 7, defaultHeight / 2, radius * 4.0, TarasandeValues.accentColor.getColor().rgb)
        RenderUtil.outlinedCircle(context.matrices, width - 7, defaultHeight / 2, 4.0, 2F, RenderUtil.colorInterpolate(TarasandeValues.accentColor.getColor(), Color.white, radius).rgb)

        if (components.isNotEmpty()) {
            val expansionAnimation = min((System.currentTimeMillis() - expansionTime) / 100.0, 1.0)
            val expansion = if (expanded) expansionAnimation else 1.0 - expansionAnimation

            context.matrices.push()
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
            val lineWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH)
            GL11.glLineWidth(2F)
            val matrix = context.matrices.peek()?.positionMatrix!!
            val bufferBuilder = Tessellator.getInstance().buffer
            RenderSystem.enableBlend()

            RenderSystem.defaultBlendFunc()
            RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
            context.matrices.translate(this.width - 16, this.defaultHeight / 2, 0.0)
            context.matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((expansion * 90.0).toFloat()))
            context.matrices.translate(-(this.width - 16), -(this.defaultHeight / 2), 0.0)
            val accentColor = TarasandeValues.accentColor.getColor()
            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
            bufferBuilder.vertex(matrix, (this.width - 16 - 1).toFloat(), (this.defaultHeight / 2 - 2).toFloat(), 0F).color(accentColor.red / 255F, accentColor.green / 255F, accentColor.blue / 255F, accentColor.alpha / 255F).next()
            bufferBuilder.vertex(matrix, (this.width - 16 + 1).toFloat(), (this.defaultHeight / 2).toFloat(), 0F).color(accentColor.red / 255F, accentColor.green / 255F, accentColor.blue / 255F, accentColor.alpha / 255F).next()
            bufferBuilder.vertex(matrix, (this.width - 16 - 1).toFloat(), (this.defaultHeight / 2 + 2).toFloat(), 0F).color(accentColor.red / 255F, accentColor.green / 255F, accentColor.blue / 255F, accentColor.alpha / 255F).next()
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())

            RenderSystem.disableBlend()
            GL11.glLineWidth(lineWidth)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            context.matrices.pop()
        }
        if (expanded) {
            var yOffset = 0.0
            for (component in components) {
                context.matrices.push()
                context.matrices.translate(5.0, this.defaultHeight + yOffset, 0.0)
                component.width = width - 10.0
                component.render(context, mouseX - 5, (mouseY - defaultHeight - yOffset).toInt(), delta)
                context.matrices.pop()
                yOffset += component.getHeight()
            }
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (expanded) {
            var yOffset = 0.0
            for (component in components) {
                if (component.value.isEnabled()) {
                    component.mouseClicked(mouseX - 5.0, mouseY - defaultHeight - yOffset, button)
                }
                yOffset += component.getHeight()
            }
        }
        if (button == 0) {
            if (isHovered(mouseX, mouseY, 0.0, 0.0, width, getHeight())) {
                if (Vec2f(mouseX.toFloat(), mouseY.toFloat()).distanceSquared(Vec2f((width - 7).toFloat(), (defaultHeight / 2).toFloat())) < 16F) {
                    module.switchState()
                    toggleTime = System.currentTimeMillis()
                }
                if (components.isNotEmpty() && isHovered(mouseX, mouseY, width - 16 - 4, defaultHeight / 2 - 4, width - 16 + 4, defaultHeight / 2 + 4)) {
                    expanded = !expanded
                    expansionTime = System.currentTimeMillis()
                }
                return true
            }
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if (expanded) {
            var yOffset = 0.0
            for (component in components) {
                if (component.value.isEnabled()) {
                    component.mouseReleased(mouseX - 5.0, mouseY - defaultHeight - yOffset, button)
                }
                yOffset += component.getHeight()
            }
        }
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        for (component in components) {
            if (component.value.isEnabled() && component.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                return true
            }
        }
        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        for (component in components) {
            if (component.value.isEnabled() && component.keyPressed(keyCode, scanCode, modifiers)) {
                return true
            }
        }
        return false
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        for (component in components) {
            if (component.value.isEnabled()) {
                component.charTyped(chr, modifiers)
            }
        }
    }

    override fun tick() {
        for (component in components) {
            if (component.value.isEnabled()) {
                component.tick()
            }
        }
    }

    override fun onClose() {
        for (component in components) {
            if (component.value.isEnabled()) {
                component.onClose()
            }
        }
    }

    override fun getHeight(): Double {
        return defaultHeight + if (expanded) {
            var maxHeight = 0.0
            for (component in components) {
                maxHeight += component.getHeight()
            }
            maxHeight
        } else 0.0
    }
}