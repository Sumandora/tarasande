package net.tarasandedevelopment.tarasande_rejected_features.information

import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information

class InformationFeaturesModules : Information("Features", "Modules") {

    override fun getMessage() = TarasandeMain.managerModule().list.size.toString()
}

class InformationFeaturesValues : Information("Features", "Values") {

    override fun getMessage() = TarasandeMain.managerValue().list.size.toString()
}

class InformationFeaturesGraphs : Information("Features", "Graphs") {

    override fun getMessage() = TarasandeMain.managerGraph().list.size.toString()
}

class InformationFeaturesPackagesForTarasande : Information("Features", "Packages for tarasande") {

    private val packages = ArrayList<String>()

    init {
        FabricLoader.getInstance().allMods.forEach {
            if (it.metadata.dependencies.any { dependency -> dependency.modId == "tarasande" }) {
                packages.add(it.metadata.name)
            }
        }
    }

    override fun getMessage(): String? {
        if (packages.isEmpty()) return null
        return "\n" + packages.joinToString("\n")
    }
}
