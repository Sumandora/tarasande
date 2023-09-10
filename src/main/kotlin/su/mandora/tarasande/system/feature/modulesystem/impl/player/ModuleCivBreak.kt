package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.injection.accessor.IClientConnection
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleCivBreak : Module("Civ break", "Breaks blocks multiple times", ModuleCategory.PLAYER) {

    private val packets = ValueMode(this, "Packets", true, "Start block breaking", "Stop block breaking")
    private val multiplier = ValueNumber(this, "Multiplier", 0.0, 100.0, 1000.0, 1.0, isEnabled = { packets.anySelected() })

    private fun sendPackets(startBreaking: Boolean, stopBreaking: Boolean, pos: BlockPos, direction: Direction) {
        val clientConnection = mc.networkHandler?.connection as? IClientConnection ?: return
        for (i in 0 until multiplier.value.toInt()) {
            if (startBreaking) clientConnection.tarasande_forceSend(PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, PlayerUtil.getSequence()))
            if (stopBreaking) clientConnection.tarasande_forceSend(PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, PlayerUtil.getSequence()))
        }
    }

    private var queuedBlock: Pair<BlockPos, Direction>? = null

    override fun onDisable() {
        queuedBlock = null
    }

    init {
        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.POST && mc.player != null) {
                if (queuedBlock != null) {
                    sendPackets(startBreaking = true, stopBreaking = true, queuedBlock!!.first, queuedBlock!!.second)
                    queuedBlock = null
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
                            if (!packets.isSelected(0)) {
                                sendPackets(startBreaking = false, stopBreaking = true, event.packet.pos, event.packet.direction)
                            } else {
                                queuedBlock = Pair(event.packet.pos, event.packet.direction)
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}
