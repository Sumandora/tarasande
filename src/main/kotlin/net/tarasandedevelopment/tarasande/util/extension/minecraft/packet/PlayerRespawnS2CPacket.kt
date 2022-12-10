package net.tarasandedevelopment.tarasande.util.extension.minecraft.packet

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket

fun PlayerRespawnS2CPacket.isNewWorld() = dimension != MinecraftClient.getInstance().world?.registryKey