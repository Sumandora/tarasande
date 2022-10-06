package net.tarasandedevelopment.tarasande.screen.cheatmenu.information

import de.florianmichael.viaprotocolhack.util.VersionList
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information.Information

class InformationFeaturesModules : Information("Features", "Modules") {

    override fun getMessage() = TarasandeMain.get().managerModule.list.size.toString()
}

class InformationFeaturesProtocols : Information("Features", "Protocols") {

    override fun getMessage() = VersionList.getProtocols().size.toString()
}

class InformationFeaturesCreativeItems : Information("Features", "Creative Items") {

    override fun getMessage() = TarasandeMain.get().screenCheatMenu.managerCreative.list.size.toString()
}
