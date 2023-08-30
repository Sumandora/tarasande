package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShapes
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.event.impl.EventCollisionShape
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBind
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.math.plus
import su.mandora.tarasande.util.extension.minecraft.math.times
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.player.PlayerUtil
import kotlin.math.ceil

class ModulePhase : Module("Phase", "Allows you to move through blocks", ModuleCategory.MOVEMENT) {

    private val mode = ValueMode(this, "Mode", false, "Skip", "Disable collision", "V-Clip")
    private val distance = ValueNumber(this, "Distance", 0.0, 1.0, 2.0, 0.1, isEnabled = { mode.isSelected(0) })
    private val fallThrough = ValueBoolean(this, "Fall through", false, isEnabled = { mode.isSelected(1) })
    private val teleportUpKey = ValueBind(this, "Teleport up key", ValueBind.Type.KEY, GLFW.GLFW_KEY_SPACE, isEnabled = { mode.isSelected(2) })
    private val teleportDownKey = ValueBind(this, "Teleport down key", ValueBind.Type.KEY, GLFW.GLFW_KEY_LEFT_SHIFT, isEnabled = { mode.isSelected(2) })

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state != EventUpdate.State.PRE_PACKET)
                return@registerEvent

            when {
                mode.isSelected(0) -> {
                    if (mc.player?.horizontalCollision == true) {
                        mc.player?.setPosition(mc.player?.pos!! + Rotation(PlayerUtil.getMoveDirection().toFloat(), 0F).forwardVector() * distance.value)
                    }
                }

                mode.isSelected(2) -> {
                    val boundingBoxHeight = mc.player!!.boundingBox.yLength
                    fun isCollideable(blockPos: BlockPos): Boolean {
                        return !mc.world!!.getBlockState(blockPos).getCollisionShape(mc.world!!, blockPos).isEmpty
                    }
                    while (teleportUpKey.wasPressed() > 0) {
                        val aboveHead = mc.player!!.blockPos.add(0, ceil(boundingBoxHeight).toInt(), 0)
                        if (isCollideable(aboveHead)) {
                            val aboveAboveHead = aboveHead.add(0, 1, 0) // Great variable name
                            if (!isCollideable(aboveAboveHead))
                                mc.player!!.setPosition(mc.player!!.x, aboveAboveHead.y.toDouble(), mc.player!!.z)
                        }
                    }

                    while (teleportDownKey.wasPressed() > 0) {
                        val belowHead = mc.player!!.blockPos.add(0, -1, 0)
                        if (isCollideable(belowHead)) {
                            val belowBelowHead = belowHead.add(0, -ceil(boundingBoxHeight).toInt(), 0) // Great variable name again
                            if (!isCollideable(belowBelowHead))
                                mc.player!!.setPosition(mc.player!!.x, belowBelowHead.y.toDouble(), mc.player!!.z)
                        }
                    }
                }
            }
        }

        registerEvent(EventCollisionShape::class.java) { event ->
            if (mode.isSelected(1)) {
                if (fallThrough.value || event.pos.y >= (mc.player ?: return@registerEvent).blockPos.y)
                    event.collisionShape = VoxelShapes.empty()
            }
        }
    }
}
