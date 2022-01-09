package su.mandora.tarasande.module.player

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventTimeTravel
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.mixin.accessor.IRenderTickCounter
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleTimer : Module("Timer", "Changes the clientside ticks per second", ModuleCategory.PLAYER) {

	private val ticksPerSecond = ValueNumber(this, "Ticks per second", 1.0, 20.0, 100.0, 1.0)

	override fun onDisable() {
		((mc as IMinecraftClient).renderTickCounter as IRenderTickCounter).tickTime = (1000.0 / 20.0f).toFloat()
	}

	val eventConsumer = Consumer<Event> { event ->
		if (event is EventTimeTravel) {
			((mc as IMinecraftClient).renderTickCounter as IRenderTickCounter).tickTime = (1000.0 / ticksPerSecond.value).toFloat()
		}
	}

}