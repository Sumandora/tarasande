package su.mandora.tarasande.module.movement

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.event.EventPacket
import su.mandora.tarasande.mixin.accessor.IKeyBinding
import su.mandora.tarasande.screen.menu.ScreenMenu
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueMode
import java.util.function.Consumer

class ModuleInventoryMove : Module("Inventory move", "Allows you to move while in inventory", ModuleCategory.MOVEMENT) {

    private val canceledPackets = ValueMode(this, "Canceled packets", true, "Open", "Close")

    private val keybinding = ArrayList(PlayerUtil.movementKeys)

    init {
        keybinding.add(mc.options.jumpKey)
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPacket -> {
                if (event.type == EventPacket.Type.SEND) {
                    if (
                        (canceledPackets.isSelected(0) && event.packet is ClientCommandC2SPacket && event.packet.mode == ClientCommandC2SPacket.Mode.OPEN_INVENTORY) ||
                        (canceledPackets.isSelected(1) && event.packet is CloseHandledScreenC2SPacket && event.packet.syncId == 0)
                    ) event.cancelled = true
                }
            }
            is EventKeyBindingIsPressed -> {
                if (isPassingEvents())
                    if (keybinding.contains(event.keyBinding))
                        event.pressed = InputUtil.isKeyPressed(mc.window?.handle!!, (event.keyBinding as IKeyBinding).boundKey.code)
            }
        }
    }

    fun isPassingEvents() = (enabled && (mc.currentScreen is HandledScreen<*> || mc.currentScreen is ScreenMenu)) || (mc.currentScreen == null || mc.currentScreen?.passEvents!!)
}