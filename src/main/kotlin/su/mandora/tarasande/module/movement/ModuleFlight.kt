package su.mandora.tarasande.module.movement

import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.event.Priority
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventCollisionShape
import su.mandora.tarasande.event.EventJump
import su.mandora.tarasande.event.EventMovement
import su.mandora.tarasande.event.EventVanillaFlight
import su.mandora.tarasande.mixin.accessor.IEntity
import su.mandora.tarasande.mixin.accessor.IKeyBinding
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleFlight : Module("Flight", "Allows flight in non-creative modes", ModuleCategory.MOVEMENT) {

    val mode = ValueMode(this, "Mode", false, "Vanilla", "Motion", "Air walk")
    val flightSpeed = object : ValueNumber(this, "Flight speed", 0.0, 1.0, 5.0, 0.1) {
        override fun isEnabled() = mode.isSelected(0) || mode.isSelected(1)
    }
    private val baseYMotion = object : ValueNumber(this, "Base Y-motion", -1.0, 0.0, 1.0, 0.01) {
        override fun isEnabled() = mode.isSelected(1)
    }

    @Priority(1002) // we need to override step
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventVanillaFlight -> {
                if (!mode.isSelected(0))
                    return@Consumer
                event.flying = true
                event.flightSpeed *= flightSpeed.value.toFloat()
            }

            is EventMovement -> {
                if (!mode.isSelected(1))
                    return@Consumer
                if (event.entity != mc.player)
                    return@Consumer
                var yMotion = 0.0
                if ((mc.options.jumpKey as IKeyBinding).tarasande_forceIsPressed())
                    yMotion += flightSpeed.value
                if ((mc.options.sneakKey as IKeyBinding).tarasande_forceIsPressed())
                    yMotion -= flightSpeed.value
                if (yMotion == 0.0)
                    yMotion = baseYMotion.value
                event.velocity = (mc.player as IEntity).tarasande_invokeMovementInputToVelocity(Vec3d(
                    MathUtil.roundAwayFromZero(PlayerUtil.input.movementSideways.toDouble()),
                    0.0,
                    MathUtil.roundAwayFromZero(PlayerUtil.input.movementForward.toDouble())
                ), flightSpeed.value.toFloat(), mc.player?.yaw!!)
                event.velocity = Vec3d(event.velocity.x, yMotion, event.velocity.z)
            }

            is EventCollisionShape -> {
                if (!mode.isSelected(2))
                    return@Consumer
                if (!event.collisionShape.isEmpty)
                    return@Consumer
                if (event.pos.y <= mc.player?.blockPos?.y!!) {
                    var yOffset = mc.player?.y!! - mc.player?.blockPos?.y!!
                    if (event.pos.y < mc.player?.blockPos?.y!!)
                        yOffset = 1.0
                    event.collisionShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, yOffset, 1.0)
                }
            }

            is EventJump -> {
                if (mode.isSelected(2))
                    event.cancelled = true
            }
        }
    }

}
