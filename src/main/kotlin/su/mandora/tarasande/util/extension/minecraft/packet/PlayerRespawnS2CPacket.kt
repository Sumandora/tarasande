package su.mandora.tarasande.util.extension.minecraft.packet

import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import su.mandora.tarasande.mc

fun PlayerRespawnS2CPacket.isNewWorld() = dimension != mc.world?.registryKey