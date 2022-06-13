package su.mandora.tarasande.module.render

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventGamma
import java.util.function.Consumer

class ModuleFullBright : Module("Full bright", "Makes everything bright", ModuleCategory.RENDER) {

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventGamma) {
            event.color = -0x1
        }
    }
}