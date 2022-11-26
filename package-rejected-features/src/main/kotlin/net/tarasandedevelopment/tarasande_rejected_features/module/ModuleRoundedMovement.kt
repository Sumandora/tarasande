package net.tarasandedevelopment.tarasande_rejected_features.module

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import kotlin.math.roundToInt

//This was originally for the LiveOverflow event Server -> 176.9.20.205
class ModuleRoundedMovement : Module("Rounded movement", "Rounds the movement packets", ModuleCategory.MOVEMENT) {

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.SEND && event.packet is PlayerMoveC2SPacket) {
                (event.packet as PlayerMoveC2SPacket).x = (MinecraftClient.getInstance().player!!.x * 100.0).roundToInt() / 100.0
                (event.packet as PlayerMoveC2SPacket).z = (MinecraftClient.getInstance().player!!.z * 100.0).roundToInt() / 100.0
            }
        }
    }
}
