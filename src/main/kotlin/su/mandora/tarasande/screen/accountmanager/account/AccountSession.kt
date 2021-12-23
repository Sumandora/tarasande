package su.mandora.tarasande.screen.accountmanager.account

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.minecraft.client.util.Session
import su.mandora.tarasande.TarasandeMain
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

    override fun logIn() {
        service = YggdrasilAuthenticationService(Proxy.NO_PROXY, "", environment).createMinecraftSessionService()
        session = Session(username, uuid, accessToken, Optional.empty(), Optional.empty(), Session.AccountType.MOJANG)
    }

    override fun getDisplayName() = username

    override fun getSessionService() = service

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        jsonArray.add(username)
        jsonArray.add(uuid)
        jsonArray.add(accessToken)
        jsonArray.add(TarasandeMain.get().gson.toJsonTree(session as Any?))
        return jsonArray
    }

    override fun load(jsonElement: JsonElement): Account {
        val jsonArray = jsonElement.asJsonArray
        val account = AccountSession(jsonArray[0].asString, jsonArray[1].asString, jsonArray[2].asString)
        account.session = TarasandeMain.get().gson.fromJson(jsonArray[3], Session::class.java) as Session
        return account
    }

    override fun create(credentials: List<String>) = AccountSession(credentials[0], credentials[1], credentials[2])
}