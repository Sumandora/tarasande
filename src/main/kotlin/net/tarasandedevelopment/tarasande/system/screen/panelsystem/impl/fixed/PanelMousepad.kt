package net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import su.mandora.event.EventDispatcher
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import kotlin.math.abs
import kotlin.math.max

class PanelMousepad : Panel("Mousepad", 100.0, 50.0, true) {

    private val rotations = ArrayList<Rotation>()
    private var lastRotation: Rotation? = null

    init {
        EventDispatcher.add(EventUpdate::class.java) {
            if (it.state == EventUpdate.State.POST) {
                val rotation = if (RotationUtil.fakeRotation != null) RotationUtil.fakeRotation else Rotation(MinecraftClient.getInstance().player!!)
                if (lastRotation != null)
                    rotations.add(rotation!! - lastRotation!!)
                lastRotation = rotation
                while (rotations.size > 20)
                    rotations.removeAt(0)
            }
        }
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (rotations.isEmpty())
            return
        val xMax = max(rotations.stream().mapToDouble { abs(it.yaw).toDouble() }.max().asDouble, panelWidth / 2.0)
        val yMax = max(rotations.stream().mapToDouble { abs(it.pitch).toDouble() }.max().asDouble, panelHeight / 2.0)

        val matrix = matrices?.peek()?.positionMatrix!!

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.disableCull()
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)

        for (rotation in rotations)
            bufferBuilder.vertex(matrix, (x + panelWidth / 2f + (rotation.yaw / xMax) * (panelWidth / 2f)).toFloat(), (y + (panelHeight + titleBarHeight) / 2f + (rotation.pitch / yMax) * ((panelHeight - titleBarHeight) / 2f)).toFloat(), 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).
            next()

        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
        RenderSystem.enableCull()

        RenderUtil.fillCircle(matrices, (x + panelWidth / 2f + (rotations.last().yaw / xMax) * (panelWidth / 2f)), (y + (panelHeight + titleBarHeight) / 2f + (rotations.last().pitch / yMax) * ((panelHeight - titleBarHeight) / 2f)), 1.0, -1)
    }
}