package net.tarasandedevelopment.tarasande.protocolhack.provider.vialegacy

import de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.provider.OldAuthProvider
import net.minecraft.client.MinecraftClient

class OldAuthProviderFabricOldAuthProvider : OldAuthProvider() {

    override fun sendJoinServer(serverId: String?) {
        MinecraftClient.getInstance().sessionService.joinServer(
            MinecraftClient.getInstance().session.profile,
            MinecraftClient.getInstance().session.accessToken,
            serverId
        )
    }
}
