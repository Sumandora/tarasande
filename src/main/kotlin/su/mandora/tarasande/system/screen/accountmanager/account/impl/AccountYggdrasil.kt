package su.mandora.tarasande.system.screen.accountmanager.account.impl

import com.google.gson.JsonArray
import net.minecraft.client.session.Session
import su.mandora.authlib.Agent
import su.mandora.authlib.yggdrasil.YggdrasilAuthenticationService
import su.mandora.authlib.yggdrasil.YggdrasilEnvironment
import su.mandora.authlib.yggdrasil.YggdrasilUserAuthentication
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.accountmanager.account.Account
import su.mandora.tarasande.system.screen.accountmanager.account.api.AccountInfo
import su.mandora.tarasande.system.screen.accountmanager.account.api.TextFieldInfo
import su.mandora.tarasande.util.extension.kotlinruntime.parseUUID
import java.net.URL
import java.util.*
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService as MojangYggdrasilAuthenticationService

@AccountInfo(name = "Yggdrasil")
class AccountYggdrasil : Account() {

    @TextFieldInfo("Auth-host", false, default = YggdrasilEnvironment.PROD_AUTH_HOST + "/authenticate")
    var authHost = ""

    @TextFieldInfo("Username/E-Mail", false)
    var username = ""

    @TextFieldInfo("Password", true)
    var password = ""

    override fun logIn() {
        val userAuthentication = YggdrasilUserAuthentication(YggdrasilAuthenticationService(mc.networkProxy), "", Agent.MINECRAFT, URL(authHost))
        userAuthentication.setUsername(username)
        userAuthentication.setPassword(password)
        userAuthentication.logIn()
        if (userAuthentication.isLoggedIn) {
            session = Session(userAuthentication.selectedProfile?.name, parseUUID(userAuthentication.selectedProfile!!.id!!), userAuthentication.authenticatedToken, Optional.empty(), Optional.empty(), Session.AccountType.MOJANG)
            MojangYggdrasilAuthenticationService(mc.networkProxy, environment).also {
                yggdrasilAuthenticationService = it
                minecraftSessionService = it.createMinecraftSessionService()
            }
        }
    }

    override fun getDisplayName() = if (session != null) session?.username!! else username

    override fun save(): JsonArray {
        val jsonArray = JsonArray()
        jsonArray.add(username)
        jsonArray.add(password)
        jsonArray.add(authHost)
        return jsonArray
    }

    override fun load(jsonArray: JsonArray): Account {
        val account = AccountYggdrasil()
        account.username = jsonArray[0].asString
        account.password = jsonArray[1].asString
        account.authHost = jsonArray[2].asString

        return account
    }

    override fun create(credentials: List<String>) {
        authHost = credentials[0]
        username = credentials[1]
        password = credentials[2]
    }
}