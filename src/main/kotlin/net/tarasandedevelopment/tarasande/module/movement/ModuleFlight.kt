package net.tarasandedevelopment.tarasande.module.movement

import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.event.Priority
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventCollisionShape
import net.tarasandedevelopment.tarasande.event.EventJump
import net.tarasandedevelopment.tarasande.event.EventMovement
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mixin.accessor.IEntity
import net.tarasandedevelopment.tarasande.mixin.accessor.IVec3d
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleFlight : Module("Flight", "Allows flight in non-creative modes", ModuleCategory.MOVEMENT) {

    val mode = ValueMode(this, "Mode", false, "Vanilla", "Motion", "Air walk", "Motion reset")
    val flightSpeed = object : ValueNumber(this, "Flight speed", 0.0, 1.0, 5.0, 0.1) {
        override fun isEnabled() = mode.isSelected(0) || mode.isSelected(1)
    }
    private val baseYMotion = object : ValueNumber(this, "Base Y-motion", -1.0, 0.0, 1.0, 0.01) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val tickFrequency = object : ValueNumber(this, "Tick frequency", 1.0, 2.0, 10.0, 1.0) {
        override fun isEnabled() = mode.isSelected(3)
    }

    @Priority(1002) // we need to override step
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventMovement -> {
                if (!mode.isSelected(1))
                    return@Consumer
                if (event.entity != mc.player)
                    return@Consumer
                var yMotion = 0.0
                if (PlayerUtil.input.jumping)
                    yMotion += flightSpeed.value
                if (PlayerUtil.input.sneaking)
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

            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE)
                    if (mode.isSelected(3))
                        if (mc.player?.age?.mod(tickFrequency.value.toInt()) == 0)
                            (mc.player?.velocity as IVec3d).tarasande_setY(0.0)
            }
        }
    }

}
