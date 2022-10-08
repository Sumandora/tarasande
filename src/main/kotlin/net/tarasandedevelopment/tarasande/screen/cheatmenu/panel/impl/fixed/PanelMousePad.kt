package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientPlayerEntity
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil.fakeRotation
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import java.awt.Color
import java.util.*
import kotlin.math.abs
import kotlin.math.max

class PanelMousePad(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Mouse Pad", x, y, 100.0, 50.0, background = true, fixed = true) {

    private val rotations = ArrayList<Pair<Float, Float>>()
    private var lastRotation: Rotation? = null

    init {
        TarasandeMain.get().managerEvent.add {
            if (it is EventUpdate) {
                if (it.state == EventUpdate.State.POST) {

                    val player = MinecraftClient.getInstance().player as IClientPlayerEntity
                    val rotation: Rotation? = if (fakeRotation != null) fakeRotation else Rotation(player.tarasande_getLastYaw(), player.tarasande_getLastPitch())

                    val deltaYaw: Float
                    val deltaPitch: Float
                    if (lastRotation != null) {
                        deltaYaw = rotation!!.yaw - lastRotation!!.yaw
                        deltaPitch = rotation.pitch - lastRotation!!.pitch
                    } else {
                        deltaPitch = 0f
                        deltaYaw = deltaPitch
                    }

                    lastRotation = rotation
                    rotations.add(Pair(deltaYaw, deltaPitch))
                }
            }
        }
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (rotations.isEmpty())
            return
        val xMax = max(rotations.stream().mapToDouble { abs(it.first).toDouble() }.max().asDouble, panelWidth / 2.0)
        val yMax = max(rotations.stream().mapToDouble { abs(it.second).toDouble() }.max().asDouble, panelHeight / 2.0)

        val matrix = matrices?.peek()?.positionMatrix!!

        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.disableCull()
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)

        for (rotation in rotations)
            bufferBuilder.
            vertex(matrix, (x + panelWidth / 2f + (rotation.first / xMax) * (panelWidth / 2f)).toFloat(), (y + panelHeight / 2f + (rotation.second / yMax) * (panelHeight / 2f)).toFloat(), 0.0f).
            color(1.0f, 1.0f, 1.0f, 1.0f).
            next()

        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
        RenderSystem.enableCull()

        RenderUtil.fillCircle(matrices, (x + panelWidth / 2f + (rotations.last().first / xMax) * (panelWidth / 2f)), (y + panelHeight / 2f + (rotations.last().second / yMax) * (panelHeight / 2f)), 1.0, Color.white.rgb)

        while (rotations.size > 20)
            rotations.removeAt(0)
    }
}