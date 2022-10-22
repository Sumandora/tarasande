package net.tarasandedevelopment.tarasande.module.qualityoflife

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventPacket
import kotlin.math.roundToInt

class ModuleLiveOverflowMovement : Module("LiveOverflow movement", "Bot movement for 176.9.20.205", ModuleCategory.QUALITY_OF_LIFE) {

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.SEND && event.packet is PlayerMoveC2SPacket) {
                // Check = private fun isLegitMovement(input: Double) = ((input * 1000) % 10) == 0.0
                event.packet.x = (MinecraftClient.getInstance().player!!.x * 100.0).roundToInt() / 100.0
                event.packet.z = (MinecraftClient.getInstance().player!!.z * 100.0).roundToInt() / 100.0
            }
        }
    }
}
