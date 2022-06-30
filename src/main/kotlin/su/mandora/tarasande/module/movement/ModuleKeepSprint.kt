package su.mandora.tarasande.module.movement

import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.event.Priority
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventAttack
import su.mandora.tarasande.event.EventAttackEntity
import su.mandora.tarasande.event.EventKeepSprint
import su.mandora.tarasande.event.EventVelocity
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleKeepSprint : Module("Keep sprint", "Prevents unsprinting by attacking", ModuleCategory.MOVEMENT) {

    private val horizontalSlowdown = ValueNumber(this, "Horizontal slowdown", 0.0, 1.0, 1.0, 0.1)
    private val unsprint = ValueBoolean(this, "Unsprint", false)

    private val knockbackAware = ValueBoolean(this, "Knockback-aware", false)
    private val packets = object : ValueMode(this, "Packets", true, "Velocity", "Explosion") {
        override fun isEnabled() = knockbackAware.value
    }

    private var prevVelocity: Vec3d? = null
    private var disabled = false

    @Priority(1001) // Velocity
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventAttackEntity -> {
                if (event.state != EventAttackEntity.State.PRE) return@Consumer
                prevVelocity = mc.player?.velocity
            }
            is EventKeepSprint -> {
                if (!disabled) {
                    if (!unsprint.value) event.sprinting = true
                    mc.player?.velocity = prevVelocity?.multiply(horizontalSlowdown.value, 1.0, horizontalSlowdown.value)
                }
            }
            is EventVelocity -> {
                if (knockbackAware.value && packets.isSelected(event.packet.ordinal) && !event.cancelled && event.velocityX * event.velocityX + event.velocityZ * event.velocityZ > 0.01) disabled = true
            }
            is EventAttack -> {
                if (mc.player?.isOnGround!!) disabled = false
            }
        }
    }

}