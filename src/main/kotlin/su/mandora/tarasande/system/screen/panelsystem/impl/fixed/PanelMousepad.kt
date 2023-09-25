package su.mandora.tarasande.system.screen.panelsystem.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.panelsystem.Panel
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.system.screen.panelsystem.api.PanelFixed
import su.mandora.tarasande.util.render.RenderUtil
import kotlin.math.abs
import kotlin.math.max

class PanelMousepad : PanelFixed("Mousepad", 100.0, 50.0, true) {

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

    override fun renderContent(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (rotations.isEmpty())
            return
        val xMax = max(rotations.stream().mapToDouble { abs(it.yaw).toDouble() }.max().asDouble, panelWidth / 2.0)
        val yMax = max(rotations.stream().mapToDouble { abs(it.pitch).toDouble() }.max().asDouble, panelHeight / 2.0)

        val matrix = context.matrices.peek()?.positionMatrix!!

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.disableCull()
        RenderSystem.enableBlend()

        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)

        for (rotation in rotations)
            bufferBuilder.vertex(matrix, (x + panelWidth / 2F + (rotation.yaw / xMax) * (panelWidth / 2F)).toFloat(), (y + (panelHeight + titleBarHeight) / 2F + (rotation.pitch / yMax) * ((panelHeight - titleBarHeight) / 2F)).toFloat(), 0F).color(1F, 1F, 1F, 1F).next()

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())

        RenderSystem.disableBlend()
        RenderSystem.enableCull()

        RenderUtil.fillCircle(context.matrices, x + panelWidth / 2F + (rotations.last().yaw / xMax) * (panelWidth / 2F), y + (panelHeight + titleBarHeight) / 2F + (rotations.last().pitch / yMax) * ((panelHeight - titleBarHeight) / 2F), 1.0, -1)
    }
}