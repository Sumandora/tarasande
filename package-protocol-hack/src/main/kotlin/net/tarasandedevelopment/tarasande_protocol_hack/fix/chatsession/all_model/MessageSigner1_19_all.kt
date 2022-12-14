package net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.all_model

import java.security.PrivateKey
import java.security.Signature

interface MessageSigner1_19_all {

    fun sign(signer: SignatureUpdatable1_19_all): ByteArray?

    companion object {

        fun create(privateKey: PrivateKey?, algorithm: String?): MessageSigner1_19_all {
            return object : MessageSigner1_19_all {
                override fun sign(signer: SignatureUpdatable1_19_all): ByteArray? {
                    try {
                        val signature = Signature.getInstance(algorithm)
                        signature.initSign(privateKey)
                        signer.update(object : SignatureUpdater1_19_all {
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
