package net.tarasandedevelopment.tarasande_protocol_hack.fix

import com.viaversion.viaversion.api.connection.StoredObject
import com.viaversion.viaversion.api.connection.UserConnection
import net.minecraft.network.encryption.PlayerPublicKey.PublicKeyData
import net.minecraft.network.encryption.Signer
import java.security.PrivateKey

class ProfileKeyStorage(connection: UserConnection) : StoredObject(connection) {

    var publicKeyData: PublicKeyData? = null
    var originalNonce: ByteArray? = null
    var privateKey: PrivateKey? = null
    var signer: Signer? = null
}