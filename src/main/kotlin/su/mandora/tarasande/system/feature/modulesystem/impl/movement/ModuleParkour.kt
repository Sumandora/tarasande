package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import su.mandora.tarasande.event.impl.EventKeyBindingIsPressed
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleParkour : Module("Parkour", "Jumps before falling off ledges", ModuleCategory.MOVEMENT) {

    private val detectionMethod = ValueMode(this, "Detection method", false, "Extrapolation", "Ground")
    private val extrapolation = ValueNumber(this, "Extrapolation", 0.0, 1.0, 10.0, 1.0, isEnabled = { detectionMethod.isSelected(0) })
    private val ignoreWhenSneaking = ValueBoolean(this, "Ignore when sneaking", true)

    private var wasOnGround = false

    override fun onEnable() {
        wasOnGround = false
    }

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if(!ignoreWhenSneaking.value || !mc.player!!.isSneaking)
                    if (detectionMethod.isSelected(1) && wasOnGround && mc.player?.isOnGround == false && mc.player?.velocity?.y!! < 0.0)
                        mc.player?.jump()

                wasOnGround = mc.player?.isOnGround == true
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.jumpKey) {
                if(!ignoreWhenSneaking.value || !mc.player!!.isSneaking)
                    if (detectionMethod.isSelected(0) && PlayerUtil.isOnEdge(extrapolation.value))
                        event.pressed = true
            }
        }
    }
}
