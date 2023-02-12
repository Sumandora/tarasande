package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.injection.accessor.IClientConnection
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleCivBreak : Module("Civ break", "Breaks blocks multiple times", ModuleCategory.PLAYER) {

    private val packets = ValueMode(this, "Packets", true, "Start block breaking", "Stop block breaking")
    private val multiplier = ValueNumber(this, "Multiplier", 0.0, 100.0, 1000.0, 1.0, isEnabled = { packets.anySelected() })

    private fun sendPackets(startBreaking: Boolean, stopBreaking: Boolean, pos: BlockPos, direction: Direction) {
        for (i in 0 until multiplier.value.toInt()) {
            if (startBreaking)
                (mc.networkHandler?.connection as IClientConnection).tarasande_addForcePacket(
                    PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, PlayerUtil.getSequence())
                )
            if (stopBreaking)
                (mc.networkHandler?.connection as IClientConnection).tarasande_addForcePacket(
                    PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, PlayerUtil.getSequence())
                )
        }
    }

    private var queriedBlock: Pair<BlockPos, Direction>? = null

    init {
        registerEvent(EventTick::class.java) { event ->
            if(event.state == EventTick.State.POST) {
                if(queriedBlock != null) {
                    sendPackets(startBreaking = true, stopBreaking = true, queriedBlock!!.first, queriedBlock!!.second)
                    queriedBlock = null
                }
            }
        }
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.SEND && event.packet is PlayerActionC2SPacket) {
                when (event.packet.action) {
                    PlayerActionC2SPacket.Action.START_DESTROY_BLOCK -> {
                        if (packets.isSelected(0) && !packets.isSelected(1)) {
                            sendPackets(startBreaking = true, stopBreaking = false, event.packet.pos, event.packet.direction)
                        }
                    }

                    PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK -> {
                        if (packets.isSelected(1)) {
                            if(!packets.isSelected(0)) {
                                sendPackets(startBreaking = false, stopBreaking = true, event.packet.pos, event.packet.direction)
                            } else {
                                queriedBlock = Pair(event.packet.pos, event.packet.direction)
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}
