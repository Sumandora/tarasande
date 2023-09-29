package su.mandora.tarasande.system.screen.informationsystem.impl

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.util.extension.javaruntime.clearAndGC
import su.mandora.tarasande.util.extension.minecraft.asByteArray

class InformationServerBrand : Information("Server", "Server Brand") {

    private val compiledRegex = Regex("\\(.*?\\) ")
    private val regex = ValueBoolean(this, "Regex", true)

    override fun getMessage(): String? {
        var brand = mc.networkHandler?.brand ?: return null
        if (regex.value)
            brand = brand.replace(compiledRegex, "")
        return brand
    }
}

class InformationOpenChannels : Information("Server", "Open Channels") {

    private val openChannels = ArrayList<String>()

    init {
        EventDispatcher.apply {
            add(EventPacket::class.java) {
                if (it.type == EventPacket.Type.RECEIVE && it.packet is CustomPayloadS2CPacket) {
                    val payload = it.packet.payload
                    val channel = payload.id()
                    val content = PacketByteBuf(Unpooled.buffer()).also { payload.write(it) }.asByteArray().decodeToString()
                    if (channel.toString() == "minecraft:register") {
                        content.split("\u0000").forEach { data ->
                            if (!openChannels.contains(data))
                                openChannels.add(data)
                        }
                    } else if (channel.toString() == "minecraft:unregister") {
                        content.split("\u0000").forEach { data ->
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
