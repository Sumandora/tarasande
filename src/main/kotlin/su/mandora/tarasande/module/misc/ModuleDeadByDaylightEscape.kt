package su.mandora.tarasande.module.misc

import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventUpdate
import java.util.function.Consumer

class ModuleDeadByDaylightEscape : Module("Dead by daylight escape", "Insta escapes in Gomme's dead by daylight", ModuleCategory.MISC) {

	val eventConsumer = Consumer<Event> { event ->
		if(event is EventUpdate && event.state == EventUpdate.State.POST) {
			TarasandeMain.get().log.println("Trying to escape")
			for(i in 0..150)
				mc.networkHandler?.sendPacket(PlayerInputC2SPacket(if(i % 2 == 0) 1.0f else -1.0f, 0.0f, false, false))
			this.enabled = false
		}
	}

}