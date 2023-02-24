package de.florianmichael.tarasande_protocol_spoofer.tarasandevalues

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.util.Identifier
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText

object VivecraftSpoofer {
    val enabled = ValueBoolean(this, "Enabled", false)

    private val version = ValueText(this, "Version", "Vivecraft 1.19.2  jrbudda-VR-2-b7")
    private val identifier = Identifier("vivecraft:data")

    fun sendVersionInfo() {
        val connection = mc.networkHandler?.connection!!

        connection.send(CustomPayloadC2SPacket(Identifier("minecraft:register"), PacketByteBuf(Unpooled.buffer()).writeString(identifier.toString())))
        connection.send(CustomPayloadC2SPacket(identifier, PacketByteBuf(Unpooled.buffer()).apply {
            writeByte(0)
            writeBytes(version.value.toByteArray())
        }))
    }
}
