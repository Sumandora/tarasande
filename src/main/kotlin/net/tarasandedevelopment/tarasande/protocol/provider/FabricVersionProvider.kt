package net.tarasandedevelopment.tarasande.protocol.provider

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import com.viaversion.viaversion.protocols.base.BaseVersionProvider
import de.florianmichael.viaprotocolhack.ViaProtocolHack
import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.SharedConstants
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.protocol.util.ProtocolAutoDetector
import java.net.InetSocketAddress

class FabricVersionProvider : BaseVersionProvider() {

    override fun getClosestServerProtocol(connection: UserConnection?): Int {
        if (connection!!.isClientSide) {
            val info = connection.protocolInfo
            var currentVersion = TarasandeMain.get().protocolHack.clientsideVersion()
            val address = connection.channel?.remoteAddress()

            if (address is InetSocketAddress) {
                try {
                    if (TarasandeMain.get().protocolHack.isAuto()) {
                        val autoVersion = ProtocolAutoDetector.detectVersion(address).get()

                        if (autoVersion != null) {
                            TarasandeMain.get().protocolHack.realClientsideVersion = autoVersion.version

                            return autoVersion.version
                        }
                    }
                } catch (e: Exception) {
                    ViaProtocolHack.instance().logger().warning("Could not auto detect: $e")
                }
            }

            val supported = VersionList.isSupported(currentVersion, info.protocolVersion)
            if (!supported)
                currentVersion = info.protocolVersion

            TarasandeMain.get().protocolHack.realClientsideVersion = currentVersion
            return currentVersion
        }
        return ProtocolVersion.getProtocol(SharedConstants.getGameVersion().protocolVersion).version
    }
}
