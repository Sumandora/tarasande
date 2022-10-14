package net.tarasandedevelopment.tarasande.module.qualityoflife

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.mixin.accessor.IPlayerMoveC2SPacket
import java.util.function.Consumer
import kotlin.math.roundToInt

class ModuleLiveOverflowMovement : Module("LiverOverflow movement", "Bot movement for 176.9.20.205", ModuleCategory.QUALITY_OF_LIFE) {

    val eventConsumer = Consumer<Event> {
        if (it is EventPacket) {
            if (it.type == EventPacket.Type.SEND && it.packet is PlayerMoveC2SPacket) {
                // Check = private fun isLegitMovement(input: Double) = ((input * 1000) % 10) == 0.0
                (it.packet as IPlayerMoveC2SPacket).tarasande_setX((MinecraftClient.getInstance().player!!.x * 100.0).roundToInt() / 100.0)
                (it.packet as IPlayerMoveC2SPacket).tarasande_setZ((MinecraftClient.getInstance().player!!.z * 100.0).roundToInt() / 100.0)
            }
        }
    }
}
