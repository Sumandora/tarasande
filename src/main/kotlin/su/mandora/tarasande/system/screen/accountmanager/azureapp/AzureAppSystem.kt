package su.mandora.tarasande.system.screen.accountmanager.azureapp

import su.mandora.tarasande.Manager
import su.mandora.tarasande.system.screen.accountmanager.azureapp.impl.AzureAppPresetInGameAccountSwitcher

object ManagerAzureApp : Manager<AzureAppPreset>() {

    init {
        add(
            AzureAppPresetInGameAccountSwitcher()
        )
    }

}

open class AzureAppPreset(val name: String, val clientId: String, val scope: String, val redirectUri: String = "http://localhost:", val clientSecret: String? = null)
