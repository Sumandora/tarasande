package net.tarasandedevelopment.tarasande.screen.menu.information

import de.florianmichael.viaprotocolhack.ViaProtocolHack
import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.base.screen.menu.information.Information

class InformationServerBrand : Information("Server", "Server Brand") {
    override fun getMessage(): String? {
        if (!MinecraftClient.getInstance().isInSingleplayer) {
            return MinecraftClient.getInstance().player!!.serverBrand
        }
        return null
    }
}

class InformationProtocolVersion : Information("Server", "Protocol Version") {

    override fun getMessage() = VersionList.getProtocols().find { it.version == ViaProtocolHack.instance().provider().realClientsideVersion() }?.name
}
