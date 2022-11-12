package net.tarasandedevelopment.tarasande.systems.screen.informationsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import net.tarasandedevelopment.event.EventDispatcher
import net.tarasandedevelopment.tarasande.events.EventDisconnect
import net.tarasandedevelopment.tarasande.events.EventPacket
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.Information
import java.nio.charset.StandardCharsets

class InformationServerBrand : Information("Server", "Server Brand") {

    private val compiledRegex = Regex("\\(.*?\\) ")
    private val regex = ValueBoolean(this, "Regex", true)

    override fun getMessage(): String? {
        if (!MinecraftClient.getInstance().isInSingleplayer) {
            var brand = MinecraftClient.getInstance().player?.serverBrand ?: return null

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
                openChannels.clear()
            }
        }
    }

    override fun getMessage(): String? {
        if (openChannels.isEmpty()) return null

        return "\n" + openChannels.joinToString("\n")
    }
}
