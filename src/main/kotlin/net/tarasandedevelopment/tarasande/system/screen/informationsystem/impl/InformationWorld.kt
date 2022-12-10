package net.tarasandedevelopment.tarasande.system.screen.informationsystem.impl

import com.google.common.collect.Iterables
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventInvalidPlayerInfo
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.util.extension.minecraft.packet.isNewWorld
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import su.mandora.event.EventDispatcher
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class InformationEntities : Information("World", "Entities") {
    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().world == null)
            return null
        return Iterables.size(MinecraftClient.getInstance().world?.entities!!).toString()
    }
}

class InformationWorldTime : Information("World", "World Time") {

    private var lastUpdate: Pair<Long, Long>? = null

    init {
        EventDispatcher.apply {
            add(EventPacket::class.java, 1) {
                if (it.type == EventPacket.Type.RECEIVE && it.packet is WorldTimeUpdateS2CPacket) {
                    lastUpdate = Pair(it.packet.timeOfDay, it.packet.time)
                }
            }
            add(EventDisconnect::class.java) {
                if(it.connection == MinecraftClient.getInstance().networkHandler?.connection)
                    lastUpdate = null
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

    private val vanishedPlayers = CopyOnWriteArrayList<UUID>()


    init {
        EventDispatcher.apply {
            add(EventInvalidPlayerInfo::class.java) { event ->
                if (!vanishedPlayers.contains(event.uuid))
                    vanishedPlayers.add(event.uuid)
            }
            add(EventPacket::class.java) { event ->
                if(event.type == EventPacket.Type.RECEIVE && event.packet is PlayerRespawnS2CPacket)
                    if (event.packet.isNewWorld())
                        vanishedPlayers.clear()
            }
            add(EventDisconnect::class.java) {
                if(it.connection == MinecraftClient.getInstance().networkHandler?.connection)
                    vanishedPlayers.clear()
            }
        }
    }

    override fun getMessage(): String? {
        if (vanishedPlayers.isEmpty()) return null

        return "\n" + vanishedPlayers.joinToString("\n")
    }
}