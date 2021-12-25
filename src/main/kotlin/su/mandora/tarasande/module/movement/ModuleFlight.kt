package su.mandora.tarasande.module.movement

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventVanillaFlight
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleFlight : Module("Flight", "Allows flight in non-creative modes", ModuleCategory.MOVEMENT) {

    private val mode = ValueMode(this, "Mode", false, "Vanilla")
    private val flightSpeed = object : ValueNumber(this, "Flight speed", 0.0, 1.0, 5.0, 0.1) {
        override fun isVisible() = mode.isSelected(0)
    }

    val eventConsumer = Consumer<Event> { event ->
        if(event is EventVanillaFlight && mode.isSelected(0)) {
            event.flying = true
            event.flightSpeed *= flightSpeed.value.toFloat()
        }
    }

}