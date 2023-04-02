package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import su.mandora.tarasande.event.impl.*
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.isMissHitResult
import su.mandora.tarasande.util.player.container.ContainerUtil

class ModuleClickPearl : Module("Click pearl", "Auto switches to a ender pearl", ModuleCategory.PLAYER) {

    private var prevSlot: Int? = null
    private var state = State.IDLE

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.SEND && event.packet is PlayerInteractItemC2SPacket) {
                if (state == State.WAIT_FOR_THROW)
                    state = State.SWITCH_BACK
            }
        }
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                var pearlSlot = ContainerUtil.findSlot { it.value.isOf(Items.ENDER_PEARL) }
                if (pearlSlot == null)
                    if (mc.player?.offHandStack?.isOf(Items.ENDER_PEARL) == true)
                        pearlSlot = -1

                if (pearlSlot == null) {
                    state = State.IDLE // FUCK
                    return@registerEvent
                }
                if (state == State.WAIT_FOR_BUTTON_RELEASE && !mc.options.useKey.pressed) {
                    state = State.IDLE
                    return@registerEvent
                }
                if (state == State.SWITCH_BACK && prevSlot != null) {
                    mc.player?.inventory?.selectedSlot = prevSlot
                    state = State.WAIT_FOR_BUTTON_RELEASE
                    return@registerEvent
                }
                if (state == State.IDLE && mc.options.useKey.pressed && mc.crosshairTarget.isMissHitResult() && mc.player?.isUsingItem == false) {
                    if(mc.player?.inventory?.mainHandStack?.isOf(Items.ENDER_PEARL) == true)
                        return@registerEvent
                    prevSlot = mc.player?.inventory?.selectedSlot
                    mc.player?.inventory?.selectedSlot = pearlSlot
                    state = State.WAIT_FOR_THROW
                }
            }
        }
        registerEvent(EventAttack::class.java, 1) { event ->
            if (state != State.IDLE) {
                event.dirty = true
            }
        }
        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.useKey && state == State.WAIT_FOR_THROW)
                event.pressed = true
        }
        registerEvent(EventDisconnect::class.java) { event ->
            if(event.connection == mc.player?.networkHandler?.connection) {
                state = State.IDLE // Abort
            }
        }
    }

    enum class State {
        IDLE, WAIT_FOR_THROW, SWITCH_BACK, WAIT_FOR_BUTTON_RELEASE
    }
}