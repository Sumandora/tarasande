package net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.v1_19_2

import net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.model.SignatureUpdatable1_19_2
import net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.model.SignatureUpdater1_19_2
import java.security.PrivateKey
import java.security.Signature

interface MessageSigner1_19_2 {

    fun sign(signer: SignatureUpdatable1_19_2): ByteArray?

    companion object {

        fun create(privateKey: PrivateKey?, algorithm: String?): MessageSigner1_19_2 {
            return object : MessageSigner1_19_2 {
                override fun sign(signer: SignatureUpdatable1_19_2): ByteArray? {
                    try {
                        val signature = Signature.getInstance(algorithm)
                        signature.initSign(privateKey)
                        signer.update(object : SignatureUpdater1_19_2 {
                            override fun update(data: ByteArray) {
                                signature.update(data)
                            }
                        })
                        return signature.sign()
                    } catch (exception: Exception) {
                        throw IllegalStateException("Failed to sign message", exception)
                    }
                }
            }
        }
    }
}
