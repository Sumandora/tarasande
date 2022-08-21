package su.mandora.tarasande.esp

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.base.esp.ESPElement
import su.mandora.tarasande.base.esp.ESPElementRotatable
import su.mandora.tarasande.base.esp.Orientation
import su.mandora.tarasande.module.render.ModuleESP
import su.mandora.tarasande.util.player.tagname.TagName
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueColor
import su.mandora.tarasande.value.ValueNumber
import java.awt.Color
import kotlin.math.min
import kotlin.math.ceil

class ESPElementBox : ESPElement("Box") {
    private val width = ValueNumber(this, "Width", 2.0, 1.0, 5.0, 0.1)
    private val outlined = ValueBoolean(this, "Outlined", true)
    private val outlineWidth = object : ValueNumber(this, "Outline width", 2.0, 1.0, 5.0, 0.1) {
        override fun isEnabled() = outlined.value
    }

    override fun draw(matrices: MatrixStack, entity: Entity, rectangle: ModuleESP.Rectangle) {
        val col = Color(entity.teamColorValue).let { Color(it.red, it.green, it.blue, 255) }.rgb
        if (outlined.value) RenderUtil.outlinedFill(matrices, rectangle.x, rectangle.y, rectangle.z, rectangle.w, outlineWidth.value.toFloat(), Color.black.rgb)
        RenderUtil.outlinedFill(matrices, rectangle.x, rectangle.y, rectangle.z, rectangle.w, width.value.toFloat(), col)
    }
}

class ESPElementName : ESPElementRotatable("Name", arrayOf(Orientation.LEFT, Orientation.RIGHT), false) {
    private val outlined = ValueBoolean(this, "Outlined", true)

    override fun draw(matrices: MatrixStack, entity: Entity, sideWidth: Double) {
        val col = Color(entity.teamColorValue).let { Color(it.red, it.green, it.blue, 255) }.rgb
        val tagName = TagName.getTagName(entity)?.asOrderedText() ?: return
        matrices.push()
        val width = MinecraftClient.getInstance().textRenderer?.getWidth(tagName)!!
        var factor = (sideWidth / width).toFloat()
        if (factor > 3.0f) factor = 3.0f
        matrices.translate(sideWidth / 2, 0.0, 0.0)
        matrices.scale(factor, factor, 1.0f)
        matrices.translate(-sideWidth / 2, 0.0, 0.0)
        if (outlined.value) {
            val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)
            MinecraftClient.getInstance().textRenderer?.drawWithOutline(tagName, (sideWidth / 2f - width / 2f).toFloat(), 0.0f, col, Color.black.rgb, matrices.peek().positionMatrix, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE)
            immediate.draw()
        } else {
            MinecraftClient.getInstance().textRenderer?.drawWithShadow(matrices, tagName, (sideWidth / 2f - width / 2f).toFloat(), 0.0f, col)
        }
        matrices.pop()
    }

    override fun getHeight(entity: Entity, sideWidth: Double): Double {
        return MinecraftClient.getInstance().textRenderer.fontHeight.toDouble() * min(sideWidth / MinecraftClient.getInstance().textRenderer?.getWidth(TagName.getTagName(entity)?.asOrderedText() ?: return 0.0)!!, 3.0)
    }
}

class ESPElementHealthBar : ESPElementRotatable("Health bar", arrayOf(Orientation.TOP, Orientation.BOTTOM)) {
    private val outlined = ValueBoolean(this, "Outlined", true)
    private val fadeColorBegin = ValueColor(this, "Fade color begin", 0.33f /*green*/, 1.0f, 1.0f)
    private val fadeColorEnd = ValueColor(this, "Fade color end", 0.0f /*red*/, 1.0f, 1.0f)

    override fun draw(matrices: MatrixStack, entity: Entity, sideWidth: Double) {
        val height = getHeight(entity, sideWidth)
        if (height <= 0.0) return
        entity as LivingEntity
        matrices.push()
        val percentage = MathHelper.clamp(entity.health / entity.maxHealth, 0.0f, 1.0f)
        RenderUtil.fillHorizontalGradient(matrices, 0.0, 0.0, sideWidth * percentage, height, RenderUtil.colorInterpolate(fadeColorBegin.getColor(), fadeColorEnd.getColor(), 1.0 - percentage).rgb, fadeColorEnd.getColor().rgb)
        if (outlined.value) {
            RenderUtil.outlinedFill(matrices, 0.0, 0.0, sideWidth, height, ceil(height * 0.5).toFloat(), Color.black.rgb)
        }
        matrices.pop()
    }

    override fun getHeight(entity: Entity, sideWidth: Double) = if (entity is LivingEntity) min(sideWidth * 0.01, 40.0) else 0.0
}
