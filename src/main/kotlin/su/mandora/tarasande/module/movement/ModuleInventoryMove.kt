package su.mandora.tarasande.module.movement

import net.minecraft.client.Keyboard
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.BookScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventPacket
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.mixin.accessor.IKeyBinding
import su.mandora.tarasande.mixin.accessor.IScreen
import su.mandora.tarasande.screen.menu.ScreenMenu
import su.mandora.tarasande.value.ValueMode
import java.util.function.Consumer

class ModuleInventoryMove : Module("Inventory Move", "Allows you to move while in inventory", ModuleCategory.MOVEMENT) {

	private val canceledPackets = ValueMode(this, "Canceled packets", true, "Open", "Close")

	private val keybinding = listOf(
		mc.options.keyForward,
		mc.options.keyLeft,
		mc.options.keyBack,
		mc.options.keyRight,
		mc.options.keyJump
	)

	val eventConsumer = Consumer<Event> { event ->
		if(event is EventUpdate && event.state == EventUpdate.State.PRE) {
			if(isPassingEvents())
				keybinding.forEach { it.isPressed = InputUtil.isKeyPressed(mc.window?.handle!!, (it as IKeyBinding).boundKey.code) }
		}
		if(event is EventPacket) {
			if(event.type == EventPacket.Type.SEND) {
				when {
					canceledPackets.isSelected(0) && event.packet is ClientCommandC2SPacket && event.packet.mode == ClientCommandC2SPacket.Mode.OPEN_INVENTORY -> event.setCancelled()
					canceledPackets.isSelected(1) && event.packet is CloseHandledScreenC2SPacket && event.packet.syncId == 0 /* PlayerScreenHandler hardcoded in parent constructor call */ -> event.setCancelled()
				}
			}
		}
	}

	fun isPassingEvents() = enabled && (mc.currentScreen is HandledScreen<*> || mc.currentScreen is ScreenMenu) || mc.currentScreen == null || mc.currentScreen?.passEvents!!
}