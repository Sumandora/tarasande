package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleAutoLog : Module("Auto log", "Disconnects when death is imminent", ModuleCategory.COMBAT) {

    private val disconnectWhen = ValueMode(this, "Triggers", true, "Low health", "Fall distance", "Targeted entity")
    private val health = object : ValueNumber(this, "Health", 0.0, 0.4, 1.0, 0.01) {
        override fun isEnabled() = disconnectWhen.isSelected(0)
    }
    private val fallDistance = object : ValueNumber(this, "Fall distance", 0.0, 3.0, 10.0, 0.1) {
        override fun isEnabled() = disconnectWhen.isSelected(1)
    }

    init {
        registerEvent(EventPacket::class.java) { event ->
            if(event.type == EventPacket.Type.RECEIVE && event.packet is HealthUpdateS2CPacket) {
                if(disconnectWhen.isSelected(0) && event.packet.health / mc.player?.maxHealth!! <= health.value)
                    PlayerUtil.disconnect()
            }
        }
        registerEvent(EventUpdate::class.java) { event ->
            if(event.state == EventUpdate.State.PRE_PACKET) {
                if(disconnectWhen.isSelected(1) && mc.player?.fallDistance!! > fallDistance.value)
                    PlayerUtil.disconnect()

                if(disconnectWhen.isSelected(2) && mc.world?.players?.any { PlayerUtil.isAttackable(it) } == true)
                    PlayerUtil.disconnect()
            }
        }
    }

}