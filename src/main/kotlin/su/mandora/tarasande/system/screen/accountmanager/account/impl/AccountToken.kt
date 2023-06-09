package su.mandora.tarasande.system.screen.accountmanager.account.impl

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.minecraft.client.util.Session
import su.mandora.tarasande.gson
import su.mandora.tarasande.system.screen.accountmanager.account.Account
import su.mandora.tarasande.system.screen.accountmanager.account.api.AccountInfo
import su.mandora.tarasande.system.screen.accountmanager.account.api.TextFieldInfo
import su.mandora.tarasande.system.screen.accountmanager.environment.ManagerEnvironment
import su.mandora.tarasande.system.screen.accountmanager.environment.impl.EnvironmentPresetEasyMC
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL
import java.util.*


@AccountInfo(name = "Token")
class AccountToken : Account() {

    @TextFieldInfo("Redeem-Url", false, default = "https://api.easymc.io/v1/token/redeem")
    private var redeemUrl = ""

    @TextFieldInfo("Token", false)
    private var token = ""

    init {
        // Default environment
        environment = ManagerEnvironment.get(EnvironmentPresetEasyMC::class.java).create()
    }

    override fun logIn() {
        val http = URL(redeemUrl).openConnection() as HttpURLConnection
        http.requestMethod = "POST"
        http.doOutput = true
        http.setRequestProperty("Content-Type", "application/json")
        http.connect()
        http.outputStream.write(JsonObject().also { it.addProperty("token", token) }.toString().toByteArray())

        val json = gson.fromJson(String(http.inputStream.readAllBytes()), JsonObject::class.java)

        session = Session(json["mcName"].asString, json["uuid"].asString, json["session"].asString, Optional.empty(), Optional.empty(), Session.AccountType.MOJANG)
        service = YggdrasilAuthenticationService(Proxy.NO_PROXY, "", environment).createMinecraftSessionService()
    }

    override fun getDisplayName() = if (session != null) session?.username!! else token

    override fun save(): JsonArray {
        val jsonArray = JsonArray()
        jsonArray.add(redeemUrl)
        jsonArray.add(token)
        return jsonArray
    }

    override fun load(jsonArray: JsonArray): Account {
        val account = AccountToken()
        account.redeemUrl = jsonArray[0].asString
        account.token = jsonArray[1].asString

        return account
    }

    override fun create(credentials: List<String>) {
        redeemUrl = credentials[0]
        token = credentials[1]
    }
}