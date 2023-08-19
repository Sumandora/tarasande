package su.mandora.tarasande.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Box
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import su.mandora.tarasande.injection.accessor.IDrawContext
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBind
import su.mandora.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import su.mandora.tarasande.util.extension.minecraft.math.minus
import java.awt.Color
import kotlin.math.*

object RenderUtil {

    private val escapeCharacters = listOf("\t", "\b", "\n", "\r")
    var deltaTime = 0.0

    fun isHovered(mouseX: Double, mouseY: Double, left: Double, up: Double, right: Double, bottom: Double): Boolean {
        return mouseX > left && mouseY > up && mouseX < right && mouseY < bottom
    }

    // Those are just the outlined shapes, use the DrawContext extensions for filled ones.

    fun outlinedFill(matrices: MatrixStack, x1: Double, y1: Double, x2: Double, y2: Double, width: Float, color: Int) {
        glEnable(GL_LINE_SMOOTH)
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        val lineWidth = glGetFloat(GL_LINE_WIDTH)
        glLineWidth(width)
        val matrix = matrices.peek().positionMatrix
        val colors = colorToRGBA(color)

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()

        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), max(y1, y2).toFloat(), 0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), min(y1, y2).toFloat(), 0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), min(y1, y2).toFloat(), 0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())

        RenderSystem.disableBlend()
        glLineWidth(lineWidth)
        glDisable(GL_LINE_SMOOTH)
    }

    fun outlinedHorizontalGradient(matrices: MatrixStack, x1: Double, y1: Double, x2: Double, y2: Double, width: Float, colorStart: Int, colorEnd: Int) {
        glEnable(GL_LINE_SMOOTH)
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        val lineWidth = glGetFloat(GL_LINE_WIDTH)
        glLineWidth(width)
        val matrix = matrices.peek().positionMatrix

        val startColors = colorToRGBA(colorStart)
        val endColors = colorToRGBA(colorEnd)

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()

        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), max(y1, y2).toFloat(), 0F).color(startColors[0], startColors[1], startColors[2], startColors[3]).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), min(y1, y2).toFloat(), 0F).color(startColors[0], startColors[1], startColors[2], startColors[3]).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), min(y1, y2).toFloat(), 0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())

        RenderSystem.disableBlend()
        glLineWidth(lineWidth)
        glDisable(GL_LINE_SMOOTH)
    }

    fun outlinedCircle(matrices: MatrixStack, x: Double, y: Double, radius: Double, width: Float, color: Int) {
        glEnable(GL_LINE_SMOOTH)
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        val lineWidth = glGetFloat(GL_LINE_WIDTH)
        glLineWidth(width)
        val matrix = matrices.peek().positionMatrix
        val colors = colorToRGBA(color)
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()

        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
        var circle = 0.0
        while (circle <= 1.01) {
            bufferBuilder.vertex(matrix, (x - sin(circle * PI * 2) * radius).toFloat(), (y + cos(circle * PI * 2) * radius).toFloat(), 0F).color(colors[0], colors[1], colors[2], colors[3]).next()
            circle += 0.01
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())

        RenderSystem.disableBlend()
        glLineWidth(lineWidth)
        glDisable(GL_LINE_SMOOTH)
    }

    fun fillCircle(matrices: MatrixStack, x: Double, y: Double, radius: Double, color: Int) {
        val cull = glIsEnabled(GL_CULL_FACE)
        RenderSystem.disableCull()
        RenderSystem.disableDepthTest()
        val matrix = matrices.peek().positionMatrix
        val colors = colorToRGBA(color)
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()

        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR)
        var circle = 0.0
        while (circle <= 1.01) {
            bufferBuilder.vertex(matrix, (x - sin(circle * PI * 2) * radius).toFloat(), (y + cos(circle * PI * 2) * radius).toFloat(), 0F).color(colors[0], colors[1], colors[2], colors[3]).next()
            circle += 0.01
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())

        RenderSystem.disableBlend()
        if(cull)
            RenderSystem.enableCull()
    }

    fun blockOutline(matrices: MatrixStack, box: Box, color: Int) {
        val matrix = matrices.peek().positionMatrix

        RenderSystem.enableBlend()
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        RenderSystem.disableDepthTest()

        val colors = colorToRGBA(color)
        RenderSystem.setShaderColor(colors[0], colors[1], colors[2], colors[3])

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.setShader { GameRenderer.getPositionProgram() }

        val vec3d = mc.gameRenderer.camera.pos
        val shape = box.offset(-vec3d.x, -vec3d.y, -vec3d.z)

        val minX = shape.minX.toFloat()
        val maxX = shape.maxX.toFloat()

        val minY = shape.minY.toFloat()
        val maxY = shape.maxY.toFloat()

        val minZ = shape.minZ.toFloat()
        val maxZ = shape.maxZ.toFloat()

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION)
        bufferBuilder.vertex(matrix, minX, minY, minZ).next()
        bufferBuilder.vertex(matrix, maxX, minY, minZ).next()
        bufferBuilder.vertex(matrix, maxX, minY, maxZ).next()
        bufferBuilder.vertex(matrix, minX, minY, maxZ).next()

        bufferBuilder.vertex(matrix, minX, maxY, minZ).next()
        bufferBuilder.vertex(matrix, minX, maxY, maxZ).next()
        bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next()
        bufferBuilder.vertex(matrix, maxX, maxY, minZ).next()

        bufferBuilder.vertex(matrix, minX, minY, minZ).next()
        bufferBuilder.vertex(matrix, minX, maxY, minZ).next()
        bufferBuilder.vertex(matrix, maxX, maxY, minZ).next()
        bufferBuilder.vertex(matrix, maxX, minY, minZ).next()

        bufferBuilder.vertex(matrix, maxX, minY, minZ).next()
        bufferBuilder.vertex(matrix, maxX, maxY, minZ).next()
        bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next()
        bufferBuilder.vertex(matrix, maxX, minY, maxZ).next()

        bufferBuilder.vertex(matrix, minX, minY, maxZ).next()
        bufferBuilder.vertex(matrix, maxX, minY, maxZ).next()
        bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next()
        bufferBuilder.vertex(matrix, minX, maxY, maxZ).next()

        bufferBuilder.vertex(matrix, minX, minY, minZ).next()
        bufferBuilder.vertex(matrix, minX, minY, maxZ).next()
        bufferBuilder.vertex(matrix, minX, maxY, maxZ).next()
        bufferBuilder.vertex(matrix, minX, maxY, minZ).next()

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())

        RenderSystem.enableDepthTest()
        RenderSystem.enableBlend()
        glDisable(GL_LINE_SMOOTH)
    }

    fun colorToRGBA(color: Int): FloatArray {
        val a = ColorHelper.Argb.getAlpha(color).toFloat() / 255F
        val r = ColorHelper.Argb.getRed(color).toFloat() / 255F
        val g = ColorHelper.Argb.getGreen(color).toFloat() / 255F
        val b = ColorHelper.Argb.getBlue(color).toFloat() / 255F

        return floatArrayOf(r, g, b, a)
    }

    fun renderItemStack(context: DrawContext, x: Int, y: Int, tickDelta: Float, item: ItemStack) {
        RenderSystem.enableCull()
        DiffuseLighting.enableGuiDepthLighting()
        if (mc.currentScreen is ScreenBetterOwnerValues) { // Hack for blur
            val prev = (context as IDrawContext).tarasande_isGuiItemRendering()
            (context as IDrawContext).tarasande_setGuiItemRendering(true)
            mc.inGameHud.renderHotbarItem(context, x, y, tickDelta, mc.player, item, 0)
            (context as IDrawContext).tarasande_setGuiItemRendering(prev)
        } else {
            mc.inGameHud.renderHotbarItem(context, x, y, tickDelta, mc.player, item, 0)
        }
        DiffuseLighting.disableGuiDepthLighting()
    }

    fun colorInterpolate(a: Color, b: Color, t: Double) = colorInterpolate(a, b, t, t, t, t)

    fun colorInterpolate(a: Color, b: Color, tR: Double, tG: Double, tB: Double, tA: Double): Color {
        return Color((a.red + (b.red - a.red) * tR.toFloat()) / 255F, (a.green + (b.green - a.green) * tG.toFloat()) / 255F, (a.blue + (b.blue - a.blue) * tB.toFloat()) / 255F, (a.alpha + (b.alpha - a.alpha) * tA.toFloat()) / 255F)
    }

    fun renderPath(matrices: MatrixStack, path: Iterable<Vec3d>, color: Int) {
        RenderSystem.enableBlend()
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        RenderSystem.disableDepthTest()

        matrices.push()
        val vec3d = mc.gameRenderer.camera.pos
        matrices.translate(-vec3d.x, -vec3d.y, -vec3d.z)

        val colors = colorToRGBA(color)
        RenderSystem.setShaderColor(colors[0], colors[1], colors[2], colors[3])

        val bufferBuilder = Tessellator.getInstance().buffer

        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }

        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)

        val matrix = matrices.peek()?.positionMatrix!!
        for (vec in path) {
            bufferBuilder.vertex(matrix, vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat()).color(color).next()
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        matrices.pop()
        RenderSystem.enableDepthTest()
        glDisable(GL_LINE_SMOOTH)
        RenderSystem.disableBlend()
    }

    fun project(modelView: Matrix4f, projection: Matrix4f, vector: Vec3d): Pair<Vec3d, Boolean> {
        val camPos = vector - mc.gameRenderer.camera.pos
        val vec1 = Vector4f(camPos.x.toFloat(), camPos.y.toFloat(), camPos.z.toFloat(), 1F).mul(modelView)
        val screenPos = vec1.mul(projection)

        val newW = 1.0 / screenPos.w * 0.5

        var position = Vec3d(
            screenPos.x * newW + 0.5,
            screenPos.y * newW + 0.5,
            screenPos.z * newW + 0.5
        )

        position = Vec3d(
            position.x * mc.window.scaledWidth.toDouble(),
            (1.0 - position.y) * mc.window.scaledHeight.toDouble(),
            position.z
        )

        if(screenPos.w <= 0.0) {
            position = Vec3d(
                mc.window.scaledWidth - position.x,
                mc.window.scaledHeight - position.y,
                screenPos.z.toDouble()
            )
        }

        return position to (screenPos.w > 0.0)
    }

    fun getBindName(type: ValueBind.Type, button: Int): String {
        when (type) {
            ValueBind.Type.KEY -> {
                if(button == GLFW.GLFW_KEY_UNKNOWN)
                    return "none"

                var keyName = GLFW.glfwGetKeyName(button, -1) ?: GLFW.glfwGetKeyName(GLFW.GLFW_KEY_UNKNOWN, GLFW.glfwGetKeyScancode(button))

                if (keyName == null || keyName.trim().isEmpty() || escapeCharacters.contains(keyName)) {
                    GLFW::class.java.declaredFields
                        .filter { it.name.startsWith("GLFW_KEY_") }
                        .firstOrNull { it.get(GLFW::class.java) == button }?.also {
                            keyName = it.name.substring("GLFW_KEY_".length).replace("_", " ").lowercase()
                        }
                }

                return if (keyName.isNullOrEmpty()) "Key#$button" else keyName!!
            }

            ValueBind.Type.MOUSE -> return "Mouse#$button"
            else -> return "Invalid type"
        }
    }

    fun quad(context: DrawContext, width: Float = mc.window.framebufferWidth.toFloat(), height: Float = mc.window.framebufferHeight.toFloat()) {
        // Convert width & height to NDC
        @Suppress("NAME_SHADOWING")
        val width = width / mc.window.framebufferWidth.toFloat() * 2F - 1F
        @Suppress("NAME_SHADOWING")
        val height = height / mc.window.framebufferHeight.toFloat() * 2F - 1F

        val matrix = context.matrices.peek().positionMatrix
        val bufferBuilder = Tessellator.getInstance().buffer
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION)
        bufferBuilder.vertex(matrix, -1F, -1F, 0F).next()
        bufferBuilder.vertex(matrix, width, -1F, 0F).next()
        bufferBuilder.vertex(matrix, width, height, 0F).next()
        bufferBuilder.vertex(matrix, -1F, height, 0F).next()
        BufferRenderer.draw(bufferBuilder.end())
    }
}