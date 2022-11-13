package net.tarasandedevelopment.tarasande.systems.screen.informationsystem.impl

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.event.EventDispatcher
import net.tarasandedevelopment.tarasande.events.EventConnectServer
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.util.string.StringUtil

class InformationHandlers : Information("Connection", "Handlers") {
    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().networkHandler == null || MinecraftClient.getInstance().networkHandler?.connection == null) return null
        val names = (MinecraftClient.getInstance().networkHandler?.connection!!.channel ?: return null).pipeline().names()
        if (names.isEmpty()) return null
        return "\n" + names.subList(0, names.size - 1).joinToString("\n")
    }
}

class InformationPlayTime : Information("Connection", "Play Time") {

    private var time = 0L

    init {
        EventDispatcher.add(EventConnectServer::class.java) {
            time = System.currentTimeMillis()
        }
    }

    override fun getMessage(): String? {
        if (MinecraftClient.getInstance().isInSingleplayer) return null

        return StringUtil.formatTime(System.currentTimeMillis() - this.time)
    }
}