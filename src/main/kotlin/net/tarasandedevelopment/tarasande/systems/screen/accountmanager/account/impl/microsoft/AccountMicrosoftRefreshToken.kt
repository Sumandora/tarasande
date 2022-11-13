package net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.impl.microsoft

import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.api.AccountInfo
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.api.TextFieldInfo
import java.net.ServerSocket

@AccountInfo("Refresh-Token")
class AccountMicrosoftRefreshToken : AccountMicrosoft() {

    @TextFieldInfo("Refresh-Token", true)
    private var token = ""

    override fun setupHttpServer(): ServerSocket {
        error("Account is invalid")
    }

    override fun logIn() {
        redirectUri = azureApp.redirectUri + randomPort()
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
