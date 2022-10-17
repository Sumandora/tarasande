package net.tarasandedevelopment.tarasande.module.player

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientConnection
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientPlayerInteractionManager
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber

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
                                (mc.interactionManager as IClientPlayerInteractionManager).tarasande_invokeSendSequencedPacket(mc.world) { sequence: Int ->
                                    PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, event.packet.pos, event.packet.direction, sequence).also {
                                        (mc.networkHandler?.connection as IClientConnection).tarasande_addForcePacket(it)
                                    }
                                }
                            }
                        }
                    }

                    PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK -> {
                        if (packets.isSelected(1)) {
                            for (i in 0 until multiplier.value.toInt()) {
                                (mc.interactionManager as IClientPlayerInteractionManager).tarasande_invokeSendSequencedPacket(mc.world) { sequence: Int ->
                                    PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, event.packet.pos, event.packet.direction, sequence).also {
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