package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.EventMovement
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.PlayerUtil
import kotlin.math.cos
import kotlin.math.sin

class ModuleVehicleSpeed : Module("Vehicle speed", "Modifies vehicle movement speeds", ModuleCategory.MOVEMENT) {

    private val speed = ValueNumber(this, "Speed", 0.0, 1.0, 3.0, 0.1)

    private val hardStop = ValueBoolean(this, "Hard stop", true)

    init {
        registerEvent(EventMovement::class.java) { event ->
            if (event.entity != mc.player?.vehicle)
                return@registerEvent

            if (!PlayerUtil.isPlayerMoving()) {
                if(hardStop.value)
                    event.velocity = Vec3d.ZERO.withAxis(Direction.Axis.Y, event.velocity.y)
                return@registerEvent
            }

            val rad = Math.toRadians(PlayerUtil.getMoveDirection() + 90)

            event.velocity = Vec3d(
                cos(rad) * speed.value,
                event.velocity.y,
                sin(rad) * speed.value
            )
        }
    }
}
