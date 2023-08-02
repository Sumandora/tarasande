package su.mandora.tarasande.system.feature.modulesystem.impl.render

import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventSwing
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNoSwing : Module("No swing", "Hides the hand swing animation", ModuleCategory.RENDER) {

    private val mode = ValueMode(this, "Mode", true, "Clientside", "Serverside")
    private val hand = ValueMode(this, "Hand", true, "Main hand", "Off hand", isEnabled = { mode.anySelected() })
    val disableEquipProgress = ValueBoolean(this, "Disable equip progress", true)

    fun canEquipBeIgnored(oldItem: ItemStack?, newItem: ItemStack?): Boolean {
        if(oldItem == null || newItem == null)
            return false

        if (oldItem.isEmpty && newItem.isEmpty)
            return true

        if (oldItem.isEmpty || newItem.isEmpty)
            return false

        return oldItem.isOf(newItem.item)
    }

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
