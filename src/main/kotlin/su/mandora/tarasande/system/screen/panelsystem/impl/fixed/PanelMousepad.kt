package su.mandora.tarasande.system.screen.panelsystem.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.panelsystem.Panel
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.render.RenderUtil
import kotlin.math.abs
import kotlin.math.max

class PanelMousepad : Panel("Mousepad", 100.0, 50.0, true) {

    private val rotations = ArrayList<Rotation>()
    private var lastRotation: Rotation? = null

    init {
        EventDispatcher.add(EventUpdate::class.java) {
            if (it.state == EventUpdate.State.POST) {
                val rotation = Rotations.fakeRotation ?: Rotation(mc.player!!)
                if (lastRotation != null)
                    rotations.add(rotation - lastRotation!!)
                lastRotation = rotation
                while (rotations.size > 20)
                    rotations.removeAt(0)
            }
        }
    }

    override fun renderContent(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (rotations.isEmpty())
            return
        val xMax = max(rotations.stream().mapToDouble { abs(it.yaw).toDouble() }.max().asDouble, panelWidth / 2.0)
        val yMax = max(rotations.stream().mapToDouble { abs(it.pitch).toDouble() }.max().asDouble, panelHeight / 2.0)

        val matrix = matrices.peek()?.positionMatrix!!

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.disableCull()
        RenderSystem.enableBlend()
        
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)

        for (rotation in rotations)
            bufferBuilder.vertex(matrix, (x + panelWidth / 2f + (rotation.yaw / xMax) * (panelWidth / 2f)).toFloat(), (y + (panelHeight + titleBarHeight) / 2f + (rotation.pitch / yMax) * ((panelHeight - titleBarHeight) / 2f)).toFloat(), 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).next()

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        
        RenderSystem.disableBlend()
        RenderSystem.enableCull()

        RenderUtil.fillCircle(matrices, (x + panelWidth / 2f + (rotations.last().yaw / xMax) * (panelWidth / 2f)), (y + (panelHeight + titleBarHeight) / 2f + (rotations.last().pitch / yMax) * ((panelHeight - titleBarHeight) / 2f)), 1.0, -1)
    }
}