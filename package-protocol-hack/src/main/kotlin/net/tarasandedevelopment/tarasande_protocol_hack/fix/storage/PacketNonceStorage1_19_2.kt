package net.tarasandedevelopment.tarasande_protocol_hack.fix.storage

import com.viaversion.viaversion.api.connection.StoredObject
import com.viaversion.viaversion.api.connection.UserConnection

class PacketNonceStorage1_19_2(connection: UserConnection) : StoredObject(connection) {

    var nonce: ByteArray? = null

}
