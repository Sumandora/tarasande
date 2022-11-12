package net.tarasandedevelopment.tarasande.informationsystem.impl

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket
import net.minecraft.network.packet.s2c.play.MessageHeaderS2CPacket
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.informationsystem.Information
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventPacket
import java.util.*


class InformationDetectedMessages : Information("Private msg detector", "Detected messages") {

    private val signatures: Multimap<UUID, ByteArray> = MultimapBuilder.hashKeys().hashSetValues().build()
    private val conversation = ArrayList<String>()

    init {
        TarasandeMain.get().managerEvent.add(EventPacket::class.java) { event ->
            if (event.type != EventPacket.Type.RECEIVE) return@add

            if (event.packet is MessageHeaderS2CPacket) {
                val sender = MinecraftClient.getInstance().world!!.getPlayerByUuid(event.packet.header.sender)
                val senderName = sender?.name?.string
                val signature = event.packet.headerSignature.data

                if (sender != null) {
                    this.signatures.put(sender.uuid, signature)
                    conversation.add("$senderName just sent a private message!")
                }
            }

            if (event.packet is ChatMessageS2CPacket) {
                for (entry in event.packet.message.signedBody.lastSeenMessages.entries) {
                    if (entry.profileId == event.packet.message.signedHeader.sender)
                        continue

                    val signaturesLocal = this.signatures.get(event.packet.message.signedHeader.sender)

                    for (bytes in signaturesLocal) {
                        if (Arrays.equals(bytes, entry.lastSignature.data)) {
                            val receiver = MinecraftClient.getInstance().world!!.getPlayerByUuid(event.packet.message.signedHeader.sender)
                            val sender = MinecraftClient.getInstance().world!!.getPlayerByUuid(entry.profileId)

                            if (sender != receiver) {
                                conversation.add(receiver!!.name.string + " received a private message from " + sender!!.name.string + "!")
                            }
                        }
                    }
                }
            }
        }
        TarasandeMain.get().managerEvent.add(EventDisconnect::class.java) {
            conversation.clear()
        }
    }

    override fun getMessage(): String? {
        if (conversation.isNotEmpty())
            return "\n" + conversation.subList(0, conversation.size - 1).joinToString("\n")

        return null
    }
}
