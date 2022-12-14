package net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.v1_19_2.model

import net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.model.SignatureUpdater1_19_2
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class MessageHeader1_19_2(private val precedingSignature: ByteArray?, private val sender: UUID) {

    private fun toByteArray(uuid: UUID): ByteArray {
        ByteArray(16).apply {
            ByteBuffer.
            wrap(this).
            order(ByteOrder.BIG_ENDIAN).

            putLong(uuid.mostSignificantBits).
            putLong(uuid.leastSignificantBits)
            return this
        }
    }

    fun update(updater: SignatureUpdater1_19_2, bodyDigest: ByteArray) {
        if (precedingSignature != null) {
            updater.update(precedingSignature)
        }
        updater.update(toByteArray(sender))
        updater.update(bodyDigest)
    }
}
