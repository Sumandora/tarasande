package net.tarasandedevelopment.tarasande.base.addon

import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager

class ManagerAddon : Manager<SandeEntrypoint>() {

    init {
        FabricLoader.getInstance().getEntrypointContainers("tarasande", SandeEntrypoint::class.java).forEach {
            val metadata = it.provider.metadata
            val addon = it.entrypoint

            addon.modId = metadata.id
            addon.modAuthors = metadata.authors.map { p -> p.name }
            addon.modVersion = metadata.version.friendlyString

            TarasandeMain.get().managerEvent.add(it.entrypoint.managerConsumer)

            val consumer = addon.defaultEventConsumer()
            if (consumer != null)
                TarasandeMain.get().managerEvent.add(consumer)

            this.add(addon)
            addon.create(TarasandeMain.get())
        }
    }
}
