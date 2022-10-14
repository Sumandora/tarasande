package net.tarasandedevelopment.tarasande.base.addon

import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager

class ManagerAddon : Manager<Addon>() {

    init {
        FabricLoader.getInstance().getEntrypointContainers("tarasande", Addon::class.java).forEach {
            val metadata = it.provider.metadata
            val addon = it.entrypoint

            addon.modId = metadata.id
            addon.modAuthors = metadata.authors.map { p -> p.name }
            addon.modVersion = metadata.version.friendlyString

            this.add(addon)
            addon.create(TarasandeMain.get())
        }
    }
}

abstract class Addon {
    var modId: String? = null
    var modAuthors: List<String>? = null
    var modVersion: String? = null

    abstract fun create(tarasandeMain: TarasandeMain?)
    abstract fun onLoadManager(manager: Manager<*>?)
}
