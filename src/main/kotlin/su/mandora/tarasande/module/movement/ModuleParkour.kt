package su.mandora.tarasande.module.movement

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleParkour : Module("Parkour", "Jumps when falling off ledges", ModuleCategory.MOVEMENT) {

    private val detectionMethod = ValueMode(this, "Detection method", false, "Extrapolation", "Ground")
    private val extrapolation = object : ValueNumber(this, "Extrapolation", 0.0, 1.0, 10.0, 1.0) {
        override fun isEnabled() = detectionMethod.isSelected(0)
    }

    private var wasOnGround = false

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventUpdate ->
                if (event.state == EventUpdate.State.PRE) {
                    if (detectionMethod.isSelected(1) && wasOnGround && mc.player?.isOnGround == false && mc.player?.velocity?.y!! < 0.0) {
                        mc.player?.jump()
                    }
                    wasOnGround = mc.player?.isOnGround == true
                }

            is EventKeyBindingIsPressed -> {
                if (event.keyBinding == mc.options.jumpKey) {
                    if (detectionMethod.isSelected(0) && PlayerUtil.isOnEdge(extrapolation.value))
                        event.pressed = true
                }
            }
        }
    }

}