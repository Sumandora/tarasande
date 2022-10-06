package net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.account

import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.AccountInfo
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.TextFieldInfo
import java.net.ServerSocket

@AccountInfo("Refresh-Token", true)
class AccountRefreshToken(
    @TextFieldInfo("Redirect-Uri", false) private val _redirectUri: String,
    @TextFieldInfo("Client-Id", true) private val _clientId: String,
    @TextFieldInfo("Refresh-Token", true) private val token: String,
    @TextFieldInfo("Scope", false) private val _scope: String,
    @TextFieldInfo("Client-Secret", true) private val _clientSecret: String
) : AccountMicrosoft() {

    @Suppress("unused") // Reflections
    constructor() : this("", "", "", "", "")

    override fun setupHttpServer(): ServerSocket {
        error("Account is invalid")
    }

    override fun logIn() {
        clientId = _clientId
        redirectUri = _redirectUri
        scope = _scope
        if (_clientSecret.isNotEmpty()) {
            clientSecret = _clientSecret
        }
        msAuthProfile = buildFromRefreshToken(token)

        super.logIn()
    }

    override fun getDisplayName(): String {
        return if (session == null) "Unnamed Refresh-Token account" else super.getDisplayName()
    }

    override fun create(credentials: List<String>) = AccountRefreshToken(credentials[0], credentials[1], credentials[2], credentials[3], credentials[4])
}