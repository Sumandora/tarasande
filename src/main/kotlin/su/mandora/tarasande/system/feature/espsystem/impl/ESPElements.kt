package su.mandora.tarasande.system.feature.espsystem.impl

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.espsystem.ESPElement
import su.mandora.tarasande.system.feature.espsystem.ESPElementRotatable
import su.mandora.tarasande.system.feature.espsystem.Orientation
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleESP
import su.mandora.tarasande.util.extension.kotlinruntime.ignoreAlpha
import su.mandora.tarasande.util.extension.minecraft.render.drawText
import su.mandora.tarasande.util.extension.minecraft.render.fillHorizontalGradient
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import java.awt.Color
import kotlin.math.abs
import kotlin.math.min

class ESPElementBox : ESPElement("Box") {
    private val width = ValueNumber(this, "Width", 0.1, 2.0, 5.0, 0.1)
    private val outlined = ValueBoolean(this, "Outlined", true)
    private val outlineWidth = ValueNumber(this, "Outline width", 0.1, 1.0, 5.0, 0.1, isEnabled = { outlined.value })

    override fun draw(context: DrawContext, entity: Entity, rectangle: ModuleESP.Rectangle) {
        val col = entity.teamColorValue.ignoreAlpha()
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F)
        if (outlined.value)
            RenderUtil.outlinedFill(context.matrices, rectangle.x, rectangle.y, rectangle.z, rectangle.w, (width.value + outlineWidth.value).toFloat(), Color.black.rgb)
        RenderUtil.outlinedFill(context.matrices, rectangle.x, rectangle.y, rectangle.z, rectangle.w, width.value.toFloat(), col)
    }
}

class ESPElementRotatableName : ESPElementRotatable("Name", arrayOf(Orientation.LEFT, Orientation.RIGHT, Orientation.BOTTOM)) {
    //TODO
    private val outlined = ValueBoolean(this, "Outlined", true)
    private val fitBoxWidth = ValueBoolean(this, "Fit box width", false)
    private val scale = ValueNumber(this, "Scale", 0.1, 1.0, 3.0, 0.1)

    override fun draw(context: DrawContext, entity: Entity, sideWidth: Double, orientation: Orientation) {
        val col = entity.teamColorValue.ignoreAlpha()
        val tagName = entity.displayName.asOrderedText()
        context.matrices.push()
        val width = mc.textRenderer!!.getWidth(tagName)
        var factor =
            if (fitBoxWidth.value)
                (sideWidth / width).toFloat()
            else
                1F
        if (factor > 3F) factor = 3F
        factor *= scale.value.toFloat()
        context.matrices.translate(sideWidth / 2, 0.0, 0.0)
        context.matrices.scale(factor, factor, 1F)
        context.matrices.translate(-sideWidth / 2, 0.0, 0.0)
        if (outlined.value) {
            val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)
            mc.textRenderer!!.drawWithOutline(tagName, (sideWidth / 2F - width / 2F).toFloat(), 0F, col, Color.black.rgb, context.matrices.peek().positionMatrix, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE)
            immediate.draw()
        } else {
            context.drawText(mc.textRenderer, tagName, (sideWidth / 2F - width / 2F).toFloat(), 0F, col, true)
        }
        context.matrices.pop()
    }

    override fun getHeight(entity: Entity, sideWidth: Double): Double {
        return FontWrapper.fontHeight().toDouble() * min(sideWidth / mc.textRenderer!!.getWidth(entity.displayName.asOrderedText()), 3.0) * scale.value
    }
}

class ESPElementRotatableHealthBar : ESPElementRotatable("Health bar", arrayOf(Orientation.TOP, Orientation.BOTTOM)) {
    private val outlined = ValueBoolean(this, "Outlined", true)
    private val fadeColorBegin = ValueColor(this, "Fade color begin", 0.33 /*green*/, 1.0, 1.0)
    private val fadeColorEnd = ValueColor(this, "Fade color end", 0.0 /*red*/, 1.0, 1.0)

    override fun draw(context: DrawContext, entity: Entity, sideWidth: Double, orientation: Orientation) {
        val height = getHeight(entity, sideWidth)
        if (height.isNaN() || height <= 0.0) return
        entity as LivingEntity
        context.matrices.push()
        val percentage = MathHelper.clamp(entity.health / entity.maxHealth, 0F, 1F)
        if (orientation == Orientation.RIGHT)
            context.fillHorizontalGradient(0.0, height, sideWidth * percentage, 0.0, RenderUtil.colorInterpolate(fadeColorBegin.getColor(), fadeColorEnd.getColor(), 1.0 - percentage).rgb, fadeColorEnd.getColor().rgb)
        else
            context.fillHorizontalGradient(0.0, 0.0, sideWidth * percentage, height, RenderUtil.colorInterpolate(fadeColorBegin.getColor(), fadeColorEnd.getColor(), 1.0 - percentage).rgb, fadeColorEnd.getColor().rgb)
        if (outlined.value) {
            RenderUtil.outlinedFill(context.matrices, 0.0, 0.0, sideWidth, height, abs(height * 0.5).coerceAtLeast(1.0).toFloat(), Color.black.rgb)
        }
        context.matrices.pop()
    }

    override fun getHeight(entity: Entity, sideWidth: Double) = if (entity is LivingEntity) min(sideWidth * 0.025, 40.0) else 0.0
}
