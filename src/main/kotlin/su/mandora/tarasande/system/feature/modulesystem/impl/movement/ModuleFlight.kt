package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.entity.Entity
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes
import su.mandora.tarasande.event.impl.EventCollisionShape
import su.mandora.tarasande.event.impl.EventJump
import su.mandora.tarasande.event.impl.EventMovement
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleFlight : Module("Flight", "Allows flight in non-creative modes", ModuleCategory.MOVEMENT) {

    val mode = ValueMode(this, "Mode", false, "Vanilla", "Motion", "Air walk", "Motion reset")
    val flightSpeed = ValueNumber(this, "Flight speed", 0.0, 1.0, 5.0, 0.1, isEnabled = { mode.isSelected(0) || mode.isSelected(1) })
    private val baseYMotion = ValueNumber(this, "Base Y-motion", -1.0, 0.0, 1.0, 0.01, isEnabled = { mode.isSelected(1) })
    private val tickFrequency = ValueNumber(this, "Tick frequency", 1.0, 2.0, 10.0, 1.0, isEnabled = { mode.isSelected(3) })

    init {
        registerEvent(EventMovement::class.java, 1002) { event ->
            if (event.entity != mc.player)
                return@registerEvent

            if (!mode.isSelected(1))
                return@registerEvent

            var yMotion = 0.0

            if (PlayerUtil.input.jumping)
                yMotion += flightSpeed.value
            if (PlayerUtil.input.sneaking)
                yMotion -= flightSpeed.value

            if (yMotion == 0.0)
                yMotion = baseYMotion.value

            event.velocity = Entity.movementInputToVelocity(Vec3d(
                MathUtil.roundAwayFromZero(PlayerUtil.input.movementSideways.toDouble()),
                0.0,
                MathUtil.roundAwayFromZero(PlayerUtil.input.movementForward.toDouble())
            ), flightSpeed.value.toFloat(), mc.player?.yaw!!)
            event.velocity = Vec3d(event.velocity.x, yMotion, event.velocity.z)
        }

        registerEvent(EventCollisionShape::class.java) { event ->
            if (!mode.isSelected(2))
                return@registerEvent
            if (!event.collisionShape.isEmpty)
                return@registerEvent
            val offset = mc.player?.y!! - event.pos.y
            if(offset in 0.0..1.0)
                event.collisionShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, offset, 1.0)
        }

        registerEvent(EventJump::class.java) { event ->
            if (event.state == EventJump.State.PRE && mode.isSelected(2))
                event.cancelled = true
        }

        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE)
                if (mode.isSelected(3))
                    if (mc.player?.age?.mod(tickFrequency.value.toInt()) == 0)
                        mc.player?.velocity = mc.player?.velocity?.withAxis(Direction.Axis.Y, 0.0)
        }
    }
}
