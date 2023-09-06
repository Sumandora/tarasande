package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.EventKeyBindingIsPressed
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.event.impl.EventVelocity
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.math.plus
import su.mandora.tarasande.util.extension.minecraft.math.times
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.feature.rotation.api.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import java.util.concurrent.ThreadLocalRandom

class ModuleVelocity : Module("Velocity", "Reduces knock-back", ModuleCategory.MOVEMENT) {

    private val packets = ValueMode(this, "Packets", true, "Velocity", "Explosion")
    private val mode = ValueMode(this, "Mode", false, "Cancel", "Custom", "Jump")
    private val horizontal = ValueNumber(this, "Horizontal", -1.0, 0.0, 1.0, 0.01, isEnabled = { mode.isSelected(1) })
    private val vertical = ValueNumber(this, "Vertical", 0.0, 0.0, 1.0, 0.01, isEnabled = { mode.isSelected(1) })
    private val delay = ValueNumber(this, "Delay", 0.0, 0.0, 20.0, 1.0, isEnabled = { mode.isSelected(1) })
    private val addition = ValueMode(this, "Addition", false, "Never", "Depending on packet", "Always", isEnabled = { mode.isSelected(1) && delay.value > 0.0 })
    private val changeDirection = ValueBoolean(this, "Change direction", false, isEnabled = { mode.isSelected(1) })
    private val chance = ValueNumber(this, "Chance", 0.0, 75.0, 100.0, 1.0)
    private val ignoreTinyVelocity = ValueNumber(this, "Ignore tiny velocity", 0.0, 0.0, 0.5, 0.01)
    private val onlyWhenFacing = ValueBoolean(this, "Only when facing", false)
    private val facingThreshold = ValueNumber(this, "Facing threshold", 0.0, 60.0, 360.0, 15.0, isEnabled = { onlyWhenFacing.value })

    init {
        packets.select(0)
        addition.select(1) // Default, that's the most normal one
    }

    private var receivedKnockback = false
    private var lastVelocity: Vec3d? = null
    private var isJumping = false
    private var delays = ArrayList<Triple<Vec3d, Int, EventVelocity.Packet>>()

    init {
        registerEvent(EventVelocity::class.java) { event ->
            if (ThreadLocalRandom.current().nextInt(100) > chance.value) return@registerEvent
            if (!packets.isSelected(event.packet.ordinal)) return@registerEvent
            if (event.cancelled) return@registerEvent

            val velocityVector = Vec3d(event.velocityX, event.velocityY, event.velocityZ)
            if (velocityVector.horizontalLengthSquared() <= ignoreTinyVelocity.value * ignoreTinyVelocity.value)
                return@registerEvent

            if (onlyWhenFacing.value) {
                val deltaRotation = RotationUtil.getYaw(velocityVector) - PlayerUtil.getMoveDirection()
                if (deltaRotation > facingThreshold.value)
                    return@registerEvent
            }

            when {
                mode.isSelected(0) -> {
                    event.cancelled = true
                }

                mode.isSelected(1) -> {
                    if (delay.value > 0.0) {
                        delays.add(Triple(velocityVector.multiply(horizontal.value, vertical.value, horizontal.value), mc.player?.age!! + delay.value.toInt(), event.packet))
                    } else {
                        val newVelocity =
                            if (changeDirection.value)
                                (Rotation(PlayerUtil.getMoveDirection().toFloat(), 0F).forwardVector() * velocityVector.horizontalLength())
                                    .withAxis(Direction.Axis.Y, velocityVector.y)
                            else
                                velocityVector
                        event.velocityX = newVelocity.x * horizontal.value
                        event.velocityY = newVelocity.y * vertical.value
                        event.velocityZ = newVelocity.z * horizontal.value
                    }
                }

                else -> {
                    lastVelocity = velocityVector
                    receivedKnockback = true
                }
            }
        }

        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                val iterator = delays.iterator()
                while (iterator.hasNext()) {
                    val triple = iterator.next()
                    if (triple.second <= mc.player?.age!!) {
                        val newVelocity =
                            if (changeDirection.value && PlayerUtil.isPlayerMoving())
                                Rotation(PlayerUtil.getMoveDirection().toFloat(), 0F).forwardVector() * triple.first.horizontalLength()
                            else
                                triple.first
                        mc.player?.velocity =
                            if (addition.isSelected(2) || (addition.isSelected(1) && triple.third == EventVelocity.Packet.EXPLOSION))
                                mc.player?.velocity!! + newVelocity
                            else
                                newVelocity
                        iterator.remove()
                    }
                }
                when {
                    mode.isSelected(2) -> {
                        if (receivedKnockback) {
                            if (mc.player?.isOnGround!!)
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

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.jumpKey)
                event.pressed = event.pressed || isJumping
        }
    }
}
