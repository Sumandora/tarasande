package de.florianmichael.tarasande_protocol_hack.provider

import com.viaversion.viaversion.api.connection.UserConnection
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.provider.OldAuthProvider
import net.tarasandedevelopment.tarasande.mc

class FabricOldAuthProvider : OldAuthProvider() {

    override fun sendAuthRequest(user: UserConnection?, serverId: String?) {
        mc.sessionService.joinServer(
                mc.session.profile,
                mc.session.accessToken,
                serverId
        )
    }
}
