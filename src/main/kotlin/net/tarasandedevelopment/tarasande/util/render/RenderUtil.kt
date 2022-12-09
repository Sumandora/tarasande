package net.tarasandedevelopment.tarasande.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.Formatting
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBind
import net.tarasandedevelopment.tarasande.util.extension.minecraft.minus
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.*

object RenderUtil {

    private val escapeCharacters = listOf("\t", "\b", "\n", "\r")
    var deltaTime = 0.0

    fun isHovered(mouseX: Double, mouseY: Double, left: Double, up: Double, right: Double, bottom: Double): Boolean {
        return mouseX > left && mouseY > up && mouseX < right && mouseY < bottom
    }

    fun fill(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, color: Int) {
        val matrix = matrices!!.peek().positionMatrix
        val colors = colorToRGBF(color)
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), min(y1, y2).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), min(y1, y2).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun roundedFill(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, round: Double, color: Int) {
        RenderSystem.disableCull()
        RenderSystem.disableDepthTest()
        val matrix = matrices!!.peek().positionMatrix
        val colors = colorToRGBF(color)
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR)
        var quarterCircle = 0.5F
        while (quarterCircle <= 0.75F) {
            bufferBuilder.vertex(matrix, (x1 + round + round * cos(quarterCircle * PI * 2)).toFloat(), (y1 + round + round * sin(quarterCircle * PI * 2)).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
            quarterCircle += 0.025F
        }
        while (quarterCircle <= 1.0F) {
            bufferBuilder.vertex(matrix, (x2 - round + round * cos(quarterCircle * PI * 2)).toFloat(), (y1 + round + round * sin(quarterCircle * PI * 2)).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
            quarterCircle += 0.025F
        }
        quarterCircle = 0.0F
        while (quarterCircle <= 0.25F) {
            bufferBuilder.vertex(matrix, (x2 - round + round * cos(quarterCircle * PI * 2)).toFloat(), (y2 - round + round * sin(quarterCircle * PI * 2)).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
            quarterCircle += 0.025F
        }
        while (quarterCircle <= 0.5F) {
            bufferBuilder.vertex(matrix, (x1 + round + round * cos(quarterCircle * PI * 2)).toFloat(), (y2 - round + round * sin(quarterCircle * PI * 2)).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
            quarterCircle += 0.025F
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun outlinedFill(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, width: Float, color: Int) {
        glEnable(GL_LINE_SMOOTH)
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        val lineWidth = glGetFloat(GL_LINE_WIDTH)
        glLineWidth(width)
        val matrix = matrices!!.peek().positionMatrix
        val colors = colorToRGBF(color)

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), min(y1, y2).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), min(y1, y2).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
        glLineWidth(lineWidth)
        glDisable(GL_LINE_SMOOTH)
    }

    fun outlinedHorizontalGradient(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, width: Float, colorStart: Int, colorEnd: Int) {
        glEnable(GL_LINE_SMOOTH)
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        val lineWidth = glGetFloat(GL_LINE_WIDTH)
        glLineWidth(width)
        val matrix = matrices!!.peek().positionMatrix

        val startColors = colorToRGBF(colorStart)
        val endColors = colorToRGBF(colorEnd)

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0F).color(startColors[0], startColors[1], startColors[2], startColors[3]).next()
        bufferBuilder.vertex(matrix, max(x1, x2).toFloat(), min(y1, y2).toFloat(), 0.0F).color(startColors[0], startColors[1], startColors[2], startColors[3]).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), min(y1, y2).toFloat(), 0.0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
        bufferBuilder.vertex(matrix, min(x1, x2).toFloat(), max(y1, y2).toFloat(), 0.0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
        glLineWidth(lineWidth)
        glDisable(GL_LINE_SMOOTH)
    }

    fun fillHorizontalGradient(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, colorStart: Int, colorEnd: Int) {
        val matrix = matrices!!.peek().positionMatrix

        val startColors = colorToRGBF(colorStart)
        val endColors = colorToRGBF(colorEnd)

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x1.toFloat(), y1.toFloat(), 0.0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
        bufferBuilder.vertex(matrix, x1.toFloat(), y2.toFloat(), 0.0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y2.toFloat(), 0.0F).color(startColors[0], startColors[1], startColors[2], startColors[3]).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y1.toFloat(), 0.0F).color(startColors[0], startColors[1], startColors[2], startColors[3]).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun fillVerticalGradient(matrices: MatrixStack?, x1: Double, y1: Double, x2: Double, y2: Double, colorStart: Int, colorEnd: Int) {
        val matrix = matrices!!.peek().positionMatrix

        val startColors = colorToRGBF(colorStart)
        val endColors = colorToRGBF(colorEnd)

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x1.toFloat(), y1.toFloat(), 0.0F).color(startColors[0], startColors[1], startColors[2], startColors[3]).next()
        bufferBuilder.vertex(matrix, x1.toFloat(), y2.toFloat(), 0.0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y2.toFloat(), 0.0F).color(endColors[0], endColors[1], endColors[2], endColors[3]).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y1.toFloat(), 0.0F).color(startColors[0], startColors[1], startColors[2], startColors[3]).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun outlinedCircle(matrices: MatrixStack?, x: Double, y: Double, radius: Double, width: Float, color: Int) {
        glEnable(GL_LINE_SMOOTH)
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        val lineWidth = glGetFloat(GL_LINE_WIDTH)
        glLineWidth(width)
        val matrix = matrices!!.peek().positionMatrix
        val colors = colorToRGBF(color)
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
        var circle = 0.0
        while (circle <= 1.01) {
            bufferBuilder.vertex(matrix, (x - sin(circle * PI * 2) * radius).toFloat(), (y + cos(circle * PI * 2) * radius).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
            circle += 0.01
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
        glLineWidth(lineWidth)
        glDisable(GL_LINE_SMOOTH)
    }

    fun fillCircle(matrices: MatrixStack?, x: Double, y: Double, radius: Double, color: Int) {
        RenderSystem.disableCull()
        RenderSystem.disableDepthTest()
        val matrix = matrices!!.peek().positionMatrix
        val colors = colorToRGBF(color)
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR)
        var circle = 0.0
        while (circle <= 1.01) {
            bufferBuilder.vertex(matrix, (x - sin(circle * PI * 2) * radius).toFloat(), (y + cos(circle * PI * 2) * radius).toFloat(), 0.0F).color(colors[0], colors[1], colors[2], colors[3]).next()
            circle += 0.01
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun blockOutline(matrices: MatrixStack?, voxelShape: VoxelShape, color: Int) {
        if (voxelShape.isEmpty) return

        val matrix = matrices!!.peek().positionMatrix

        RenderSystem.enableBlend()
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        RenderSystem.disableDepthTest()

        val colors = colorToRGBF(color)
        RenderSystem.setShaderColor(colors[0], colors[1], colors[2], colors[3])

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.setShader { GameRenderer.getPositionProgram() }

        val vec3d = MinecraftClient.getInstance().gameRenderer.camera.pos
        val shape = voxelShape.offset(-vec3d.x, -vec3d.y, -vec3d.z)

        val minX = shape.getMin(Direction.Axis.X).toFloat()
        val maxX = shape.getMax(Direction.Axis.X).toFloat()

        val minY = shape.getMin(Direction.Axis.Y).toFloat()
        val maxY = shape.getMax(Direction.Axis.Y).toFloat()

        val minZ = shape.getMin(Direction.Axis.Z).toFloat()
        val maxZ = shape.getMax(Direction.Axis.Z).toFloat()

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

    private fun colorToRGBF(color: Int): FloatArray {
        val f = (color shr 24 and 0xFF) / 255.0F
        val g = (color shr 16 and 0xFF) / 255.0F
        val h = (color shr 8 and 0xFF) / 255.0F
        val k = (color and 0xFF) / 255.0F

        return floatArrayOf(g, h, k, f)
    }

    fun renderCorrectItem(matrices: MatrixStack, x: Int, y: Int, tickDelta: Float, item: ItemStack) {
        val position = MathUtil.fromMatrices(matrices)

        RenderSystem.enableCull()
        DiffuseLighting.enableGuiDepthLighting()
        MinecraftClient.getInstance().inGameHud.renderHotbarItem((position.x + x).toInt(), (position.y + y).toInt(), tickDelta, MinecraftClient.getInstance().player, item, 0)
        DiffuseLighting.disableGuiDepthLighting()
    }

    fun colorInterpolate(a: Color, b: Color, t: Double) = colorInterpolate(a, b, t, t, t, t)

    fun colorInterpolate(a: Color, b: Color, tR: Double, tG: Double, tB: Double, tA: Double): Color {
        return Color((a.red + (b.red - a.red) * tR.toFloat()) / 255.0F, (a.green + (b.green - a.green) * tG.toFloat()) / 255.0F, (a.blue + (b.blue - a.blue) * tB.toFloat()) / 255.0F, (a.alpha + (b.alpha - a.alpha) * tA.toFloat()) / 255.0F)
    }

    fun createImage(path: String) = NativeImageBackedTexture(NativeImage.read(javaClass.getResourceAsStream("/assets/" + TarasandeMain.get().name + "/textures/$path")))

    fun formattingByHex(hex: Int): Formatting {
        var bestFormatting: Formatting? = null
        var bestDiff = 0.0
        val red = (hex shr 16 and 0xFF) / 255.0F
        val green = (hex shr 8 and 0xFF) / 255.0F
        val blue = (hex shr 0 and 0xFF) / 255.0F

        for (formatting in Formatting.values()) {
            if (formatting.colorValue == null) continue

            val otherRed = (formatting.colorValue!! shr 16 and 0xFF) / 255.0F
            val otherGreen = (formatting.colorValue!! shr 8 and 0xFF) / 255.0F
            val otherBlue = (formatting.colorValue!! shr 0 and 0xFF) / 255.0F

            val diff = (otherRed - red).toDouble().pow(2.0) * 0.2126 + (otherGreen - green).toDouble().pow(2.0) * 0.7152 + (otherBlue - blue).toDouble().pow(2.0) * 0.0722

            if (bestFormatting == null || bestDiff > diff) {
                bestFormatting = formatting
                bestDiff = diff
            }
        }
        return bestFormatting!!
    }

    fun renderPath(matrices: MatrixStack?, path: List<Vec3d>, color: Int) {
        RenderSystem.enableBlend()
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        RenderSystem.disableDepthTest()

        matrices?.push()
        val vec3d = MinecraftClient.getInstance().gameRenderer.camera.pos
        matrices?.translate(-vec3d.x, -vec3d.y, -vec3d.z)

        val colors = colorToRGBF(color)
        RenderSystem.setShaderColor(colors[0], colors[1], colors[2], colors[3])

        val bufferBuilder = Tessellator.getInstance().buffer

        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }

        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)

        val matrix = matrices?.peek()?.positionMatrix!!
        for (vec in path) {
            bufferBuilder.vertex(matrix, vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat()).color(color).next()
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        matrices.pop()
        RenderSystem.enableDepthTest()
        glDisable(GL_LINE_SMOOTH)
        RenderSystem.disableBlend()
    }

    fun project(modelView: Matrix4f, projection: Matrix4f, vector: Vec3d): Vec3d? {
        val camPos = vector - MinecraftClient.getInstance().gameRenderer.camera.pos
        val vec1 = Vector4f(camPos.x.toFloat(), camPos.y.toFloat(), camPos.z.toFloat(), 1.0F).mul(modelView)
        val screenPos = vec1.mul(projection)

        if (screenPos.w <= 0.0) return null

        val newW = 1.0 / screenPos.w * 0.5

        screenPos.set(
            (screenPos.x * newW + 0.5).toFloat(),
            (screenPos.y * newW + 0.5).toFloat(),
            (screenPos.z * newW + 0.5).toFloat(),
            newW.toFloat()
        )

        return Vec3d(
            screenPos.x * MinecraftClient.getInstance().window?.framebufferWidth!! / MinecraftClient.getInstance().window?.scaleFactor!!,
            (MinecraftClient.getInstance().window?.framebufferHeight!! - (screenPos.y * MinecraftClient.getInstance().window?.framebufferHeight!!)) / MinecraftClient.getInstance().window?.scaleFactor!!,
            screenPos.z.toDouble()
        )
    }

    fun getBindName(type: ValueBind.Type, button: Int): String {
        when (type) {
            ValueBind.Type.KEY -> {
                var keyName: String?
                if (button == GLFW.GLFW_KEY_UNKNOWN) keyName = "none"
                else {
                    keyName = GLFW.glfwGetKeyName(button, -1)
                    if (keyName == null) keyName = GLFW.glfwGetKeyName(GLFW.GLFW_KEY_UNKNOWN, GLFW.glfwGetKeyScancode(button))
                }

                if (keyName == null || keyName.trim().isEmpty() || escapeCharacters.contains(keyName)) {
                    for (field in GLFW::class.java.declaredFields) {
                        if (field.name.startsWith("GLFW_KEY_")) {
                            val content = field.get(GLFW::class.java)
                            if (content == button) {
                                keyName = field.name.substring("GLFW_KEY_".length).replace("_", " ").lowercase()
                            }
                        }
                    }
                }

                if (keyName.isNullOrEmpty()) {
                    keyName = "Key#$button"
                }
                return keyName
            }

            ValueBind.Type.MOUSE -> return "Mouse#$button"
            else -> return "Invalid type"
        }
    }
}