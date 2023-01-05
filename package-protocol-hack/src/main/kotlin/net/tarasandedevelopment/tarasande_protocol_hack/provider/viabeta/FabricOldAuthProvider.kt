package net.tarasandedevelopment.tarasande_protocol_hack.provider.viabeta

import com.viaversion.viaversion.api.connection.UserConnection
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.provider.OldAuthProvider
import net.minecraft.client.MinecraftClient

class FabricOldAuthProvider : OldAuthProvider() {

    override fun sendAuthRequest(user: UserConnection?, serverId: String?) {
        MinecraftClient.getInstance().sessionService.joinServer(
                MinecraftClient.getInstance().session.profile,
                MinecraftClient.getInstance().session.accessToken,
                serverId
        )
    }
}
