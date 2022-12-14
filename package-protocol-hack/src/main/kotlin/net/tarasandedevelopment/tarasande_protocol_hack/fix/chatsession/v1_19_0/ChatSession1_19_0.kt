package net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.v1_19_0

import com.viaversion.viaversion.api.connection.StoredObject
import com.viaversion.viaversion.api.connection.UserConnection
import net.lenni0451.mcstructs.text.components.StringComponent
import net.lenni0451.mcstructs.text.serializer.TextComponentSerializer
import net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.model.MessageMetadata1_19_2
import net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.model.SignatureUpdatable1_19_2
import net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.model.SignatureUpdater1_19_2
import net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.v1_19_2.ChatSession1_19_2
import net.tarasandedevelopment.tarasande_protocol_hack.util.JsonSorter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*

class ChatSession1_19_0(user: UserConnection, val legacyKey: ByteArray) : StoredObject(user) {

    fun sign(sender: UUID, messageMetadata: MessageMetadata1_19_2): ByteArray? {
        return user.get(ChatSession1_19_2::class.java)?.signer?.sign(object : SignatureUpdatable1_19_2 {
            override fun update(updater: SignatureUpdater1_19_2?) {
                val data = ByteArray(32)

                val buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN)
                buffer.putLong(messageMetadata.salt)
                buffer.putLong(sender.mostSignificantBits).putLong(sender.leastSignificantBits)
                buffer.putLong(Instant.ofEpochMilli(messageMetadata.timestamp).epochSecond)

                updater?.update(data)
                updater?.update(JsonSorter.toSortedString(TextComponentSerializer.V1_18.serializeJson(StringComponent(messageMetadata.plain))).toByteArray(StandardCharsets.UTF_8))
            }
        })
    }
}
