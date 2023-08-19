package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.EventMovement
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.DEFAULT_WALK_SPEED
import su.mandora.tarasande.util.player.PlayerUtil
import kotlin.math.cos
import kotlin.math.sin

class ModuleStrafe : Module("Strafe", "Speeds up strafing", ModuleCategory.MOVEMENT) {

    private val mode = ValueMode(this, "Mode", true, "Ground", "Air")

    private val forceSpeed = ValueBoolean(this, "Force speed", false)
    private val speed = ValueNumber(this, "Speed", 0.0, DEFAULT_WALK_SPEED, 1.0, 0.01, isEnabled = { forceSpeed.value })

    init {
        for (i in 0 until mode.values.size)
            mode.select(i)
    }

    init {
        registerEvent(EventMovement::class.java, priority = 999 /* before speeds turn rate */) { event ->
            if (event.entity != mc.player)
                return@registerEvent

            if (!PlayerUtil.isPlayerMoving())
                return@registerEvent

            if (!mode.isSelected(if (mc.player!!.isOnGround) 0 else 1))
                return@registerEvent

            val moveSpeed = if(forceSpeed.value) speed.value else event.velocity.horizontalLength()
            val rad = Math.toRadians(PlayerUtil.getMoveDirection() + 90)

            event.velocity = Vec3d(
                cos(rad) * moveSpeed,
                event.velocity.y,
                sin(rad) * moveSpeed
            )
        }
    }

}