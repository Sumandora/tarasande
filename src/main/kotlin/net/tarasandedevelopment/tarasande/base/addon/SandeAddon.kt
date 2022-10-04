package net.tarasandedevelopment.tarasande.base.addon

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.event.EventLoadManager
import java.util.function.Consumer

abstract class SandeAddon {
    val managerConsumer = Consumer { event: Event -> if (event is EventLoadManager) onLoadManager(event.manager) }

    var modId: String? = null
    var modAuthors: List<String>? = null
    var modVersion: String? = null

    abstract fun create(tarasandeMain: TarasandeMain?)
    abstract fun onLoadManager(manager: Manager<*>?)

    fun defaultEventConsumer(): Consumer<Event>? {
        return null
    }
}
