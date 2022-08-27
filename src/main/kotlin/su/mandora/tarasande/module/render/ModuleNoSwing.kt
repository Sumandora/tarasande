package su.mandora.tarasande.module.render

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventPacket
import su.mandora.tarasande.event.EventResetEquipProgress
import su.mandora.tarasande.event.EventSwing
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueMode
import java.util.function.Consumer

class ModuleNoSwing : Module("No swing", "Hides the hand swing animation", ModuleCategory.RENDER) {

    private val mode = ValueMode(this, "Mode", true, "Clientside", "Serverside")
    private val hand = object : ValueMode(this, "Hand", true, "Main hand", "Off hand") {
        override fun isEnabled() = mode.anySelected()
    }
    private val fixAnimations = object : ValueBoolean(this, "Fix animations", true) {
        override fun isEnabled() = mode.isSelected(0)
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPacket -> {
                if (event.type == EventPacket.Type.RECEIVE && event.packet is HandSwingC2SPacket)
                    if (mode.isSelected(1) && hand.isSelected(event.packet.hand.ordinal))
                        event.cancelled = true
            }

            is EventSwing -> {
                if (mode.isSelected(0))
                    if (hand.isSelected(event.hand.ordinal))
                        event.cancelled = true
            }

            is EventResetEquipProgress -> {
                if (fixAnimations.value)
                    event.cancelled = true
            }
        }
    }

}
