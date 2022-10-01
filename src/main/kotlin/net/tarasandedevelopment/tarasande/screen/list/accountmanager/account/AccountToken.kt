package net.tarasandedevelopment.tarasande.screen.list.accountmanager.account

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.minecraft.client.util.Session
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.accountmanager.account.Account
import net.tarasandedevelopment.tarasande.base.screen.accountmanager.account.AccountInfo
import net.tarasandedevelopment.tarasande.base.screen.accountmanager.account.TextFieldInfo
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL
import java.util.*


@AccountInfo(name = "Token", suitableAsMain = false)
class AccountToken(
    @TextFieldInfo("Redeem-Url", false, default = "https://api.easymc.io/v1/token/redeem") private val redeemUrl: String,
    @TextFieldInfo("Token", false) private val token: String
) : Account() {

    private var service: MinecraftSessionService? = null

    @Suppress("unused") // Reflections
    constructor() : this("https://api.easymc.io/v1/token/redeem", "")

    override fun logIn() {
        val http = URL(redeemUrl).openConnection() as HttpURLConnection
        http.requestMethod = "POST"
        http.doOutput = true
        http.setRequestProperty("Content-Type", "application/json")
        http.connect()
        http.outputStream.write(JsonObject().also { it.addProperty("token", token) }.toString().toByteArray())

        val json = TarasandeMain.get().gson.fromJson(String(http.inputStream.readAllBytes()), JsonObject::class.java)

        val authenticationService = YggdrasilAuthenticationService(Proxy.NO_PROXY, "", environment)

        session = Session(json.get("mcName").asString, json.get("uuid").asString, json.get("session").asString, Optional.empty(), Optional.empty(), Session.AccountType.MOJANG)
        service = authenticationService.createMinecraftSessionService()
    }

    override fun getDisplayName() = if (session != null) session?.username!! else token

    override fun getSessionService() = this.service

    override fun save(): JsonArray {
        val jsonArray = JsonArray()
        jsonArray.add(redeemUrl)
        jsonArray.add(token)
        return jsonArray
    }

    override fun load(jsonArray: JsonArray): Account {
        return AccountToken(jsonArray[0].asString, jsonArray[1].asString)
    }

    override fun create(credentials: List<String>) = AccountToken(credentials[0], credentials[1])
}