package su.mandora.tarasande.module.movement

import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventMovement
import su.mandora.tarasande.mixin.accessor.IVec3d
import su.mandora.tarasande.value.ValueKeyBind
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleVehicleFlight : Module("Vehicle flight", "Makes you fly with vehicles (e.g. boat, horses)", ModuleCategory.MOVEMENT) {

    private val verticalSpeed = ValueNumber(this, "Vertical speed", 0.0, 0.1, 1.0, 0.1)
    private val downwardsKeybind = ValueKeyBind(this, "Downwards Keybind", GLFW.GLFW_KEY_UNKNOWN)

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventMovement) {
            val vehicle = mc.player?.vehicle
            if (vehicle != null) {
                if (event.entity == vehicle) {
                    var sign = 0.0
                    if (mc.options.keyJump.isPressed)
                        sign += 1.0
                    if (downwardsKeybind.isPressed())
                        sign -= 1.0
                    (event.velocity as IVec3d).setY(verticalSpeed.value * sign)
                }
            }
        }
    }
}