package net.tarasandedevelopment.tarasande.module.movement

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.event.Priority
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.mixin.accessor.IKeyBinding
import net.tarasandedevelopment.tarasande.screen.menu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.menu.valuecomponent.ValueComponentRegistry
import net.tarasandedevelopment.tarasande.screen.menu.valuecomponent.ValueComponentText
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueMode
import java.util.function.Consumer

class ModuleInventoryMove : Module("Inventory move", "Allows you to move while in inventory", ModuleCategory.MOVEMENT) {

    private val canceledPackets = ValueMode(this, "Canceled packets", true, "Open", "Close")

    private val keybinding = ArrayList(PlayerUtil.movementKeys)

    init {
        keybinding.add(mc.options.jumpKey)
    }

    @Priority(1) // this has to be overridden by freecam
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPacket -> {
                if (event.type == EventPacket.Type.SEND) {
                    if ((canceledPackets.isSelected(0) && event.packet is ClientCommandC2SPacket && event.packet.mode == ClientCommandC2SPacket.Mode.OPEN_INVENTORY) ||
                        (canceledPackets.isSelected(1) && event.packet is CloseHandledScreenC2SPacket && event.packet.syncId == 0))
                        event.cancelled = true
                }
            }

            is EventKeyBindingIsPressed -> {
                if (isPassingEvents())
                    if (keybinding.contains(event.keyBinding))
                        event.pressed = InputUtil.isKeyPressed(mc.window?.handle!!, (event.keyBinding as IKeyBinding).tarasande_getBoundKey().code)
            }
        }
    }

    private fun isTextboxFocused(): Boolean {
        return TarasandeMain.get().screenCheatMenu.managerValueComponent.instances.any {
            when (it) {
                is ValueComponentText -> it.isFocused()
                is ValueComponentRegistry -> it.isFocused()
                else -> false
            }
        }
    }

    private fun isPassingEvents(): Boolean {
        return (enabled && (mc.currentScreen is HandledScreen<*> || (mc.currentScreen is ScreenCheatMenu && !isTextboxFocused()))) || (mc.currentScreen == null || mc.currentScreen?.passEvents!!)
    }
}