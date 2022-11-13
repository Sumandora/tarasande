package net.tarasandedevelopment.tarasande.systems.base.packagesystem

import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.Manager

internal class ManagerPackage : Manager<Package>() {

    init {
        FabricLoader.getInstance().getEntrypointContainers("tarasande", java.lang.Object::class.java).forEach {
            val metadata = it.provider.metadata
            this.add(Package(
                metadata.id,
                metadata.authors.map { p -> p.name },
                metadata.version.friendlyString
            ))
        }
    }
}

internal data class Package(
    var modId: String,
    var modAuthors: List<String>,
    var modVersion: String
)
