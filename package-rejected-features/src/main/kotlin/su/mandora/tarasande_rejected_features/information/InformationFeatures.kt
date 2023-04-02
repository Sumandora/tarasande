package su.mandora.tarasande_rejected_features.information

import net.fabricmc.loader.api.FabricLoader
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.system.base.valuesystem.ManagerValue
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.screen.graphsystem.ManagerGraph
import su.mandora.tarasande.system.screen.informationsystem.Information

class InformationFeaturesModules : Information("Features", "Modules") {

    override fun getMessage() = ManagerModule.list.size.toString()
}

class InformationFeaturesValues : Information("Features", "Values") {

    override fun getMessage() = ManagerValue.list.size.toString()
}

class InformationFeaturesGraphs : Information("Features", "Graphs") {

    override fun getMessage() = ManagerGraph.list.size.toString()
}

class InformationFeaturesPackagesForTarasande : Information("Features", "Packages for $TARASANDE_NAME") {

    private val packages = ArrayList<String>()

    init {
        FabricLoader.getInstance().allMods.forEach {
            if (it.metadata.dependencies.any { dependency -> dependency.modId == TARASANDE_NAME }) {
                packages.add(it.metadata.name)
            }
        }
    }

    override fun getMessage(): String? {
        if (packages.isEmpty()) return null
        return "\n" + packages.joinToString("\n")
    }
}
