package net.tarasandedevelopment.tarasande_rejected_features.information

import de.florianmichael.viaprotocolhack.util.VersionList
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information

class InformationFeaturesModules : Information("Features", "Modules") {

    override fun getMessage() = TarasandeMain.managerModule().list.size.toString()
}

class InformationFeaturesProtocols : Information("Features", "Protocols") {

    override fun getMessage() = VersionList.PROTOCOLS.size.toString()
}

class InformationFeaturesValues : Information("Features", "Values") {

    override fun getMessage() = TarasandeMain.managerValue().list.size.toString()
}

class InformationFeaturesGraphs : Information("Features", "Graphs") {

    override fun getMessage() = TarasandeMain.managerGraph().list.size.toString()
}