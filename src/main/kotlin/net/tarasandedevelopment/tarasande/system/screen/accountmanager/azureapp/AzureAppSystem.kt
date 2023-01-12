package net.tarasandedevelopment.tarasande.system.screen.accountmanager.azureapp

import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.azureapp.impl.*

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

open class AzureAppPreset(val name: String, val clientId: String, val scope: String, val redirectUri: String = "http://localhost:", val clientSecret: String? = null)
