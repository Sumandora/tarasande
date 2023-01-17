package de.florianmichael.tarasande_protocol_hack.fix.chatsession.v1_19_2.model

import com.google.common.hash.Hashing
import com.google.common.hash.HashingOutputStream
import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature
import de.florianmichael.tarasande_protocol_hack.fix.chatsession.all_model.MessageMetadata1_19_all
import java.io.DataOutput
import java.io.DataOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.time.Instant

class MessageBody1_19_2(private val messageMetadata: MessageMetadata1_19_all, private val lastSeenMessages: Array<PlayerMessageSignature>) {

    private fun writeLastSeenMessages(output: DataOutput) {
        lastSeenMessages.forEach {
            output.apply {
                writeByte(70)
                it.uuid().apply {
                    writeLong(mostSignificantBits)
                    writeLong(leastSignificantBits)
                }
                write(it.signatureBytes())
            }
        }
    }

    fun digestBytes(): ByteArray {
        HashingOutputStream(Hashing.sha256(), OutputStream.nullOutputStream()).apply {
            DataOutputStream(this).apply {
                writeLong(messageMetadata.salt)
                writeLong(Instant.ofEpochMilli(messageMetadata.timestamp).epochSecond)
                OutputStreamWriter(this as OutputStream, StandardCharsets.UTF_8).apply {
                    write(messageMetadata.plain)
                    flush()
                }
                write(70)

                writeLastSeenMessages(this)
            }
            return hash().asBytes()
        }
    }
}
