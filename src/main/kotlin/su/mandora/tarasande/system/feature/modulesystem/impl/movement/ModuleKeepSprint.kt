package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.EventAttack
import su.mandora.tarasande.event.impl.EventAttackEntity
import su.mandora.tarasande.event.impl.EventKeepSprint
import su.mandora.tarasande.event.impl.EventVelocity
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.UNSPRINT_SPEED_REDUCTION

class ModuleKeepSprint : Module("Keep sprint", "Prevents unsprinting by attacking", ModuleCategory.MOVEMENT) {

    private val speedMultiplier = ValueNumber(this, "Speed multiplier", 0.0, 1.0, 1.0, 0.1)
    private val unsprint = ValueBoolean(this, "Unsprint", false)

    private val knockbackAware = ValueBoolean(this, "Knockback-aware", false)
    private val packets = ValueMode(this, "Packets", true, "Velocity", "Explosion", isEnabled = { knockbackAware.value })
    private val reducingSpeedMultiplier = ValueNumber(this, "Reducing speed multiplier", 0.0, UNSPRINT_SPEED_REDUCTION, 1.0, 0.1, isEnabled = { knockbackAware.value })
    private val unsprintWhenReducing = ValueBoolean(this, "Unsprint when reducing", true, isEnabled = { knockbackAware.value })

    private var prevVelocity: Vec3d? = null
    private var takingKnockback = false

    init {
        registerEvent(EventAttackEntity::class.java) { event ->
            if (event.state != EventAttackEntity.State.PRE) return@registerEvent
            prevVelocity = mc.player?.velocity
        }

        registerEvent(EventKeepSprint::class.java) { event ->
            val unsprint = if (takingKnockback) unsprintWhenReducing else this.unsprint
            val speedMultiplier = if (takingKnockback) reducingSpeedMultiplier else this.speedMultiplier

            if (!unsprint.value) event.sprinting = true
            mc.player?.velocity = prevVelocity?.multiply(speedMultiplier.value, 1.0, speedMultiplier.value)
        }

        registerEvent(EventVelocity::class.java, 999) { event ->
            if (knockbackAware.value && packets.isSelected(event.packet.ordinal) && !event.cancelled && event.velocityX * event.velocityX + event.velocityZ * event.velocityZ > 0.01)
                takingKnockback = true
        }

        registerEvent(EventAttack::class.java) {
            if (mc.player?.isOnGround!!)
                takingKnockback = false
        }
    }

}