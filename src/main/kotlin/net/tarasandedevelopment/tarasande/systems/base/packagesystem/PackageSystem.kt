package net.tarasandedevelopment.tarasande.systems.base.packagesystem

import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.base.Manager

internal class ManagerPackage : Manager<net.tarasandedevelopment.tarasande.systems.base.packagesystem.Package>() {

    init {
        FabricLoader.getInstance().getEntrypointContainers("tarasande", net.tarasandedevelopment.tarasande.systems.base.packagesystem.Package::class.java).forEach {
            val metadata = it.provider.metadata
            val addon = it.entrypoint

            addon.modId = metadata.id
            addon.modAuthors = metadata.authors.map { p -> p.name }
            addon.modVersion = metadata.version.friendlyString

            this.add(addon)
        }
    }
}

internal data class Package(
    var modId: String,
    var modAuthors: List<String>,
    var modVersion: String
)
