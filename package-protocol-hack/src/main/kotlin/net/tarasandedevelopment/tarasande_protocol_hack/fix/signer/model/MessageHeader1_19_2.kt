package net.tarasandedevelopment.tarasande_protocol_hack.fix.signer.model

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.SignatureException
import java.util.*

class MessageHeader1_19_2(private val precedingSignature: ByteArray?, private val sender: UUID) {

    private fun toByteArray(uuid: UUID): ByteArray {
        val bs = ByteArray(16)
        ByteBuffer.wrap(bs).order(ByteOrder.BIG_ENDIAN).putLong(uuid.mostSignificantBits).putLong(uuid.leastSignificantBits)
        return bs
    }

    @Throws(SignatureException::class)
    fun update(updater: SignatureUpdater1_19_2, bodyDigest: ByteArray) {
        if (precedingSignature != null) {
            updater.update(precedingSignature)
        }
        updater.update(toByteArray(sender))
        updater.update(bodyDigest)
    }
}
