package su.mandora.tarasande.module.player

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventTimer
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleTimer : Module("Timer", "Changes the clientside ticks per second", ModuleCategory.PLAYER) {

	private val ticksPerSecond = ValueNumber(this, "Ticks per second", 1.0, 20.0, 100.0, 1.0)

	val eventConsumer = Consumer<Event> { event ->
		if (event is EventTimer) {
			event.lastFrameDuration *= (ticksPerSecond.value / 20.0).toFloat()
		}
	}

}