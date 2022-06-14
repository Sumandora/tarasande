package su.mandora.tarasande.module.render

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.world.World
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventPacket
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.mixin.accessor.IWorldTimeUpdateS2CPacket
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleWorldTime : Module("World time", "Changes the time of day", ModuleCategory.RENDER) {

    // I am unsure if this is supposed to be the max time or is just a coincidence to be the same value
    private val time = ValueNumber(this, "Time", 0.0, World.field_30969 / 2.0, World.field_30969.toDouble(), 1.0)

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