package su.mandora.tarasande.util.extension.minecraft.packet

import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.network.packet.s2c.play.PositionFlag
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.mc
import su.mandora.tarasande.feature.rotation.api.Rotation

fun PlayerPositionLookS2CPacket.evaluateNewRotation(): Rotation {
    var j = yaw
    var k = pitch
    (Rotations.fakeRotation ?: mc.player?.let { Rotation(it) })?.also {
        if (flags.contains(PositionFlag.X_ROT)) {
            k += it.pitch
        }
        if (flags.contains(PositionFlag.Y_ROT)) {
            j += it.yaw
        }
    }
    return Rotation(j, k)
}