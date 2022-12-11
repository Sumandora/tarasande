package net.tarasandedevelopment.tarasande_protocol_hack.fix.signer.model

import java.security.SignatureException

interface SignatureUpdatable1_19_2 {
    @Throws(SignatureException::class)
    fun update(updater: SignatureUpdater1_19_2?)
}

interface SignatureUpdater1_19_2 {
    @Throws(SignatureException::class)
    fun update(data: ByteArray)
}
