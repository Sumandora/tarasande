package su.mandora.tarasande.module.combat

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.event.Priority
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventAttackEntity
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleWTap : Module("W-Tap", "Automatically W/S-Taps for you", ModuleCategory.COMBAT) {

    private val mode = ValueMode(this, "Mode", false, "W-Tap", "S-Tap", "Packet")
    private val packets = object : ValueNumber(this, "Packets", 2.0, 2.0, 10.0, 2.0) {
        override fun isEnabled() = mode.isSelected(2)
    }

    var changeBinds = false

    @Priority(1001) // KillAura
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventUpdate -> {
                if (event.state == EventUpdate.State.POST) {
                    changeBinds = false
                }
            }
            is EventAttackEntity -> {
                if (event.state != EventAttackEntity.State.PRE) return@Consumer
                changeBinds = true
                if (mode.isSelected(2)) {
                    if (mc.player?.isSprinting!!) mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING))

                    for (i in 0..(packets.value - 2.0).toInt()) {
                        if (i % 2 == 0) mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING))
                        else mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING))
                    }

                    if (mc.player?.isSprinting!!) mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING))
                }
            }
            is EventKeyBindingIsPressed -> {
                if (PlayerUtil.movementKeys.contains(event.keyBinding)) {
                    if (changeBinds) {
                        when {
                            mode.isSelected(0) -> {
                                event.pressed = false
                            }
                            mode.isSelected(1) -> {
                                event.pressed = !event.pressed
                            }
                        }
                    }
                }
            }
        }
    }

}