package net.tarasandedevelopment.tarasande_protocol_hack.provider.vialegacy

import de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.provider.OldAuthProvider
import net.minecraft.client.MinecraftClient

class FabricOldAuthProvider : OldAuthProvider() {

    override fun sendJoinServer(serverId: String?) {
        MinecraftClient.getInstance().sessionService.joinServer(
            MinecraftClient.getInstance().session.profile,
            MinecraftClient.getInstance().session.accessToken,
            serverId
        )
    }
}
