package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.injection.accessor.IClientConnection
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleCivBreak : Module("Civ break", "Breaks blocks multiple times", ModuleCategory.PLAYER) {

    private val packets = ValueMode(this, "Packets", true, "Start block breaking", "Stop block breaking")
    private val multiplier = object : ValueNumber(this, "Multiplier", 0.0, 100.0, 1000.0, 1.0) {
        override fun isEnabled() = packets.anySelected()
    }

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.SEND && event.packet is PlayerActionC2SPacket) {
                when (event.packet.action) {
                    PlayerActionC2SPacket.Action.START_DESTROY_BLOCK -> {
                        if (packets.isSelected(0)) {
                            for (i in 0 until multiplier.value.toInt()) {
                                mc.interactionManager!!.sendSequencedPacket(mc.world) {
                                    PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, event.packet.pos, event.packet.direction, it).also {
                                        (mc.networkHandler?.connection as IClientConnection).tarasande_addForcePacket(it)
                                    }
                                }
                            }
                        }
                    }

                    PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK -> {
                        if (packets.isSelected(1)) {
                            for (i in 0 until multiplier.value.toInt()) {
                                mc.interactionManager!!.sendSequencedPacket(mc.world) {
                                    PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, event.packet.pos, event.packet.direction, it).also {
                                        (mc.networkHandler?.connection as IClientConnection).tarasande_addForcePacket(it)
                                    }
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}
