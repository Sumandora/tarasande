package net.tarasandedevelopment.tarasande.systems.feature.espsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.MathHelper
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueColor
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.feature.espsystem.ESPElement
import net.tarasandedevelopment.tarasande.systems.feature.espsystem.ESPElementRotatable
import net.tarasandedevelopment.tarasande.systems.feature.espsystem.Orientation
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleESP
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import java.awt.Color
import kotlin.math.abs
import kotlin.math.min

class ESPElementBox : ESPElement("Box") {
    private val width = ValueNumber(this, "Width", 1.0, 2.0, 5.0, 0.1)
    private val outlined = ValueBoolean(this, "Outlined", true)
    private val outlineWidth = object : ValueNumber(this, "Outline width", 1.0, 2.0, 5.0, 0.1) {
        override fun isEnabled() = outlined.value
    }

    override fun draw(matrices: MatrixStack, entity: Entity, rectangle: ModuleESP.Rectangle) {
        val col = Color(entity.teamColorValue).rgb // ignore alpha
        if (outlined.value)
            RenderUtil.outlinedFill(matrices, rectangle.x, rectangle.y, rectangle.z, rectangle.w, outlineWidth.value.toFloat(), Color.black.rgb)
        RenderUtil.outlinedFill(matrices, rectangle.x, rectangle.y, rectangle.z, rectangle.w, width.value.toFloat(), col)
    }
}

class ESPElementRotatableName : ESPElementRotatable("Name", arrayOf(Orientation.LEFT, Orientation.RIGHT, Orientation.BOTTOM)) {
    //TODO
    private val outlined = ValueBoolean(this, "Outlined", true)
    private val fitBoxWidth = ValueBoolean(this, "Fit box width", false)
    private val scale = ValueNumber(this, "Scale", 0.1, 1.0, 3.0, 0.1)

    override fun draw(matrices: MatrixStack, entity: Entity, sideWidth: Double, orientation: Orientation) {
        val col = Color(entity.teamColorValue).rgb // ignore alpha
        val tagName = TarasandeMain.get().tagName.getTagName(entity)?.asOrderedText() ?: return
        matrices.push()
        val width = MinecraftClient.getInstance().textRenderer!!.getWidth(tagName)
        var factor =
            if (fitBoxWidth.value)
                (sideWidth / width).toFloat()
            else
                1.0f
        if (factor > 3.0f) factor = 3.0f
        factor *= scale.value.toFloat()
        matrices.translate(sideWidth / 2, 0.0, 0.0)
        matrices.scale(factor, factor, 1.0f)
        matrices.translate(-sideWidth / 2, 0.0, 0.0)
        if (outlined.value) {
            val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)
            MinecraftClient.getInstance().textRenderer!!.drawWithOutline(tagName, (sideWidth / 2f - width / 2f).toFloat(), 0.0f, col, Color.black.rgb, matrices.peek().positionMatrix, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE)
            immediate.draw()
        } else {
            MinecraftClient.getInstance().textRenderer!!.drawWithShadow(matrices, tagName, (sideWidth / 2f - width / 2f).toFloat(), 0.0f, col)
        }
        matrices.pop()
    }

    override fun getHeight(entity: Entity, sideWidth: Double): Double {
        return FontWrapper.fontHeight().toDouble() * min(sideWidth / MinecraftClient.getInstance().textRenderer!!.getWidth(TarasandeMain.get().tagName.getTagName(entity)?.asOrderedText() ?: return 0.0), 3.0) * scale.value
    }
}

class ESPElementRotatableHealthBar : ESPElementRotatable("Health bar", arrayOf(Orientation.TOP, Orientation.BOTTOM)) {
    private val outlined = ValueBoolean(this, "Outlined", true)
    private val fadeColorBegin = ValueColor(this, "Fade color begin", 0.33f /*green*/, 1.0f, 1.0f)
    private val fadeColorEnd = ValueColor(this, "Fade color end", 0.0f /*red*/, 1.0f, 1.0f)

    override fun draw(matrices: MatrixStack, entity: Entity, sideWidth: Double, orientation: Orientation) {
        val height = getHeight(entity, sideWidth)
        if (height <= 0.0) return
        entity as LivingEntity
        matrices.push()
        val percentage = MathHelper.clamp(entity.health / entity.maxHealth, 0.0f, 1.0f)
        if (orientation == Orientation.RIGHT)
            RenderUtil.fillHorizontalGradient(matrices, 0.0, height, sideWidth * percentage, 0.0, RenderUtil.colorInterpolate(fadeColorBegin.getColor(), fadeColorEnd.getColor(), 1.0 - percentage).rgb, fadeColorEnd.getColor().rgb)
        else
            RenderUtil.fillHorizontalGradient(matrices, 0.0, 0.0, sideWidth * percentage, height, RenderUtil.colorInterpolate(fadeColorBegin.getColor(), fadeColorEnd.getColor(), 1.0 - percentage).rgb, fadeColorEnd.getColor().rgb)
        if (outlined.value) {
            RenderUtil.outlinedFill(matrices, 0.0, 0.0, sideWidth, height, abs(height * 0.5).coerceAtLeast(1.0).toFloat(), Color.black.rgb)
        }
        matrices.pop()
    }

    override fun getHeight(entity: Entity, sideWidth: Double) = if (entity is LivingEntity) min(sideWidth * 0.025, 40.0) else 0.0
}
