package net.tarasandedevelopment.tarasande_protocol_hack.provider.vialegacy

import de.florianmichael.vialegacy.pre_netty.PreNettyConstants
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.provider.EncryptionProvider
import io.netty.channel.Channel
import net.minecraft.network.encryption.PacketDecryptor
import net.minecraft.network.encryption.PacketEncryptor
import javax.crypto.Cipher

class FabricEncryptionProvider : EncryptionProvider() {

    companion object {
        var decryptionKey: Cipher? = null
        var encryptionKey: Cipher? = null
        var channel: Channel? = null
    }

    override fun encryptConnection() {
        channel?.pipeline()?.addBefore(PreNettyConstants.DECODER, "decrypt", PacketDecryptor(decryptionKey))
        channel?.pipeline()?.addBefore(PreNettyConstants.ENCODER, "encrypt", PacketEncryptor(encryptionKey))
    }
}
