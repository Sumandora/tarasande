package net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.model

interface SignatureUpdatable1_19_2 {
    fun update(updater: SignatureUpdater1_19_2?)
}

interface SignatureUpdater1_19_2 {
    fun update(data: ByteArray)
}
