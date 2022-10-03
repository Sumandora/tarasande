package net.tarasandedevelopment.tarasande.module.movement

import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventMovement
import net.tarasandedevelopment.tarasande.mixin.accessor.IVec3d
import net.tarasandedevelopment.tarasande.value.ValueBind
import net.tarasandedevelopment.tarasande.value.ValueNumber
import org.lwjgl.glfw.GLFW
import java.util.function.Consumer

class ModuleVehicleFlight : Module("Vehicle flight", "Makes you fly with vehicles (e.g. boat, horses)", ModuleCategory.MOVEMENT) {

    private val verticalSpeed = ValueNumber(this, "Vertical speed", 0.0, 0.1, 1.0, 0.1)
    private val downwardsBind = ValueBind(this, "Downwards bind", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventMovement) {
            val vehicle = mc.player?.vehicle
            if (vehicle != null) {
                if (event.entity == vehicle) {
                    var sign = 0.0
                    if (mc.options.jumpKey.isPressed) sign += 1.0
                    if (downwardsBind.isPressed()) sign -= 1.0
                    (event.velocity as IVec3d).tarasande_setY(verticalSpeed.value * sign)
                }
            }
        }
    }
}