package net.tarasandedevelopment.tarasande.module.movement

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventMouse
import net.tarasandedevelopment.tarasande.event.EventRender3D
import net.tarasandedevelopment.tarasande.util.extension.withAlpha
import net.tarasandedevelopment.tarasande.util.math.pathfinder.PathFinder
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ModuleClickTP : Module("Click tp", "Teleports you to the position you click at", ModuleCategory.MOVEMENT) {

    private fun isPassable(pos: BlockPos): Boolean {
        return mc.world?.getBlockState(pos)?.getCollisionShape(mc.world, pos).let { it == null || it.isEmpty }
    }

    val pathFinder = PathFinder({ _, node -> isPassable(BlockPos(node.x, node.y, node.z)) && isPassable(BlockPos(node.x, node.y + 1, node.z)) })

    private var path: List<Vec3d>? = null
    private var goal: Vec3d? = null

    override fun onDisable() {
        path = null
        goal = null
    }

    init {
        registerEvent(EventMouse::class.java) { event ->
            if (event.action == GLFW.GLFW_PRESS && event.button == 1 && mc.currentScreen == null) {
                val hitResult = PlayerUtil.getTargetedEntity(mc.options.viewDistance.value * 16.0, Rotation(mc.player!!), false)
                if (hitResult != null) {
                    var blockPos =
                        if (hitResult is BlockHitResult)
                            hitResult.blockPos
                        else
                            BlockPos(hitResult.pos)
                    if (blockPos == null)
                        return@registerEvent

                    while (!isPassable(blockPos))
                        blockPos = blockPos.add(0, 1, 0)

                    for (vec in (pathFinder.findPath(mc.player?.pos!!, Vec3d.of(blockPos).also { goal = it }, 1000L) ?: return@registerEvent).also { path = it }) {
                        mc.networkHandler?.sendPacket(PositionAndOnGround(vec.x, vec.y, vec.z, false))
                        mc.player?.setPosition(vec)
                    }
                }
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            if (goal != null) {
                RenderUtil.blockOutline(event.matrices, VoxelShapes.fullCube().offset(goal?.x!!, goal?.y!!, goal?.z!!), Color.white.withAlpha(50).rgb)
            }
            RenderUtil.renderPath(event.matrices, path ?: return@registerEvent, -1)
        }
    }

}