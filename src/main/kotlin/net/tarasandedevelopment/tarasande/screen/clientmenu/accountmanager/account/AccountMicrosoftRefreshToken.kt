package net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.account

import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.AccountInfo
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.TextFieldInfo
import java.net.ServerSocket

@AccountInfo("Refresh-Token")
class AccountMicrosoftRefreshToken : AccountMicrosoft() {

    @TextFieldInfo("Refresh-Token", true)
    private var token: String = ""

    override fun setupHttpServer(): ServerSocket {
        error("Account is invalid")
    }

    override fun logIn() {
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
