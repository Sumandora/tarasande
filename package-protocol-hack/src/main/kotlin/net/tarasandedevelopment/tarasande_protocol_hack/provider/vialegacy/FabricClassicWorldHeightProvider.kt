package net.tarasandedevelopment.tarasande_protocol_hack.provider.vialegacy

import com.viaversion.viaversion.api.connection.UserConnection
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicWorldHeightProvider

class FabricClassicWorldHeightProvider : ClassicWorldHeightProvider() {

    override fun getMaxChunkSectionCount(user: UserConnection?) = 64.toShort()
}
