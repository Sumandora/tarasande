package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.world.World
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.events.impl.EventPacket
import net.tarasandedevelopment.events.impl.EventUpdate
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory

class ModuleWorldTime : Module("World time", "Changes the time of day", ModuleCategory.RENDER) {

    private val moonStates = arrayOf(
        "Full moon",
        "Warning gibbous",
        "Last quarter",
        "Waning crescent",
        "New moon",
        "Waxing crescent",
        "First quarter",
        "Waxing gibbous"
    )

    private val modifyTime = ValueBoolean(this, "Modify time", true)
    // I am unsure if this is supposed to be the max time or is just a coincidence to be the same value
    private val time = object : ValueNumber(this, "Time", 0.0, World.field_30969 / 2.0, World.field_30969.toDouble(), 1.0) {
        override fun isEnabled() = modifyTime.value
    }

    private val forceMoonPhase = ValueMode(this, "Force moon phase", false, "Off", *moonStates)

    fun moonPhase(): Int {
        if (!enabled || forceMoonPhase.isSelected(0)) return -1

        return moonStates.indexOf(forceMoonPhase.selected[0])
    }

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                mc.world?.timeOfDay = time.value.toLong()
            }
        }

        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE && event.packet is WorldTimeUpdateS2CPacket)
                event.packet.timeOfDay = time.value.toLong()
        }
    }
}
