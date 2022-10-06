package net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.account

import com.google.gson.JsonArray
import com.mojang.authlib.Agent
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import net.minecraft.client.util.Session
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.Account
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.AccountInfo
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.TextFieldInfo
import java.net.Proxy
import java.util.*


@AccountInfo(name = "Yggdrasil", suitableAsMain = true)
class AccountYggdrasil(
    @TextFieldInfo("Username/E-Mail", false) private val username: String,
    @TextFieldInfo("Password", true) private val password: String
) : Account() {

    private var service: MinecraftSessionService? = null

    @Suppress("unused") // Reflections
    constructor() : this("", "")

    override fun logIn() {
        val authenticationService = YggdrasilAuthenticationService(Proxy.NO_PROXY, "", environment)
        val userAuthentication = YggdrasilUserAuthentication(authenticationService, "", Agent.MINECRAFT, environment)
        userAuthentication.setUsername(username)
        userAuthentication.setPassword(password)
        userAuthentication.logIn()
        if (userAuthentication.isLoggedIn) {
            session = Session(userAuthentication.selectedProfile.name, userAuthentication.selectedProfile.id.toString(), userAuthentication.authenticatedToken, Optional.empty(), Optional.empty(), Session.AccountType.MOJANG)
            service = authenticationService.createMinecraftSessionService()
        }
    }

    override fun getDisplayName() = if (session != null) session?.username!! else username

    override fun getSessionService() = this.service

    override fun save(): JsonArray {
        val jsonArray = JsonArray()
        jsonArray.add(username)
        jsonArray.add(password)
        return jsonArray
    }

    override fun load(jsonArray: JsonArray): Account {
        return AccountYggdrasil(jsonArray[0].asString, jsonArray[1].asString)
    }

    override fun create(credentials: List<String>) = AccountYggdrasil(credentials[0], credentials[1])
}