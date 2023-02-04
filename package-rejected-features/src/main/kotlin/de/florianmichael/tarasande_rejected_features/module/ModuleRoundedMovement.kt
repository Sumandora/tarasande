package de.florianmichael.tarasande_rejected_features.module

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import kotlin.math.pow
import kotlin.math.roundToInt

//This was originally for the LiveOverflow event Server -> 176.9.20.205
class ModuleRoundedMovement : Module("Rounded movement", "Rounds the movement packets", ModuleCategory.MOVEMENT) {

    private val digits = ValueNumber(this, "Digits", 0.0, 2.0, 10.0, 1.0)

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.SEND && event.packet is PlayerMoveC2SPacket) {
                val scale = 10.0.pow(digits.value)
                val playerMoveC2SPacket = event.packet as PlayerMoveC2SPacket
                playerMoveC2SPacket.x = (playerMoveC2SPacket.x * scale).roundToInt() / scale
                playerMoveC2SPacket.z = (playerMoveC2SPacket.z * scale).roundToInt() / scale
            }
        }
    }
}
