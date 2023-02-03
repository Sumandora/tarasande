package de.florianmichael.tarasande_protocol_hack.tarasande.account

import com.google.gson.JsonArray
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import de.florianmichael.tarasande_protocol_hack.xbox.XboxLiveSession
import de.florianmichael.tarasande_protocol_hack.xbox.XboxUserAuthentication
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.Account
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.AccountInfo
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.TextFieldInfo
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.microsoft.*
import java.net.*

@AccountInfo("Bedrock")
class AccountBedrock : Account() {

    @TextFieldInfo("E-Mail", false)
    var email = ""

    @TextFieldInfo("Password", true)
    var password = ""

    private var service: MinecraftSessionService? = null

    override fun logIn() {
        val accessToken = XboxUserAuthentication.authenticate(email, password)
        val xboxLiveSession = XboxLiveSession.create(accessToken)

        session = xboxLiveSession
        service = YggdrasilAuthenticationService(Proxy.NO_PROXY, "", environment).createMinecraftSessionService()
    }

    override fun getDisplayName() = if (session != null) session?.username!! else if(email.isNotEmpty()) email else "Unnamed Bedrock-account"

    override fun getSessionService(): MinecraftSessionService? = service

    override fun save(): JsonArray {
        val jsonArray = JsonArray()
        jsonArray.add(email)
        jsonArray.add(password)
        return jsonArray
    }

    override fun load(jsonArray: JsonArray): Account {
        val account = AccountBedrock()

        account.email = jsonArray[0].asString
        account.password = jsonArray[1].asString

        return account
    }

    override fun create(credentials: List<String>) {
        email = credentials[0]
        password = credentials[1]
    }
}