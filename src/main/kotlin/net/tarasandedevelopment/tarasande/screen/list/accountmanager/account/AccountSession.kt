package net.tarasandedevelopment.tarasande.screen.list.accountmanager.account

import com.google.gson.JsonArray
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.minecraft.client.util.Session
import net.tarasandedevelopment.tarasande.base.screen.accountmanager.account.Account
import net.tarasandedevelopment.tarasande.base.screen.accountmanager.account.AccountInfo
import net.tarasandedevelopment.tarasande.base.screen.accountmanager.account.TextFieldInfo
import java.net.Proxy
import java.util.*


@AccountInfo(name = "Session", suitableAsMain = false) // We shouldn't allow users to use cracked accounts as mains, because if we later use the main to authenticate services, we might run into problems
class AccountSession(
    @TextFieldInfo("Username", false) private val username: String,
    @TextFieldInfo("UUID", false) private val uuid: String,
    @TextFieldInfo("Access Token", false) private val accessToken: String,
    @TextFieldInfo("X Uid", false) private val xUid: String,
    @TextFieldInfo("Client Uid", false) private val clientUid: String
) : Account() {

    private var service: MinecraftSessionService? = null

    @Suppress("unused") // Reflections
    constructor() : this("", "", "", "", "")

    override fun logIn() {
        service = YggdrasilAuthenticationService(Proxy.NO_PROXY, "", environment).createMinecraftSessionService()
        session = Session(username, uuid, accessToken, if (xUid.isEmpty()) Optional.empty() else Optional.of(xUid), if (clientUid.isEmpty()) Optional.empty() else Optional.of(clientUid), if (xUid.isNotEmpty() || clientUid.isNotEmpty()) Session.AccountType.MSA else Session.AccountType.MOJANG)
    }

    override fun getDisplayName() = username

    override fun getSessionService() = service

    override fun save(): JsonArray {
        val jsonArray = JsonArray()
        jsonArray.add(username)
        jsonArray.add(uuid)
        jsonArray.add(accessToken)
        jsonArray.add(xUid)
        jsonArray.add(clientUid)
        return jsonArray
    }

    override fun load(jsonArray: JsonArray): Account {
        return AccountSession(jsonArray[0].asString, jsonArray[1].asString, jsonArray[2].asString, jsonArray[3].asString, jsonArray[4].asString)
    }

    override fun create(credentials: List<String>) = AccountSession(credentials[0], credentials[1], credentials[2], credentials[3], credentials[4])
}