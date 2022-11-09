package net.tarasandedevelopment.tarasande.features.module.chat

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket
import net.minecraft.network.packet.s2c.play.MessageHeaderS2CPacket
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventPacket
import java.util.*
import kotlin.collections.ArrayList

class ModulePrivateMsgDetector : Module("Private msg detector", "Detects private conversations using signatures", ModuleCategory.CHAT) {

    private val signatures: Multimap<UUID, ByteArray> = MultimapBuilder.hashKeys().hashSetValues().build()
    val conversation = ArrayList<String>()

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type != EventPacket.Type.RECEIVE) return@registerEvent

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
        registerEvent(EventDisconnect::class.java) {
            conversation.clear()
        }
    }
}
