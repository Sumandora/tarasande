package net.tarasandedevelopment.tarasande_protocol_hack.fix

import net.minecraft.network.encryption.Signer
import net.minecraft.network.message.MessageSignatureData
import net.minecraft.text.Text
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*

object MessageSigner1_19_0 {

//    private val trackedMetadata = ArrayList<MessageMetadata>()
//
//    fun get(): MessageMetadata {
//        val latest = trackedMetadata.last()
//        trackedMetadata.remove(latest)
//
//        return latest
//    }
//
//    fun track(messageMetadata: MessageMetadata) {
//        trackedMetadata.add(messageMetadata)
//    }

    fun sign(signer: Signer, decorateText: Text, sender: UUID, timeStamp: Instant, salt: Long): MessageSignatureData {
        return MessageSignatureData(signer.sign { sign ->
            val data = ByteArray(32)

            val buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN)
            buffer.putLong(salt)
            buffer.putLong(sender.mostSignificantBits).putLong(sender.leastSignificantBits)
            buffer.putLong(timeStamp.epochSecond)

            sign.update(data)
            sign.update(Text.Serializer.toSortedJsonString(decorateText).toByteArray(StandardCharsets.UTF_8))
        })
    }
}