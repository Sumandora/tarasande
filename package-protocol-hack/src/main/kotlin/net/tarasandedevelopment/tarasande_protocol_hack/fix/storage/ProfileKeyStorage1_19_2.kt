package net.tarasandedevelopment.tarasande_protocol_hack.fix.storage

import com.viaversion.viaversion.api.connection.StoredObject
import com.viaversion.viaversion.api.connection.UserConnection
import net.minecraft.network.encryption.PlayerPublicKey.PublicKeyData
import net.minecraft.network.encryption.Signer
import net.tarasandedevelopment.tarasande_protocol_hack.fix.signer.MessageSigner1_19_2
import java.security.PrivateKey

class ProfileKeyStorage1_19_2(connection: UserConnection) : StoredObject(connection) {

    var publicKeyData: PublicKeyData? = null
    var privateKey: PrivateKey? = null
    var signer: MessageSigner1_19_2? = null

    fun setupConnection(publicKeyData: PublicKeyData, privateKey: PrivateKey) {
        this.publicKeyData = publicKeyData
        this.privateKey = privateKey

        this.signer = MessageSigner1_19_2.create(privateKey, "SHA256withRSA")
    }
}
