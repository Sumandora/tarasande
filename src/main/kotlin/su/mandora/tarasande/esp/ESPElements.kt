package su.mandora.tarasande.esp

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import su.mandora.tarasande.base.esp.ESPElement
import su.mandora.tarasande.base.esp.ESPElementRotatable
import su.mandora.tarasande.base.esp.Orientation
import su.mandora.tarasande.module.render.ModuleESP
import su.mandora.tarasande.util.player.tagname.TagName
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueNumber
import java.awt.Color

class ESPElementBox : ESPElement("Box") {
    private val width = ValueNumber(this, "Width", 2.0, 1.0, 5.0, 0.1)
    private val outlined = ValueBoolean(this, "Outlined", true)
    private val outlineWidth = object : ValueNumber(this, "Outline width", 2.0, 1.0, 5.0, 0.1) {
        override fun isEnabled() = outlined.value
    }

    override fun draw(matrices: MatrixStack, entity: Entity, rectangle: ModuleESP.Rectangle) {
        val col = Color(entity.teamColorValue).let { Color(it.red, it.green, it.blue, 255) }.rgb
        if (outlined.value)
            RenderUtil.outlinedFill(matrices, rectangle.x, rectangle.y, rectangle.z, rectangle.w, outlineWidth.value.toFloat(), Color.black.rgb)
        RenderUtil.outlinedFill(matrices, rectangle.x, rectangle.y, rectangle.z, rectangle.w, width.value.toFloat(), col)
    }
}

class ESPElementName : ESPElementRotatable("Name", arrayOf(Orientation.LEFT, Orientation.RIGHT), false, MinecraftClient.getInstance().textRenderer.fontHeight.toDouble()) {
    private val outlined = ValueBoolean(this, "Outlined", true)

    override fun draw(matrices: MatrixStack, entity: Entity, sideBegin: Double, sideEnd: Double) {
        val col = Color(entity.teamColorValue).let { Color(it.red, it.green, it.blue, 255) }.rgb
        val tagName = TagName.getTagName(entity)?.asOrderedText() ?: return
        matrices.push()
        matrices.scale(2.0f / MinecraftClient.getInstance().window?.scaleFactor?.toFloat()!!, 2.0f / MinecraftClient.getInstance().window?.scaleFactor?.toFloat()!!, 1.0f)
        if (outlined.value) {
            val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)
            MinecraftClient.getInstance().textRenderer?.drawWithOutline(tagName, (sideBegin + (sideEnd - sideBegin) * 0.5f - MinecraftClient.getInstance().textRenderer?.getWidth(tagName)!! / 2f).toFloat(), 0.0f, col, Color.black.rgb, matrices.peek().positionMatrix, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE)
            immediate.draw()
        } else {
            MinecraftClient.getInstance().textRenderer?.drawWithShadow(matrices, tagName, (sideBegin + (sideEnd - sideBegin) * 0.5f - MinecraftClient.getInstance().textRenderer?.getWidth(tagName)!! / 2f).toFloat(), 0.0f, col)
        }
        matrices.pop()
    }

}