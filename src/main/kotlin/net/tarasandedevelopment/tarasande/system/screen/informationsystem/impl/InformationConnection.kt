package net.tarasandedevelopment.tarasande.system.screen.informationsystem.impl

import net.tarasandedevelopment.tarasande.event.EventDispatcher
import net.tarasandedevelopment.tarasande.event.impl.EventConnectServer
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.util.string.StringUtil

class InformationHandlers : Information("Connection", "Handlers") {
    private val displayMode = ValueMode(this, "Display mode", false, "Names", "Size")

    override fun getMessage(): String? {
        val names = (mc.networkHandler?.connection!!.channel ?: return null).pipeline().names()
        if (names.isEmpty()) return null

        return if (displayMode.isSelected(0)) {
            "\n" + names.subList(0, names.size - 1).joinToString("\n")
        } else {
            names.size.toString()
        }
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
