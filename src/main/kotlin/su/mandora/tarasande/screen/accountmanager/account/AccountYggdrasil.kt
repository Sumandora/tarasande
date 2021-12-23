package su.mandora.tarasande.screen.accountmanager.account

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.mojang.authlib.Agent
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import net.minecraft.client.util.Session
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.accountmanager.account.Account
import su.mandora.tarasande.base.screen.accountmanager.account.AccountInfo
import su.mandora.tarasande.base.screen.accountmanager.account.TextFieldInfo
import java.net.Proxy
import java.util.*


@AccountInfo(name = "Yggdrasil", suitableAsMain = true)
class AccountYggdrasil(
    @TextFieldInfo(name = "Username/E-Mail", hidden = false) private val username: String,
    @TextFieldInfo(name = "Password", hidden = true) private val password: String
) : Account() {

    private var service: MinecraftSessionService? = null

    constructor(): this("", "")

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

    override fun getDisplayName() = if(session != null) session?.username!! else username

    override fun getSessionService() = this.service

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        jsonArray.add(username)
        jsonArray.add(password)
        jsonArray.add(TarasandeMain.get().gson.toJsonTree(session as Any?))
        return jsonArray
    }

    override fun load(jsonElement: JsonElement): Account {
        val jsonArray = jsonElement.asJsonArray
        val account = AccountYggdrasil(jsonArray[0].asString, jsonArray[1].asString)
        account.session = TarasandeMain.get().gson.fromJson(jsonArray[2], Session::class.java) as Session
        return account
    }

    override fun create(credentials: List<String>) = AccountYggdrasil(credentials[0], credentials[1])
}