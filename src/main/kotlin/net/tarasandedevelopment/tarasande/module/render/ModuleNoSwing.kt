package net.tarasandedevelopment.tarasande.module.render

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventSwing
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueMode

class ModuleNoSwing : Module("No swing", "Hides the hand swing animation", ModuleCategory.RENDER) {

    private val mode = ValueMode(this, "Mode", true, "Clientside", "Serverside")
    private val hand = object : ValueMode(this, "Hand", true, "Main hand", "Off hand") {
        override fun isEnabled() = mode.anySelected()
    }
    val disableEquipProgress = ValueBoolean(this, "Disable equip progress", true)

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE && event.packet is HandSwingC2SPacket)
                if (mode.isSelected(1) && hand.isSelected(event.packet.hand.ordinal))
                    event.cancelled = true
        }

        registerEvent(EventSwing::class.java) { event ->
            if (mode.isSelected(0))
                if (hand.isSelected(event.hand.ordinal))
                    event.cancelled = true
        }
    }
}
