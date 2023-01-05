package net.tarasandedevelopment.tarasande_protocol_hack.provider.viabeta

import com.viaversion.viaversion.api.connection.UserConnection
import de.florianmichael.clampclient.injection.mixininterface.IClientConnection_Protocol
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.provider.EncryptionProvider
import net.minecraft.network.ClientConnection
import net.tarasandedevelopment.tarasande.event.EventConnectServer
import su.mandora.event.EventDispatcher

class FabricEncryptionProvider : EncryptionProvider() {

    private var clientConnection: ClientConnection? = null

    init {
        EventDispatcher.add(EventConnectServer::class.java) {
            clientConnection = it.connection
        }
    }

    override fun enableDecryption(user: UserConnection?) {
        if (clientConnection != null) {
            (clientConnection as IClientConnection_Protocol).vialegacy_setupPreNettyEncryption()
        }
    }
}