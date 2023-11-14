package su.mandora.tarasande.util.render.skin

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.PlayerSkinDrawer
import net.minecraft.client.render.*
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.client.util.DefaultSkinHelper
import net.minecraft.util.Identifier
import su.mandora.mcskinlookup.MCSkinLookup
import java.net.URL
import java.util.*


class SkinRenderer(val uuid: UUID?, val name: String) : AutoCloseable {

    val texture: Texture

    private val textureWidth: Int
    private val textureHeight: Int

    init {
        val skin = try {
            val lookup = MCSkinLookup()

            val skinData = if (uuid != null) lookup.lookupUUID(uuid.toString(), Optional.of(name))
            else lookup.lookupName(name)

            skinData.textures.skin
        } catch (e: IllegalStateException) {
            null
        }
        texture = if (skin == null) {
            textureWidth = PlayerSkinDrawer.SKIN_TEXTURE_WIDTH
            textureHeight = PlayerSkinDrawer.SKIN_TEXTURE_HEIGHT
            TextureIdentifier(DefaultSkinHelper.getSkinTextures(uuid).texture)
        } else {
            val conn = URL(skin.url).openConnection()
            conn.connect()

            val image = NativeImage.read(conn.getInputStream())

            textureWidth = image.width
            textureHeight = image.height

            TextureNativeImage(NativeImageBackedTexture(image))
        }
    }

    companion object {
        const val FACE_X1 = PlayerSkinDrawer.FACE_X
        const val FACE_X2 = PlayerSkinDrawer.FACE_X + PlayerSkinDrawer.FACE_WIDTH
        const val FACE_Y1 = PlayerSkinDrawer.FACE_Y
        const val FACE_Y2 = PlayerSkinDrawer.FACE_Y + PlayerSkinDrawer.FACE_HEIGHT

        const val FACE_OVERLAY_X1 = PlayerSkinDrawer.FACE_OVERLAY_X
        const val FACE_OVERLAY_X2 = PlayerSkinDrawer.FACE_OVERLAY_X + PlayerSkinDrawer.FACE_WIDTH
        const val FACE_OVERLAY_Y1 = PlayerSkinDrawer.FACE_OVERLAY_Y
        const val FACE_OVERLAY_Y2 = PlayerSkinDrawer.FACE_OVERLAY_Y + PlayerSkinDrawer.FACE_HEIGHT
    }

    fun drawHead(context: DrawContext, x: Int, y: Int, size: Int) {
        // Base
        val faceU1 = FACE_X1 / textureWidth.toFloat()
        val faceU2 = FACE_X2 / textureWidth.toFloat()

        val faceV1 = FACE_Y1 / textureHeight.toFloat()
        val faceV2 = FACE_Y2 / textureHeight.toFloat()

        // Overlay
        val faceOverlayU1 = FACE_OVERLAY_X1 / textureWidth.toFloat()
        val faceOverlayU2 = FACE_OVERLAY_X2 / textureWidth.toFloat()

        val faceOverlayV1 = FACE_OVERLAY_Y1 / textureHeight.toFloat()
        val faceOverlayV2 = FACE_OVERLAY_Y2 / textureHeight.toFloat()

        texture.draw(context, x, x + size, y, y + size, faceU1, faceU2, faceV1, faceV2)
        texture.draw(context, x, x + size, y, y + size, faceOverlayU1, faceOverlayU2, faceOverlayV1, faceOverlayV2)
    }

    override fun close() {
        texture.close()
    }

    abstract class Texture : AutoCloseable {
        open fun draw(context: DrawContext, x1: Int, x2: Int, y1: Int, y2: Int, u1: Float, u2: Float, v1: Float, v2: Float) {
            RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
            val matrix4f = context.matrices.peek().positionMatrix
            val bufferBuilder = Tessellator.getInstance().buffer
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
            bufferBuilder.vertex(matrix4f, x1.toFloat(), y1.toFloat(), 0F).texture(u1, v1).next()
            bufferBuilder.vertex(matrix4f, x1.toFloat(), y2.toFloat(), 0F).texture(u1, v2).next()
            bufferBuilder.vertex(matrix4f, x2.toFloat(), y2.toFloat(), 0F).texture(u2, v2).next()
            bufferBuilder.vertex(matrix4f, x2.toFloat(), y1.toFloat(), 0F).texture(u2, v1).next()
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        }
    }

    class TextureIdentifier(val id: Identifier) : Texture() {
        override fun draw(context: DrawContext, x1: Int, x2: Int, y1: Int, y2: Int, u1: Float, u2: Float, v1: Float, v2: Float) {
            RenderSystem.setShaderTexture(0, id)
            super.draw(context, x1, x2, y1, y2, u1, u2, v1, v2)
        }

        override fun close() = Unit
    }

    class TextureNativeImage(val texture: NativeImageBackedTexture) : Texture() {
        override fun draw(context: DrawContext, x1: Int, x2: Int, y1: Int, y2: Int, u1: Float, u2: Float, v1: Float, v2: Float) {
            texture.bindTexture()
            RenderSystem.setShaderTexture(0, texture.glId)
            super.draw(context, x1, x2, y1, y2, u1, u2, v1, v2)
        }

        override fun close() {
            texture.close()
        }
    }
}
