package su.mandora.tarasande.system.screen.informationsystem.impl

import org.apache.commons.lang3.time.DurationFormatUtils
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventConnectServer
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.screen.informationsystem.Information

class InformationHandlers : Information("Connection", "Handlers") {
    private val displayMode = ValueMode(this, "Display mode", false, "Names", "Count")

    override fun getMessage(): String? {
        val names = (mc.networkHandler?.connection!!.channel ?: return null).pipeline().names()
        if (names.isEmpty()) return null

        return when {
            displayMode.isSelected(0) -> "\n" + names.subList(0, names.size - 1).joinToString("\n")
            displayMode.isSelected(1) -> names.size.toString()
            else -> null
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

        return DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - this.time, true, false)
    }
}
