package net.tarasandedevelopment.tarasande.system.screen.informationsystem.impl

import com.google.common.collect.Iterables
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import su.mandora.event.EventDispatcher
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import java.util.concurrent.CopyOnWriteArrayList

class InformationEntities : Information("World", "Entities") {
    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().world == null)
            return null
        return Iterables.size(MinecraftClient.getInstance().world?.entities!!).toString()
    }
}

class InformationWorldTime : Information("World", "World Time") {

    var lastUpdate: Pair<Long, Long>? = null

    init {
        EventDispatcher.add(EventPacket::class.java, 1) {
            if (it.type == EventPacket.Type.RECEIVE && it.packet is WorldTimeUpdateS2CPacket) {
                lastUpdate = Pair(it.packet.timeOfDay, it.packet.time)
            }
        }
    }

    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().world == null)
            return null
        if (lastUpdate == null)
            return null
        return lastUpdate?.first.toString() + "/" + lastUpdate?.second
    }
}

class InformationSpawnPoint : Information("World", "Spawn Point") {
    private val decimalPlacesX = ValueNumber(this, "Decimal places: x", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesY = ValueNumber(this, "Decimal places: y", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesZ = ValueNumber(this, "Decimal places: z", 0.0, 1.0, 5.0, 1.0)

    override fun getMessage(): String? {
        val pos = MinecraftClient.getInstance().world?.spawnPos ?: return null

        return StringUtil.round(pos.x.toDouble(), this.decimalPlacesX.value.toInt()) + " " + StringUtil.round(pos.y.toDouble(), this.decimalPlacesY.value.toInt()) + " " + StringUtil.round(pos.z.toDouble(), this.decimalPlacesZ.value.toInt())
    }
}

class InformationVanishedPlayers : Information("World", "Vanished players") {

    private val vanishedPlayers = CopyOnWriteArrayList<String>()


    init {
        EventDispatcher.add(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE) {
                when (event.packet) {
                    is PlayerListS2CPacket -> {
                        if (event.packet.action != PlayerListS2CPacket.Action.ADD_PLAYER && event.packet.action != PlayerListS2CPacket.Action.REMOVE_PLAYER)
                            for (packetEntry in event.packet.entries)
                                if (MinecraftClient.getInstance().networkHandler?.getPlayerListEntry(packetEntry.profile.id) == null)
                                    if (!vanishedPlayers.contains(packetEntry.profile.id.toString()))
                                        vanishedPlayers.add(packetEntry.profile.id.toString())
                    }

                    is PlayerRespawnS2CPacket, is DisconnectS2CPacket -> {
                        vanishedPlayers.clear()
                    }
                }
            }
        }
    }

    override fun getMessage(): String? {
        if (vanishedPlayers.isEmpty()) return null

        return "\n" + vanishedPlayers.joinToString("\n")
    }
}