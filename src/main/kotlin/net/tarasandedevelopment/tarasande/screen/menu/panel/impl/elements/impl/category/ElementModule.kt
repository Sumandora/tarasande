package net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.impl.category

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3f
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.screen.menu.valuecomponent.ValueComponent
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.Element
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil.isHovered
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.min

class ElementModule(private val module: Module, width: Double) : Element(width) {

    private val defaultHeight = MinecraftClient.getInstance().textRenderer.fontHeight * 1.5 + 2.0
    private var toggleTime = 0L
    private var expansionTime = 0L
    private var expanded = false

    private val components = ArrayList<ValueComponent>()

    override fun init() {
        if (components.isEmpty()) {
            for (value in TarasandeMain.get().managerValue.getValues(module)) {
                components.add(TarasandeMain.get().screenCheatMenu.managerValueComponent.newInstance(value)!!)
            }
        }
        components.forEach(ValueComponent::init)
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        RenderUtil.fill(matrices, 0.0, 0.0, this.width, this.getHeight(), Int.MIN_VALUE)

        var white = Color.white

        if (!module.isEnabled()) {
            white = white.darker().darker()
            expanded = false
        }

        matrices?.push()
        matrices?.translate(2.0, this.defaultHeight / 4.0 + 1.0, 0.0)
        matrices?.scale(0.75f, 0.75f, 1.0f)
        matrices?.translate(-2.0, -(this.defaultHeight / 4.0 + 1.0f), 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, module.name, 2.0f, ((this.defaultHeight / 4.0f - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0f) + 1.0f).toFloat(), white.rgb)
        matrices?.pop()

        matrices?.push()
        matrices?.translate(2.0, this.defaultHeight - this.defaultHeight / 4.0, 0.0)
        matrices?.scale(0.5f, 0.5f, 1.0f)
        matrices?.translate(-2.0, -(this.defaultHeight - this.defaultHeight / 4.0), 0.0)
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, this.module.description, 2.0f, (this.defaultHeight - this.defaultHeight / 4.0f - MinecraftClient.getInstance().textRenderer.fontHeight / 2.0f).toFloat(), Color.lightGray.let { if (module.isEnabled()) it else it.darker().darker() }.rgb)
        matrices?.pop()

        val toggleAnimation = min((System.currentTimeMillis() - toggleTime) / 100.0, 1.0)
        val radius = if (module.enabled) toggleAnimation else 1.0 - toggleAnimation
        RenderUtil.fillCircle(matrices, width - 7, defaultHeight / 2, radius * 4.0, TarasandeMain.get().clientValues.accentColor.getColor().rgb)
        RenderUtil.outlinedCircle(matrices, width - 7, defaultHeight / 2, 4.0, 2.0f, RenderUtil.colorInterpolate(TarasandeMain.get().clientValues.accentColor.getColor(), Color.white, radius).let { if (module.isEnabled()) it else it.darker().darker() }.rgb)

        if (components.isNotEmpty()) {
            val expansionAnimation = min((System.currentTimeMillis() - expansionTime) / 100.0, 1.0)
            val expansion = if (expanded) expansionAnimation else 1.0 - expansionAnimation

            matrices?.push()
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
            val lineWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH)
            GL11.glLineWidth(2.0f)
            val matrix = matrices?.peek()?.positionMatrix!!
            val bufferBuilder = Tessellator.getInstance().buffer
            RenderSystem.enableBlend()
            RenderSystem.disableTexture()
            RenderSystem.defaultBlendFunc()
            RenderSystem.setShader { GameRenderer.getPositionColorShader() }
            matrices.translate(this.width - 16, this.defaultHeight / 2, 0.0)
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((expansion * 90.0).toFloat()))
            matrices.translate(-(this.width - 16), -(this.defaultHeight / 2), 0.0)
            val accentColor = TarasandeMain.get().clientValues.accentColor.getColor().let { if (module.isEnabled()) it else it.darker().darker() }
            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
            bufferBuilder.vertex(matrix, (this.width - 16 - 1).toFloat(), (this.defaultHeight / 2 - 2).toFloat(), 0.0f).color(accentColor.red / 255f, accentColor.green / 255f, accentColor.blue / 255f, accentColor.alpha / 255f).next()
            bufferBuilder.vertex(matrix, (this.width - 16 + 1).toFloat(), (this.defaultHeight / 2).toFloat(), 0.0f).color(accentColor.red / 255f, accentColor.green / 255f, accentColor.blue / 255f, accentColor.alpha / 255f).next()
            bufferBuilder.vertex(matrix, (this.width - 16 - 1).toFloat(), (this.defaultHeight / 2 + 2).toFloat(), 0.0f).color(accentColor.red / 255f, accentColor.green / 255f, accentColor.blue / 255f, accentColor.alpha / 255f).next()
            BufferRenderer.drawWithShader(bufferBuilder.end())
            RenderSystem.enableTexture()
            RenderSystem.disableBlend()
            GL11.glLineWidth(lineWidth)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            matrices.pop()
        }
        if (expanded) {
            var yOffset = 0.0
            for (component in components) {
                matrices?.push()
                matrices?.translate(5.0, this.defaultHeight + yOffset, 0.0)
                component.width = width - 10.0
                component.render(matrices, mouseX - 5, (mouseY - defaultHeight - yOffset).toInt(), delta)
                matrices?.pop()
                yOffset += component.getHeight()
            }
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!module.isEnabled())
            return isHovered(mouseX, mouseY, 0.0, 0.0, width, getHeight())
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
                if (Vec2f(mouseX.toFloat(), mouseY.toFloat()).distanceSquared(Vec2f((width - 7).toFloat(), (defaultHeight / 2).toFloat())) < 16.0f) {
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
        if (!module.isEnabled())
            return
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

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        if (!module.isEnabled())
            return false
        for (component in components) {
            if (component.value.isEnabled() && component.mouseScrolled(mouseX, mouseY, amount)) {
                return true
            }
        }
        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (!module.isEnabled())
            return false
        for (component in components) {
            if (component.value.isEnabled() && component.keyPressed(keyCode, scanCode, modifiers)) {
                return true
            }
        }
        return false
    }

    override fun charTyped(chr: Char, modifiers: Int) {
        if (!module.isEnabled())
            return
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