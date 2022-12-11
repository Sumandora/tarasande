package net.tarasandedevelopment.tarasande_protocol_hack.fix.signer.model

import com.google.common.hash.Hashing
import com.google.common.hash.HashingOutputStream
import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature
import java.io.DataOutput
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.time.Instant

class MessageBody1_19_2(private val plain: String, private val timestamp: Instant, private val salt: Long, private val lastSeenMessages: Array<PlayerMessageSignature>) {

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
        val hashingOutputStream = HashingOutputStream(Hashing.sha256(), OutputStream.nullOutputStream())
        try {
            DataOutputStream(hashingOutputStream).apply {
                writeLong(salt)
                writeLong(timestamp.epochSecond)
                OutputStreamWriter(this as OutputStream, StandardCharsets.UTF_8).apply {
                    write(plain)
                    flush()
                }
                write(70)

                writeLastSeenMessages(this)
            }
        } catch (iOException: IOException) {
            // empty catch block
        }
        return hashingOutputStream.hash().asBytes()
    }
}
