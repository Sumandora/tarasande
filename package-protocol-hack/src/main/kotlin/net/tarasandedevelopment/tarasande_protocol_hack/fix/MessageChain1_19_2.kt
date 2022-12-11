package net.tarasandedevelopment.tarasande_protocol_hack.fix

import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature
import net.tarasandedevelopment.tarasande_protocol_hack.fix.signer.MessageSigner1_19_2
import net.tarasandedevelopment.tarasande_protocol_hack.fix.signer.model.MessageBody1_19_2
import net.tarasandedevelopment.tarasande_protocol_hack.fix.signer.model.MessageHeader1_19_2
import net.tarasandedevelopment.tarasande_protocol_hack.fix.signer.model.SignatureUpdatable1_19_2
import net.tarasandedevelopment.tarasande_protocol_hack.fix.signer.model.SignatureUpdater1_19_2
import java.time.Instant
import java.util.UUID

object MessageChain1_19_2 {

    private var precedingSignature: ByteArray? = null

    fun pack(plain: String, timestamp: Instant, salt: Long, lastSeenMessages: Array<PlayerMessageSignature>, sender: UUID, signer: MessageSigner1_19_2): ByteArray {
        val header = MessageHeader1_19_2(precedingSignature, sender)
        val body = MessageBody1_19_2(plain, timestamp, salt, lastSeenMessages)

        precedingSignature = signer.sign(object : SignatureUpdatable1_19_2 {
            override fun update(updater: SignatureUpdater1_19_2?) {
                header.update(updater!!, body.digestBytes())
            }
        })
        return precedingSignature!!
    }
}
