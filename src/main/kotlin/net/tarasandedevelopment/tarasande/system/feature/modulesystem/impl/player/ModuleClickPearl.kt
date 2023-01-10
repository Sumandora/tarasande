package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.item.EnderPearlItem
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.extension.minecraft.isMissHitResult
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleClickPearl : Module("Click pearl", "Auto switches to a ender pearl", ModuleCategory.PLAYER) {

    private var prevSlot: Int? = null
    private var state = State.IDLE

    init {
        registerEvent(EventPacket::class.java) { event ->
            if(event.type == EventPacket.Type.SEND && event.packet is PlayerInteractItemC2SPacket) {
                if(state == State.WAIT_FOR_THROW)
                    state = State.SWITCH_BACK
            }
        }
        registerEvent(EventTick::class.java) { event ->
            if(event.state == EventTick.State.PRE) {
                val pearlSlot = PlayerUtil.findSlot { it.value.item is EnderPearlItem }
                if(pearlSlot == null) {
                    state = State.IDLE // FUCK
                    return@registerEvent
                }
                if(state == State.WAIT_FOR_BUTTON_RELEASE && !mc.options.useKey.isPressed) {
                    state = State.IDLE
                    return@registerEvent
                }
                if(state == State.SWITCH_BACK && prevSlot != null) {
                    mc.player?.inventory?.selectedSlot = prevSlot
                    state = State.WAIT_FOR_BUTTON_RELEASE
                    return@registerEvent
                }
                if(state == State.IDLE && mc.options.useKey.isPressed && mc.crosshairTarget.isMissHitResult()) {
                    prevSlot = mc.player?.inventory?.selectedSlot
                    mc.player?.inventory?.selectedSlot = pearlSlot
                    state = State.WAIT_FOR_THROW
                }
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if(event.keyBinding == mc.options.useKey && state == State.WAIT_FOR_THROW)
                event.pressed = true
        }
    }

    enum class State {
        IDLE, WAIT_FOR_THROW, SWITCH_BACK, WAIT_FOR_BUTTON_RELEASE
    }
}