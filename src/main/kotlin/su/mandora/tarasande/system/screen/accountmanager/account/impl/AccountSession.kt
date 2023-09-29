package su.mandora.tarasande.system.screen.accountmanager.account.impl

import com.google.gson.JsonArray
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.minecraft.client.session.Session
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.accountmanager.account.Account
import su.mandora.tarasande.system.screen.accountmanager.account.api.AccountInfo
import su.mandora.tarasande.system.screen.accountmanager.account.api.TextFieldInfo
import su.mandora.tarasande.util.extension.kotlinruntime.parseUUID
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


    override fun logIn() {
        val uuid = if(this.uuid.isEmpty())
            UUID.randomUUID()
        else
            try {
                parseUUID(this.uuid)
            } catch (e: IllegalArgumentException) {
                error("Invalid UUID")
            }
        YggdrasilAuthenticationService(mc.networkProxy, environment).also {
            yggdrasilAuthenticationService = it
            minecraftSessionService = it.createMinecraftSessionService()
        }
        session = Session(username, uuid, accessToken, Optional.ofNullable(xUid), Optional.ofNullable(clientUid), if (xUid.isNotEmpty() || clientUid.isNotEmpty()) Session.AccountType.MSA else Session.AccountType.MOJANG)
    }

    override fun getDisplayName() = username

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