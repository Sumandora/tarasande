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


@AccountInfo(name = "Session", suitableAsMain = false)
class AccountSession(
    @TextFieldInfo(name = "Username", hidden = false) private val username: String,
    @TextFieldInfo(name = "UUID", hidden = false) private val uuid: String,
    @TextFieldInfo(name = "Access Token", hidden = false) private val accessToken: String
) : Account() {

    private var service: MinecraftSessionService? = null

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