package de.florianmichael.tarasande_protocol_hack.provider

import com.viaversion.viaversion.api.connection.UserConnection
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.provider.ClassicWorldHeightProvider

class FabricClassicWorldHeightProvider : ClassicWorldHeightProvider() {

    override fun getMaxChunkSectionCount(user: UserConnection?) = 64.toShort()
}
