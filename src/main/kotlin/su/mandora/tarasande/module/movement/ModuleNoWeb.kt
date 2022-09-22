package su.mandora.tarasande.module.movement

import net.minecraft.block.CobwebBlock
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventBlockCollision
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleNoWeb : Module("No web", "Prevents cobwebs' slowdown", ModuleCategory.MOVEMENT) {

    private val horizontalSlowdown = ValueNumber(this, "Horizontal slowdown", 0.0, 0.25, 1.0, 0.01)
    private val verticalSlowdown = ValueNumber(this, "Vertical slowdown", 0.0, 0.05, 1.0, 0.01)

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventBlockCollision) {
            if (event.entity != mc.player)
                return@Consumer
            if (event.state.block !is CobwebBlock)
                return@Consumer

            event.cancelled = true
            mc.player?.slowMovement(event.state, Vec3d(horizontalSlowdown.value, verticalSlowdown.value, horizontalSlowdown.value))
        }
    }

}