package de.florianmichael.tarasande_protocol_hack.provider.viaversion

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import com.viaversion.viaversion.protocols.base.BaseVersionProvider
import de.florianmichael.vialoadingbase.ViaLoadingBase
import net.minecraft.SharedConstants

class FabricVersionProvider : BaseVersionProvider() {

    override fun getClosestServerProtocol(connection: UserConnection?): Int {
        if (connection!!.isClientSide) {
            return ViaLoadingBase.getTargetVersion().originalVersion
        }
        return ProtocolVersion.getProtocol(SharedConstants.getGameVersion().protocolVersion).version
    }
}
