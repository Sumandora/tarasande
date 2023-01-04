package net.tarasandedevelopment.tarasande_protocol_hack.provider.vialegacy

import com.viaversion.viaversion.api.connection.UserConnection
import net.minecraft.client.MinecraftClient
import net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.providers.OldAuthProvider

class FabricOldAuthProvider : OldAuthProvider() {

    override fun sendAuthRequest(user: UserConnection?, serverId: String?) {
        MinecraftClient.getInstance().sessionService.joinServer(
                MinecraftClient.getInstance().session.profile,
                MinecraftClient.getInstance().session.accessToken,
                serverId
        )
    }
}
