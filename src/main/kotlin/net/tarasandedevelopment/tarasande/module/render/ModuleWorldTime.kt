package net.tarasandedevelopment.tarasande.module.render

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.world.World
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mixin.accessor.IWorldTimeUpdateS2CPacket
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleWorldTime : Module("World time", "Changes the time of day", ModuleCategory.RENDER) {

    private val modifyTime = ValueBoolean(this, "Modify time", true)
    // I am unsure if this is supposed to be the max time or is just a coincidence to be the same value
    private val time = object : ValueNumber(this, "Time", 0.0, World.field_30969 / 2.0, World.field_30969.toDouble(), 1.0) {
        override fun isEnabled() = modifyTime.value
    }

    private val modifyMoonPhase = ValueBoolean(this, "Modify moon phase", false)
    private val moonPhase = object : ValueNumber(this, "Moon phase", 0.0, 0.0, 7.0, 1.0) {
        override fun isEnabled() = modifyMoonPhase.value
    }

    fun moonPhase(): Int {
        if (!enabled || !modifyMoonPhase.value) return -1

        return moonPhase.value.toInt()
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE) {
                    mc.world?.timeOfDay = time.value.toLong()
                }
            }

            is EventPacket -> {
                if (event.type == EventPacket.Type.RECEIVE && event.packet is WorldTimeUpdateS2CPacket) {
                    (event.packet as IWorldTimeUpdateS2CPacket).tarasande_setTimeOfDay(time.value.toLong())
                }
            }
        }
    }
}
