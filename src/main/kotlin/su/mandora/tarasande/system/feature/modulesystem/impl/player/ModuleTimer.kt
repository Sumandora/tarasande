package su.mandora.tarasande.system.feature.modulesystem.impl.player

import su.mandora.tarasande.event.impl.EventTickRate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max

class ModuleTimer : Module("Timer", "Changes the clientside tick-rate", ModuleCategory.PLAYER) {

    private val mode = ValueMode(this, "Mode", false, "Constant", "Random", "Ground")
    private val ticksPerSecond = ValueNumber(this, "Ticks per second", 1.0, 20.0, 100.0, 1.0, isEnabled = { mode.isSelected(0) || mode.isSelected(1) })
    private val variation = ValueNumber(this, "Variation", 1.0, 20.0, 100.0, 1.0, isEnabled = { mode.isSelected(1) })
    private val onGroundTicksPerSecond = ValueNumber(this, "On ground ticks per second", 1.0, 20.0, 100.0, 1.0, isEnabled = { mode.isSelected(2) })
    private val offGroundTicksPerSecond = ValueNumber(this, "Off ground ticks per second", 1.0, 20.0, 100.0, 1.0, isEnabled = { mode.isSelected(2) })


    init {
        registerEvent(EventTickRate::class.java, 1) { event ->
            event.tickRate = when {
                mode.isSelected(0) -> ticksPerSecond.value.toFloat()
                mode.isSelected(1) -> max(ticksPerSecond.value + ThreadLocalRandom.current().nextInt(-variation.value.toInt() / 2, variation.value.toInt() / 2), 1.0).toFloat()
                mode.isSelected(2) -> (if (mc.player?.isOnGround == true) onGroundTicksPerSecond.value else offGroundTicksPerSecond.value).toFloat()
                else -> event.tickRate // brain
            }
        }
    }
}