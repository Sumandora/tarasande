package su.mandora.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import su.mandora.tarasande.event.impl.EventKeyBindingIsPressed
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.container.ContainerUtil

class ModuleAutoRescuePlatform : Module("Auto rescue platform", "Uses rescue platforms automatically", ModuleCategory.MISC) {

    private val fallDistance = ValueNumber(this, "Fall distance", 0.0, 6.0, 10.0, 0.1)
    private val void = ValueBoolean(this, "Void", true)

    private var prevSlot: Int? = null
    private var state = State.IDLE

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.SEND && event.packet is PlayerInteractItemC2SPacket) {
                if (state == State.WAIT_FOR_ACTIVATION)
                    state = State.SWITCH_BACK
            }
        }
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                val rescuePlatformSlot = ContainerUtil.findSlot { it.value.item == Items.BLAZE_ROD }
                if (rescuePlatformSlot == null) {
                    state = State.IDLE
                    return@registerEvent
                }
                if (state == State.SWITCH_BACK && prevSlot != null) {
                    mc.player?.inventory?.selectedSlot = prevSlot
                    state = State.IDLE
                    return@registerEvent
                }
                if (state == State.IDLE && mc.player?.fallDistance!! > fallDistance.value && (!void.value || PlayerUtil.predictFallDistance() == null)) {
                    prevSlot = mc.player?.inventory?.selectedSlot
                    mc.player?.inventory?.selectedSlot = rescuePlatformSlot
                    state = State.WAIT_FOR_ACTIVATION
                }
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.useKey && state == State.WAIT_FOR_ACTIVATION)
                event.pressed = true
        }
    }

    enum class State {
        IDLE, WAIT_FOR_ACTIVATION, SWITCH_BACK
    }
}