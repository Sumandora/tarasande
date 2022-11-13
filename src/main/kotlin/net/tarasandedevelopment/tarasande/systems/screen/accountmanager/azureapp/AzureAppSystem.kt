package net.tarasandedevelopment.tarasande.systems.screen.accountmanager.azureapp

import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.azureapp.impl.*
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
