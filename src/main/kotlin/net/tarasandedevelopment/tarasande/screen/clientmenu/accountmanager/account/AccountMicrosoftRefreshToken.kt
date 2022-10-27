package net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.account

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.AccountInfo
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.TextFieldInfo
import net.tarasandedevelopment.tarasande.screen.clientmenu.ElementMenuScreenAccountManager
import net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.azureapp.AzureAppPresetInGameAccountSwitcher
import java.net.ServerSocket

@AccountInfo("Refresh-Token")
class AccountMicrosoftRefreshToken : AccountMicrosoft() {

    @TextFieldInfo("Refresh-Token", true)
    private var token: String = ""

    override fun setupHttpServer(): ServerSocket {
        error("Account is invalid")
    }

    override fun logIn() {
        if (azureApp == null) {
            azureApp = TarasandeMain.get().managerClientMenu.get(ElementMenuScreenAccountManager::class.java).screenBetterSlotListAccountManager.managerAzureApp.get(AzureAppPresetInGameAccountSwitcher::class.java)
        }
        redirectUri = azureApp!!.redirectUri + randomPort()
        msAuthProfile = buildFromRefreshToken(token)

        super.logIn()
    }

    override fun getDisplayName(): String {
        return if (session == null) "Unnamed Refresh-Token account" else super.getDisplayName()
    }

    override fun create(credentials: List<String>) {
        token = credentials[0]
    }
}
