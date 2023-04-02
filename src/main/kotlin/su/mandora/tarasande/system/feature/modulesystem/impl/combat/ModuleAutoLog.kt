package su.mandora.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleAutoLog : Module("Auto log", "Disconnects when death is imminent", ModuleCategory.COMBAT) {

    private val disconnectWhen = ValueMode(this, "Triggers", true, "Low health", "Fall distance", "Targeted entity")
    private val health = ValueNumber(this, "Health", 0.0, 0.4, 1.0, 0.01, isEnabled = { disconnectWhen.isSelected(0) })
    private val fallDistance = ValueNumber(this, "Fall distance", 0.0, 3.0, 10.0, 0.1, isEnabled = { disconnectWhen.isSelected(1) })

    init {
        registerEvent(EventPacket::class.java, Int.MAX_VALUE /* has to come after everything, to prevent NPEs */) { event ->
            if(event.type == EventPacket.Type.RECEIVE && event.packet is HealthUpdateS2CPacket) {
                if(disconnectWhen.isSelected(0) && event.packet.health / mc.player?.maxHealth!! <= health.value)
                    PlayerUtil.disconnect()
            }
        }
        registerEvent(EventUpdate::class.java, Int.MAX_VALUE /* has to come after everything, to prevent NPEs */) { event ->
            if(event.state == EventUpdate.State.PRE_PACKET) {
                if(disconnectWhen.isSelected(1) && mc.player?.fallDistance!! > fallDistance.value)
                    PlayerUtil.disconnect()

                if(disconnectWhen.isSelected(2) && mc.world?.players?.any { PlayerUtil.isAttackable(it) } == true)
                    PlayerUtil.disconnect()
            }
        }
    }
}
