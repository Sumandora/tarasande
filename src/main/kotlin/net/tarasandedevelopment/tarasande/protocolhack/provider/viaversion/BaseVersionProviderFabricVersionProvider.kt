package net.tarasandedevelopment.tarasande.protocolhack.provider.viaversion

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import com.viaversion.viaversion.protocols.base.BaseVersionProvider
import de.florianmichael.viaprotocolhack.ViaProtocolHack
import net.minecraft.SharedConstants

class BaseVersionProviderFabricVersionProvider : BaseVersionProvider() {

    override fun getClosestServerProtocol(connection: UserConnection?): Int {
        if (connection!!.isClientSide) {
            return ViaProtocolHack.instance().provider().clientsideVersion
        }
        return ProtocolVersion.getProtocol(SharedConstants.getGameVersion().protocolVersion).version
    }
}
