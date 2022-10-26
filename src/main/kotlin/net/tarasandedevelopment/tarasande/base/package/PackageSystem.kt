package net.tarasandedevelopment.tarasande.base.`package`

import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager

class ManagerPackage : Manager<Package>() {

    init {
        FabricLoader.getInstance().getEntrypointContainers("tarasande", Package::class.java).forEach {
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

abstract class Package {
    var modId: String? = null
    var modAuthors: List<String>? = null
    var modVersion: String? = null

    abstract fun create(tarasandeMain: TarasandeMain?)
}
