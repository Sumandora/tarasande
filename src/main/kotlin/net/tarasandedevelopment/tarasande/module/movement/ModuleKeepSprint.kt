package net.tarasandedevelopment.tarasande.module.movement

import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventAttack
import net.tarasandedevelopment.tarasande.event.EventAttackEntity
import net.tarasandedevelopment.tarasande.event.EventKeepSprint
import net.tarasandedevelopment.tarasande.event.EventVelocity
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber

class ModuleKeepSprint : Module("Keep sprint", "Prevents unsprinting by attacking", ModuleCategory.MOVEMENT) {

    private val horizontalSlowdown = ValueNumber(this, "Horizontal slowdown", 0.0, 1.0, 1.0, 0.1)
    private val unsprint = ValueBoolean(this, "Unsprint", false)

    private val knockbackAware = ValueBoolean(this, "Knockback-aware", false)
    private val packets = object : ValueMode(this, "Packets", true, "Velocity", "Explosion") {
        override fun isEnabled() = knockbackAware.value
    }

    private var prevVelocity: Vec3d? = null
    private var disabled = false

    init {
        registerEvent(EventAttackEntity::class.java) { event ->
            if (event.state != EventAttackEntity.State.PRE) return@registerEvent
            prevVelocity = mc.player?.velocity
        }

        registerEvent(EventKeepSprint::class.java) { event ->
            if (!disabled) {
                if (!unsprint.value) event.sprinting = true
                mc.player?.velocity = prevVelocity?.multiply(horizontalSlowdown.value, 1.0, horizontalSlowdown.value)
            }
        }

        registerEvent(EventVelocity::class.java, 999) { event ->
            if (knockbackAware.value && packets.isSelected(event.packet.ordinal) && !event.cancelled && event.velocityX * event.velocityX + event.velocityZ * event.velocityZ > 0.01)
                disabled = true
        }

        registerEvent(EventAttack::class.java) {
            if (mc.player?.isOnGround!!)
                disabled = false
        }
    }

}