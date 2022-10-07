package net.tarasandedevelopment.tarasande.screen.cheatmenu.information

import de.florianmichael.viaprotocolhack.ViaProtocolHack
import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information.Information
import net.tarasandedevelopment.tarasande.event.EventConnectServer
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientConnection
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import java.util.function.Consumer

class InformationHandlers : Information("Connection", "Handlers") {
    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().networkHandler == null || MinecraftClient.getInstance().networkHandler?.connection == null) return null
        val names = ((MinecraftClient.getInstance().networkHandler?.connection as IClientConnection).tarasande_getChannel() ?: return null).pipeline().names()
        if (names.isEmpty()) return null
        return "\n" + names.subList(0, names.size - 1).joinToString("\n")
    }
}

class InformationProtocolVersion : Information("Connection", "Protocol Version") {

    override fun getMessage() = VersionList.getProtocols().find { it.version == ViaProtocolHack.instance().provider().realClientsideVersion() }?.name
}

class InformationPlayTime : Information("Connection", "Play Time") {

    private var time = 0L

    init {
        TarasandeMain.get().managerEvent.add {
            if (it is EventConnectServer) {
                time = System.currentTimeMillis()
            }
        }
    }

    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().isInSingleplayer) return null

        return StringUtil.formatTime(System.currentTimeMillis() - this.time)
    }
}