package su.mandora.tarasande.module.combat

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventAttackEntity
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleWTap : Module("W-Tap", "Automatically W/S-Taps for you", ModuleCategory.COMBAT) {

    private val mode = ValueMode(this, "Mode", false, "W-Tap", "S-Tap", "Packet")
    private val packets = object : ValueNumber(this, "Packets", 2.0, 2.0, 10.0, 2.0) {
        override fun isVisible() = mode.isSelected(2)
    }

    private val movementKeys = arrayListOf(
        mc.options.keyForward,
        mc.options.keyLeft,
        mc.options.keyBack,
        mc.options.keyRight
    )

    var lastAttack = -1

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventAttackEntity -> {
                lastAttack = mc.player?.age!!
                if (mode.isSelected(2)) {
                    if(mc.player?.isSprinting!!)
                        mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING))

                    for(i in 0..(packets.value - 2.0).toInt()) {
                        if(i % 2 == 0)
                            mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING))
                        else
                            mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING))
                    }

                    if(mc.player?.isSprinting!!)
                        mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING))
                }
            }
            is EventKeyBindingIsPressed -> {
                if(lastAttack > mc.player?.age!!)
                    lastAttack = -1
                if(mc.player?.age!! == lastAttack) {
                    when {
                        mode.isSelected(0) -> {
                            if(movementKeys.contains(event.keyBinding))
                                event.pressed = false
                        }
                        mode.isSelected(1) -> {
                            if(movementKeys.contains(event.keyBinding))
                                event.pressed = !event.pressed
                        }
                    }
                }
            }
        }
    }

}