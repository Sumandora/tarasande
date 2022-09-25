package su.mandora.tarasande.module.ghost

import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventBoundingBoxOverride
import su.mandora.tarasande.event.EventPacket
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleHitBox : Module("Hit box", "Makes enemy hit boxes larger", ModuleCategory.GHOST) {

    private val expand = ValueNumber(this, "Expand", 0.0, 0.0, 1.0, 0.1)

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventBoundingBoxOverride) {
            event.boundingBox = event.boundingBox.expand(expand.value)
        } else if (event is EventPacket) {
            if (event.type == EventPacket.Type.SEND)
                if (event.packet !is PlayerMoveC2SPacket && event.packet !is KeepAliveC2SPacket)
                    when (event.packet) {
                        is PlayerInteractItemC2SPacket -> {
                            IllegalStateException().printStackTrace()
                            println(event.packet.javaClass.simpleName + " " + event.packet.hand + " " + event.packet.sequence)
                        }

                        is PlayerActionC2SPacket -> {
                            println(event.packet.javaClass.simpleName + " " + event.packet.action + " " + event.packet.pos + " " + event.packet.direction + " " + event.packet.sequence)
                        }
                    }
        }
    }

}