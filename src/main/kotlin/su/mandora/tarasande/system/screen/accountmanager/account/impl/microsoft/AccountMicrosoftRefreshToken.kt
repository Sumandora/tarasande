package su.mandora.tarasande.system.screen.accountmanager.account.impl.microsoft

import net.minecraft.client.gui.screen.Screen
import su.mandora.tarasande.feature.screen.accountmanager.subscreen.ScreenBetterAzureApps
import su.mandora.tarasande.feature.screen.accountmanager.subscreen.ScreenBetterEnvironment
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.accountmanager.account.api.AccountInfo
import su.mandora.tarasande.system.screen.accountmanager.account.api.ExtraInfo
import su.mandora.tarasande.system.screen.accountmanager.account.api.TextFieldInfo

@AccountInfo("Refresh-Token", inherit = false)
class AccountMicrosoftRefreshToken : AccountMicrosoft() {

    @TextFieldInfo("Refresh-Token", true)
    private var token = ""

    override fun logIn() {
        msAuthProfile = buildFromRefreshToken(token)

        super.logIn()
    }

    override fun getDisplayName(): String {
        return if (session == null) "Unnamed Refresh-Token account" else super.getDisplayName()
    }

    override fun create(credentials: List<String>) {
        token = credentials[0]
    }

    @Suppress("unused")
    @ExtraInfo("Azure Apps")
    val azureApps: (Screen, Runnable) -> Unit = { screen, _ ->
        mc.setScreen(ScreenBetterAzureApps(screen, azureApp) { newAzureApp ->
            azureApp = newAzureApp
        })
    }

    @Suppress("unused")
    @ExtraInfo("Environment")
    val newEnvironmentExtra: (Screen, Runnable) -> Unit = { screen, _ ->
        mc.setScreen(ScreenBetterEnvironment(screen, environment) { newEnvironment ->
            environment = newEnvironment
        })
    }
}
