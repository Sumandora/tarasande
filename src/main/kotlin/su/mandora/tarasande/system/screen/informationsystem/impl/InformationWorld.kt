package su.mandora.tarasande.system.screen.informationsystem.impl

import com.google.common.collect.Iterables
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.minecraft.util.Formatting
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventInvalidPlayerInfo
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.impl.combat.ModuleAntiBot
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleESP
import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.util.extension.minecraft.packet.isNewWorld
import su.mandora.tarasande.util.string.StringUtil
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
    private val decimalPlacesX = ValueNumber(this, "Decimal places x", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesY = ValueNumber(this, "Decimal places y", 0.0, 1.0, 5.0, 1.0)
    private val decimalPlacesZ = ValueNumber(this, "Decimal places z", 0.0, 1.0, 5.0, 1.0)

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
        val closestPlayers = (mc.world?.players ?: return null)
            .map { it to (mc.player?.distanceTo(it)?.toDouble() ?: 0.0) }
            .sortedBy { it.second }
            .filter { it.first != mc.player && ManagerModule.get(ModuleESP::class.java).shouldRender(it.first) }
            .let { it.subList(0, min(amount.value.toInt(), it.size)) }
        if (closestPlayers.isEmpty()) return null
        return "\n" + closestPlayers.joinToString("\n") { Formatting.strip(it.first.gameProfile.name) + " (" + StringUtil.round(it.second, decimalPlaces.value.toInt()) + ")" }
    }
}


class InformationSequence : Information("World", "Sequence") {

    override fun getMessage(): String? {
        return mc.world?.pendingUpdateManager?.sequence?.toString()
    }
}