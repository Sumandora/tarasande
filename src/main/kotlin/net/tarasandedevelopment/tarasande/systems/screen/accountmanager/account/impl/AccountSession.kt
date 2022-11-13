package net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.impl

import com.google.gson.JsonArray
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.minecraft.client.util.Session
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.Account
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.api.AccountInfo
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.api.TextFieldInfo
import java.net.Proxy
import java.util.*


@AccountInfo(name = "Session")
class AccountSession : Account() {

    @TextFieldInfo("Username", false)
    var username = ""

    @TextFieldInfo("UUID", false)
    var uuid = ""

    @TextFieldInfo("Access Token", false)
    private var accessToken = ""

    @TextFieldInfo("X Uid", false)
    private var xUid = ""

    @TextFieldInfo("Client Uid", false)
    private var clientUid = ""

    private var service: MinecraftSessionService? = null

    override fun logIn() {
        uuid = UUID.randomUUID().toString()
        service = YggdrasilAuthenticationService(Proxy.NO_PROXY, "", environment).createMinecraftSessionService()
        session = Session(username, uuid, accessToken, Optional.ofNullable(xUid), Optional.ofNullable(clientUid), if (xUid.isNotEmpty() || clientUid.isNotEmpty()) Session.AccountType.MSA else Session.AccountType.MOJANG)
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
        val account = AccountSession()

        account.username = jsonArray[0].asString
        account.uuid = jsonArray[1].asString
        account.accessToken = jsonArray[2].asString
        account.xUid = jsonArray[3].asString
        account.clientUid = jsonArray[4].asString

        return account
    }

    override fun create(credentials: List<String>) {
        username = credentials[0]
        uuid = credentials[1]
        accessToken = credentials[2]
        xUid = credentials[3]
        clientUid = credentials[4]
    }
}