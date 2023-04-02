package su.mandora.tarasande.util.extension.minecraft.packet

import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.network.packet.s2c.play.PositionFlag
import su.mandora.tarasande.mc
import su.mandora.tarasande.util.math.rotation.Rotation

fun PlayerPositionLookS2CPacket.evaluateNewRotation(): Rotation {
    var j = yaw
    var k = pitch
    if (flags.contains(PositionFlag.X_ROT)) {
        k += mc.player?.pitch!!
    }
    if (flags.contains(PositionFlag.Y_ROT)) {
        j += mc.player?.yaw!!
    }
    return Rotation(j, k)
}