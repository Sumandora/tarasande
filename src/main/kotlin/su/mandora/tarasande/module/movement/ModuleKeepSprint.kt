package su.mandora.tarasande.module.movement

import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventAttackEntity
import su.mandora.tarasande.event.EventKeepSprint
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleKeepSprint : Module("Keep sprint", "Prevents unsprinting by attacking", ModuleCategory.MOVEMENT) {

    private val horizontalSlowdown = ValueNumber(this, "Horizontal slowdown", 0.0, 1.0, 1.0, 0.1)
    private val unsprint = ValueBoolean(this, "Unsprint", false)

    private var prevVelocity: Vec3d? = null

    val eventConsumer = Consumer<Event> { event ->
        when(event) {
            is EventAttackEntity -> {
                prevVelocity = mc.player?.velocity
            }
            is EventKeepSprint -> {
                if(!unsprint.value)
                    event.sprinting = true
                mc.player?.velocity = prevVelocity?.multiply(horizontalSlowdown.value, 1.0, horizontalSlowdown.value)
            }
        }
    }

}