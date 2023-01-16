package net.tarasandedevelopment.tarasande.system.screen.informationsystem.impl

import com.google.common.collect.Iterables
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventInvalidPlayerInfo
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.combat.ModuleAntiBot
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.util.extension.minecraft.packet.isNewWorld
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import su.mandora.event.EventDispatcher
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.min

class InformationEntities : Information("World", "Entities") {
    override fun getMessage(): String? {
        if (mc.world == null)
            return null
        return Iterables.size(mc.world?.entities!!).toString()
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
                if (it.connection == mc.networkHandler?.connection)
                    lastUpdate = null
            }
        }
    }

    override fun getMessage(): String? {
        if (mc.world == null)
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
        val pos = mc.world?.spawnPos ?: return null

        return StringUtil.round(pos.x.toDouble(), this.decimalPlacesX.value.toInt()) + " " + StringUtil.round(pos.y.toDouble(), this.decimalPlacesY.value.toInt()) + " " + StringUtil.round(pos.z.toDouble(), this.decimalPlacesZ.value.toInt())
    }
}

class InformationVanishedPlayers : Information("World", "Vanished players") {

    private var vanishedPlayers = CopyOnWriteArrayList<UUID>()


    init {
        EventDispatcher.apply {
            add(EventInvalidPlayerInfo::class.java) { event ->
                if (!vanishedPlayers.contains(event.uuid))
                    vanishedPlayers.add(event.uuid)
            }
            add(EventPacket::class.java) { event ->
                if (event.type == EventPacket.Type.RECEIVE && event.packet is PlayerRespawnS2CPacket)
                    if (event.packet.isNewWorld())
                        vanishedPlayers = CopyOnWriteArrayList()
            }
            add(EventDisconnect::class.java) {
                if (it.connection == mc.networkHandler?.connection)
                    vanishedPlayers = CopyOnWriteArrayList()
            }
        }
    }

    override fun getMessage(): String? {
        if (vanishedPlayers.isEmpty()) return null

        return "\n" + vanishedPlayers.joinToString("\n")
    }
}


class InformationTextRadar : Information("World", "Text radar") {

    private val amount = ValueNumber(this, "Amount", 0.0, 5.0, 15.0, 1.0)
    private val decimalPlaces = ValueNumber(this, "Decimal places", 0.0, 1.0, 5.0, 1.0)

    override fun getMessage(): String? {
        var closestPlayers = mc.world?.players?.map { it to (mc.player?.distanceTo(it)?.toDouble() ?: 0.0) }?.sortedBy { it.second } ?: return null
        closestPlayers = closestPlayers.filter { it.first != mc.player }.filter { !ManagerModule.get(ModuleAntiBot::class.java).isBot(it.first) }
        closestPlayers = closestPlayers.subList(0, min(amount.value.toInt(), closestPlayers.size))
        if (closestPlayers.isEmpty()) return null
        return "\n" + closestPlayers.joinToString("\n") { Formatting.strip(it.first.gameProfile.name) + " (" + StringUtil.round(it.second, decimalPlaces.value.toInt()) + ")" }
    }
}