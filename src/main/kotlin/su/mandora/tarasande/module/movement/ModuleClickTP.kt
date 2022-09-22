package su.mandora.tarasande.module.movement

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.world.ClientWorld
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventMouse
import su.mandora.tarasande.event.EventRender3D
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.pathfinder.Node
import su.mandora.tarasande.util.pathfinder.PathFinder
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.render.RenderUtil
import java.awt.Color
import java.util.function.Consumer

class ModuleClickTP : Module("Click tp", "Teleports you to the position you click at", ModuleCategory.MOVEMENT) {

    private fun isPassable(pos: BlockPos): Boolean {
        return mc.world?.getBlockState(pos)?.getCollisionShape(mc.world, pos).let { it == null || it.isEmpty }
    }

    private val pathFinder = PathFinder(object : Function2<ClientWorld?, Node, Boolean> {
        override fun invoke(world: ClientWorld?, node: Node): Boolean {
            return isPassable(BlockPos(node.x, node.y, node.z)) && isPassable(BlockPos(node.x, node.y + 1, node.z))
        }
    })

    private var path: List<Vec3d>? = null
    private var goal: Vec3d? = null

    override fun onDisable() {
        path = null
        goal = null
    }

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventMouse) {
            if (event.action == GLFW.GLFW_PRESS && event.button == 1 && mc.currentScreen == null) {
                val hitResult = PlayerUtil.getTargetedEntity(mc.options.viewDistance.value * 16.0, Rotation(mc.player!!), false)
                if (hitResult != null) {
                    var blockPos =
                        if (hitResult is BlockHitResult)
                            hitResult.blockPos
                        else
                            BlockPos(hitResult.pos)
                    if (blockPos == null)
                        return@Consumer

                    while (!isPassable(blockPos))
                        blockPos = blockPos.add(0, 1, 0)

                    for (vec in (pathFinder.findPath(mc.player?.pos!!, Vec3d.of(blockPos).also { goal = it }, 1000L) ?: return@Consumer).also { path = it }) {
                        mc.networkHandler?.sendPacket(PositionAndOnGround(vec.x, vec.y, vec.z, false))
                        mc.player?.setPosition(vec)
                    }
                }
            }
        } else if (event is EventRender3D) {
            if (goal != null) {
                RenderUtil.blockOutline(event.matrices, VoxelShapes.fullCube().offset(goal?.x!!, goal?.y!!, goal?.z!!), Color(255, 255, 255, 50).rgb)
            }
            if (path == null)
                return@Consumer
            RenderSystem.enableBlend()
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            RenderSystem.disableCull()
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            RenderSystem.disableDepthTest()
            event.matrices.push()
            val vec3d = MinecraftClient.getInstance().gameRenderer.camera.pos
            event.matrices.translate(-vec3d.x, -vec3d.y, -vec3d.z)
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
            val bufferBuilder = Tessellator.getInstance().buffer
            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
            val matrix = event.matrices.peek()?.positionMatrix!!
            for (vec in path!!) {
                bufferBuilder.vertex(matrix, vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat()).color(1f, 1f, 1f, 1f).next()
            }
            BufferRenderer.drawWithShader(bufferBuilder.end())
            event.matrices.pop()
            RenderSystem.enableDepthTest()
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            RenderSystem.enableCull()
            RenderSystem.disableBlend()
        }
    }

}