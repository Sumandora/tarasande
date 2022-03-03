package su.mandora.tarasande.module.movement

import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.event.EventVelocity
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

class ModuleVelocity : Module("Velocity", "Reduces knockback", ModuleCategory.MOVEMENT) {

    private val packets = ValueMode(this, "Packets", true, "Velocity", "Explosion")
    private val mode = ValueMode(this, "Mode", false, "Cancel", "Custom", "Jump")
    private val horizontal = object : ValueNumber(this, "Horizontal", -1.0, 0.0, 1.0, 0.01) {
        override fun isVisible() = mode.isSelected(1)
    }
    private val vertical = object : ValueNumber(this, "Vertical", 0.0, 0.0, 1.0, 0.01) {
        override fun isVisible() = mode.isSelected(1)
    }
    private val chance = object : ValueNumber(this, "Chance", 0.0, 75.0, 100.0, 1.0) {
        override fun isVisible() = mode.isSelected(2)
    }

    private var receivedKnockback = false
    private var lastVelocity: Vec3d? = null
    private var isJumping = false

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventVelocity -> {
                when (event.packet) {
                    EventVelocity.Packet.VELOCITY -> if (!packets.isSelected(0)) return@Consumer
                    EventVelocity.Packet.EXPLOSION -> if (!packets.isSelected(1)) return@Consumer
                }

                when {
                    mode.isSelected(0) -> {
                        event.setCancelled()
                    }
                    mode.isSelected(1) -> {
                        event.velocityX *= horizontal.value
                        event.velocityY *= vertical.value
                        event.velocityZ *= horizontal.value
                    }
                    else -> {
                        lastVelocity = Vec3d(event.velocityX, event.velocityY, event.velocityZ)
                        receivedKnockback = true
                    }
                }
            }
            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE) {
                    when {
                        mode.isSelected(2) -> {
                            if (receivedKnockback) {
                                if (lastVelocity?.horizontalLengthSquared()!! > 0.01 && mc.player?.isOnGround!! && ThreadLocalRandom.current().nextInt(100) <= chance.value)
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
                if (event.keyBinding == mc.options.jumpKey && isJumping)
                    event.pressed = true
            }
        }
    }

}