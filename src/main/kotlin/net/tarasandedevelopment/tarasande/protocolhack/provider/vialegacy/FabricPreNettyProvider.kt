package net.tarasandedevelopment.tarasande.protocolhack.provider.vialegacy

import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.provider.PreNettyProvider
import net.minecraft.network.encryption.PacketDecryptor
import net.minecraft.network.encryption.PacketEncryptor
import javax.crypto.Cipher

class FabricPreNettyProvider : PreNettyProvider() {

    companion object {
        var decryptionKey: Cipher? = null
        var encryptionKey: Cipher? = null
    }

    override fun decryptor() = PacketDecryptor(decryptionKey)
    override fun encryptor() = PacketEncryptor(encryptionKey)
}
