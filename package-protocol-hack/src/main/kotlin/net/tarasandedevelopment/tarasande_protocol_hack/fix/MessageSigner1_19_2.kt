package net.tarasandedevelopment.tarasande_protocol_hack.fix

import net.minecraft.network.encryption.SignatureUpdatable
import net.minecraft.network.encryption.Signer
import java.security.PrivateKey
import java.security.Signature

interface MessageSigner1_19_2 {

    fun sign(var1: SignatureUpdatable): ByteArray?
    fun sign(data: ByteArray?): ByteArray? {
        return this.sign { updater -> updater.update(data) }
    }

    companion object {
        @JvmStatic
        fun create(privateKey: PrivateKey?, algorithm: String?): Signer? {
            return Signer { updatable: SignatureUpdatable ->
                try {
                    val signature = Signature.getInstance(algorithm)
                    signature.initSign(privateKey)
                    updatable.update(signature::update)
                    return@Signer signature.sign()
                } catch (exception: Exception) {
                    throw IllegalStateException("Failed to sign message", exception)
                }
            }
        }
    }
}