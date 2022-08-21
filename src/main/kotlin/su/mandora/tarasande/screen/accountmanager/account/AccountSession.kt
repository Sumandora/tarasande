package su.mandora.tarasande.screen.accountmanager.account

import com.google.gson.JsonArray
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.minecraft.client.util.Session
import su.mandora.tarasande.base.screen.accountmanager.account.Account
import su.mandora.tarasande.base.screen.accountmanager.account.AccountInfo
import su.mandora.tarasande.base.screen.accountmanager.account.TextFieldInfo
import java.net.Proxy
import java.util.*


@AccountInfo(name = "Session", suitableAsMain = false) // We shouldn't allow users to use cracked accounts as mains, because if we later use the main to authenticate services, we might run into problems
class AccountSession(
    @TextFieldInfo("Username", false) private val username: String,
    @TextFieldInfo("UUID", false) private val uuid: String,
    @TextFieldInfo("Access Token", false) private val accessToken: String
) : Account() {

    private var service: MinecraftSessionService? = null

    @Suppress("unused") // Reflections
    constructor() : this("", "", "")

    override fun logIn() {
        service = YggdrasilAuthenticationService(Proxy.NO_PROXY, "", environment).createMinecraftSessionService()
        session = Session(username, uuid, accessToken, Optional.empty(), Optional.empty(), Session.AccountType.MOJANG)
    }

    override fun getDisplayName() = username

    override fun getSessionService() = service

    override fun save(): JsonArray {
        val jsonArray = JsonArray()
        jsonArray.add(username)
        jsonArray.add(uuid)
        jsonArray.add(accessToken)
        return jsonArray
    }

    override fun load(jsonArray: JsonArray): Account {
        return AccountSession(jsonArray[0].asString, jsonArray[1].asString, jsonArray[2].asString)
    }

    override fun create(credentials: List<String>) = AccountSession(credentials[0], credentials[1], credentials[2])
}