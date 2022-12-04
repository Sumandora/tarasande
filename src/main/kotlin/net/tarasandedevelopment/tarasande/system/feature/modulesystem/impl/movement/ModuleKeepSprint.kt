package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.event.EventAttack
import net.tarasandedevelopment.tarasande.event.EventAttackEntity
import net.tarasandedevelopment.tarasande.event.EventKeepSprint
import net.tarasandedevelopment.tarasande.event.EventVelocity
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleKeepSprint : Module("Keep sprint", "Prevents unsprinting by attacking", ModuleCategory.MOVEMENT) {

    private val speedMultiplier = ValueNumber(this, "Speed multiplier", 0.0, 1.0, 1.0, 0.1)
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
                mc.player?.velocity = prevVelocity?.multiply(speedMultiplier.value, 1.0, speedMultiplier.value)
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