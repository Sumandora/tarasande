package su.mandora.tarasande.module.player

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventTimeTravel
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.mixin.accessor.IRenderTickCounter
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

class ModuleTimer : Module("Timer", "Changes the clientside ticks per second", ModuleCategory.PLAYER) {

    private val mode = ValueMode(this, "Mode", false, "Constant", "Random", "Ground")
    private val ticksPerSecond = object : ValueNumber(this, "Ticks per second", 1.0, 20.0, 100.0, 1.0) {
        override fun isEnabled() = mode.isSelected(0) || mode.isSelected(1)
    }
    private val variation = object : ValueNumber(this, "Variation", 1.0, 20.0, 100.0, 1.0) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val onGroundTicksPerSecond = object : ValueNumber(this, "On ground ticks per second", 1.0, 20.0, 100.0, 1.0) {
        override fun isEnabled() = mode.isSelected(2)
    }
    private val offGroundTicksPerSecond = object : ValueNumber(this, "Off ground ticks per second", 1.0, 20.0, 100.0, 1.0) {
        override fun isEnabled() = mode.isSelected(2)
    }

    override fun onDisable() {
        ((mc as IMinecraftClient).tarasande_getRenderTickCounter() as IRenderTickCounter).tarasande_setTickTime((1000.0 / 20.0f).toFloat())
    }

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventTimeTravel) {
            when {
                mode.isSelected(0) -> ((mc as IMinecraftClient).tarasande_getRenderTickCounter() as IRenderTickCounter).tarasande_setTickTime((1000.0 / ticksPerSecond.value).toFloat())
                mode.isSelected(1) -> ((mc as IMinecraftClient).tarasande_getRenderTickCounter() as IRenderTickCounter).tarasande_setTickTime((1000.0 / (ticksPerSecond.value + (ThreadLocalRandom.current().nextInt(variation.value.toInt()) - variation.value / 2.0))).toFloat())
                mode.isSelected(2) -> ((mc as IMinecraftClient).tarasande_getRenderTickCounter() as IRenderTickCounter).tarasande_setTickTime((1000.0 / (if (mc.player?.isOnGround!!) onGroundTicksPerSecond.value else offGroundTicksPerSecond.value)).toFloat())
            }
        }
    }

}