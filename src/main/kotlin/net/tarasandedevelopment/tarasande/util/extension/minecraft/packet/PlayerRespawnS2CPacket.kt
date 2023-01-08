package net.tarasandedevelopment.tarasande.util.extension.minecraft.packet

import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.tarasandedevelopment.tarasande.util.extension.mc

fun PlayerRespawnS2CPacket.isNewWorld() = dimension != mc.world?.registryKey