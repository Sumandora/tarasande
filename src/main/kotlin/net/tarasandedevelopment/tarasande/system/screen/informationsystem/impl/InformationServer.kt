package net.tarasandedevelopment.tarasande.system.screen.informationsystem.impl

import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.clearAndGC
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import su.mandora.event.EventDispatcher
import java.nio.charset.StandardCharsets

class InformationServerBrand : Information("Server", "Server Brand") {

    private val compiledRegex = Regex("\\(.*?\\) ")
    private val regex = ValueBoolean(this, "Regex", true)

    override fun getMessage(): String? {
        if (!mc.isInSingleplayer) {
            var brand = mc.player?.serverBrand ?: return null

            if (regex.value)
                brand = brand.replace(compiledRegex, "")
            return brand
        }
        return null
    }
}

class InformationOpenChannels : Information("Server", "Open Channels") {

    private val openChannels = ArrayList<String>()

    init {
        EventDispatcher.apply {
            add(EventPacket::class.java) {
                if (it.type == EventPacket.Type.RECEIVE && it.packet is CustomPayloadS2CPacket) {
                    if (it.packet.channel.toString() == "minecraft:register") {
                        it.packet.data.toString(StandardCharsets.UTF_8).split("\u0000").forEach { data ->
                            if (!openChannels.contains(data))
                                openChannels.add(data)
                        }
                    } else if (it.packet.channel.toString() == "minecraft:unregister") {
                        it.packet.data.toString(StandardCharsets.UTF_8).split("\u0000").forEach { data ->
                            if (openChannels.contains(data))
                                openChannels.remove(data)
                        }
                    }
                }
            }
            add(EventDisconnect::class.java) {
                if (it.connection == mc.networkHandler?.connection) {
                    openChannels.clearAndGC()
                }
            }
        }
    }

    override fun getMessage(): String? {
        if (openChannels.isEmpty()) return null

        return "\n" + openChannels.joinToString("\n")
    }
}

class InformationLag : Information("Server", "Lag") {
    private val minimumDelay = ValueNumber(this, "Minimum delay", 100.0, 2500.0, 10000.0, 100.0)
    private val lastPacket = TimeUtil()

    init {
        EventDispatcher.add(EventPacket::class.java) {
            if (it.type == EventPacket.Type.RECEIVE && it.packet is WorldTimeUpdateS2CPacket) {
                lastPacket.reset()
            }
        }
    }

    override fun getMessage(): String? {
        if (lastPacket.hasReached(minimumDelay.value.toLong())) return (System.currentTimeMillis() - lastPacket.time).toString()
        return null
    }
}