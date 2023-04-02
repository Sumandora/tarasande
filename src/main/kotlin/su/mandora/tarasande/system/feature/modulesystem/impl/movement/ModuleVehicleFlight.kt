package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.util.math.Direction
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.event.impl.EventMovement
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBind
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleVehicleFlight : Module("Vehicle flight", "Makes you fly with vehicles (e.g. boat, horses)", ModuleCategory.MOVEMENT) {

    private val verticalSpeed = ValueNumber(this, "Vertical speed", 0.0, 0.1, 1.0, 0.1)
    private val downwardsBind = ValueBind(this, "Downwards bind", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)

    init {
        registerEvent(EventMovement::class.java) { event ->
            if (event.entity != mc.player?.vehicle)
                return@registerEvent

            var sign = 0.0
            if (mc.options.jumpKey.isPressed) sign += 1.0
            if (downwardsBind.isPressed()) sign -= 1.0

            event.velocity = event.velocity.withAxis(Direction.Axis.Y, verticalSpeed.value * sign)
        }
    }
}
