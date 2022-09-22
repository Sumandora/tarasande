package su.mandora.tarasande.module.movement

import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.event.EventVelocity
import su.mandora.tarasande.util.extension.plus
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import kotlin.math.sqrt

class ModuleVelocity : Module("Velocity", "Reduces knockback", ModuleCategory.MOVEMENT) {

    private val packets = ValueMode(this, "Packets", true, "Velocity", "Explosion")
    private val mode = ValueMode(this, "Mode", false, "Cancel", "Custom", "Jump")
    private val horizontal = object : ValueNumber(this, "Horizontal", -1.0, 0.0, 1.0, 0.01) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val vertical = object : ValueNumber(this, "Vertical", 0.0, 0.0, 1.0, 0.01) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val delay = object : ValueNumber(this, "Delay", 0.0, 0.0, 20.0, 1.0) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val addition = object : ValueBoolean(this, "Addition", false) {
        override fun isEnabled() = delay.value > 0.0
    }
    private val changeDirection = object : ValueBoolean(this, "Change direction", false) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val chance = object : ValueNumber(this, "Chance", 0.0, 75.0, 100.0, 1.0) {
        override fun isEnabled() = mode.isSelected(2)
    }

    private var receivedKnockback = false
    private var lastVelocity: Vec3d? = null
    private var isJumping = false
    private var delays = ArrayList<Pair<Vec3d, Int>>()

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventVelocity -> {
                if (!packets.isSelected(event.packet.ordinal)) return@Consumer

                when {
                    mode.isSelected(0) -> {
                        event.cancelled = true
                    }

                    mode.isSelected(1) -> {
                        if (delay.value > 0.0) {
                            delays.add(Pair(Vec3d(event.velocityX * horizontal.value, event.velocityY * vertical.value, event.velocityZ * horizontal.value), mc.player?.age!! + delay.value.toInt()))
                        } else {
                            val newVelocity = if (changeDirection.value) Rotation(PlayerUtil.getMoveDirection().toFloat(), 0.0f).forwardVector(sqrt(event.velocityX * event.velocityX + event.velocityZ * event.velocityZ)) else Vec3d(event.velocityX, 0.0, event.velocityZ)
                            event.velocityX = newVelocity.x * horizontal.value
                            event.velocityY *= vertical.value
                            event.velocityZ = newVelocity.z * horizontal.value
                        }
                    }

                    else -> {
                        if (ThreadLocalRandom.current().nextInt(100) <= chance.value) {
                            lastVelocity = Vec3d(event.velocityX, event.velocityY, event.velocityZ)
                            receivedKnockback = true
                        }
                    }
                }
            }

            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE) {
                    val iterator = delays.iterator()
                    while (iterator.hasNext()) {
                        val pair = iterator.next()
                        if (pair.second <= mc.player?.age!!) {
                            val newVelocity = if (changeDirection.value) Rotation(PlayerUtil.getMoveDirection().toFloat(), 0.0f).forwardVector(pair.first.horizontalLength()) else pair.first
                            mc.player?.velocity = if (addition.value) mc.player?.velocity!! + newVelocity else newVelocity
                            iterator.remove()
                        }
                    }
                    when {
                        mode.isSelected(2) -> {
                            if (receivedKnockback) {
                                if (lastVelocity?.horizontalLengthSquared()!! > 0.01 && mc.player?.isOnGround!!)
                                    isJumping = true

                                receivedKnockback = false
                            }
                        }

                        else -> {
                            receivedKnockback = false
                        }
                    }
                    if (!mc.player?.isOnGround!!) {
                        isJumping = false
                    }
                }
            }

            is EventKeyBindingIsPressed -> {
                if (event.keyBinding == mc.options.jumpKey)
                    event.pressed = event.pressed || isJumping
            }
        }
    }

}