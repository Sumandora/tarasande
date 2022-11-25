package net.tarasandedevelopment.tarasande.util.connection

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.tarasandedevelopment.tarasande.event.EventConnectServer
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import su.mandora.events.EventDispatcher

object AddressSaver {

    private var address = ""

    init {
        EventDispatcher.apply {
            add(EventConnectServer::class.java) {
                address = it.address.hostString + ":" + it.address.port
            }
            add(EventDisconnect::class.java) {
                if (it.connection == MinecraftClient.getInstance().networkHandler?.connection) {
                    address = ""
                }
            }
        }
    }

    fun getAddress(): String {
        if (MinecraftClient.getInstance().world == null && MinecraftClient.getInstance().player == null) {
            MinecraftClient.getInstance().currentScreen.apply {
                if (this is DirectConnectScreen && addressField != null) {
                    return addressField.text
                }
            }
        }
        return address
    }
}
