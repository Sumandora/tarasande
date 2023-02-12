package de.florianmichael.tarasande_rejected_features.information

import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.TARASANDE_NAME
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.ManagerGraph
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information

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
