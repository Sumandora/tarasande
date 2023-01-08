package net.tarasandedevelopment.tarasande.system.screen.informationsystem.impl

import net.tarasandedevelopment.tarasande.event.EventConnectServer
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import su.mandora.event.EventDispatcher

class InformationHandlers : Information("Connection", "Handlers") {
    override fun getMessage(): String? {
        if (mc.networkHandler == null || mc.networkHandler?.connection == null) return null
        val names = (mc.networkHandler?.connection!!.channel ?: return null).pipeline().names()
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
        if (mc.isInSingleplayer) return null

        return StringUtil.formatTime(System.currentTimeMillis() - this.time)
    }
}