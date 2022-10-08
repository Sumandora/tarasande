package net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.azureapp

import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.azureapp.*
import java.util.*

class ManagerAzureApp : Manager<AzureAppPreset>() {

    init {
        add(
            AzureAppPresetInGameAccountSwitcher(),
            AzureAppPresetBashAuth(),
            AzureAppPresetPolyMC(),
            AzureAppPresetMultiMC(),
            AzureAppPresetTechnicLauncher(),
            AzureAppPresetLabyMod(),
            AzureAppPresetOldTechnicLauncher(),
            AzureAppPresetGDLauncher()
        )
    }

}

open class AzureAppPreset(val name: String, val clientId: UUID, val scope: String, val redirectUri: String = "http://localhost:", val clientSecret: String? = null)
