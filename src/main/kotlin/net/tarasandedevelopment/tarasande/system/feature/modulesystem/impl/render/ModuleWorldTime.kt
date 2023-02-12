package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.world.World
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

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
    private val time = ValueNumber(this, "Time", 0.0, World.field_30969 / 2.0, World.field_30969.toDouble(), 1.0, isEnabled = { modifyTime.value })

    private val forceMoonPhase = ValueMode(this, "Force moon phase", false, "Off", *moonStates)

    fun moonPhase(): Int? {
        if (!enabled.value || forceMoonPhase.isSelected(0)) return null

        return moonStates.indexOf(forceMoonPhase.getSelected())
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
