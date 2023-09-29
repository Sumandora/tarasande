package su.mandora.tarasande.feature.screen.accountmanager.file

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.authlib.Environment
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.minecraft.client.session.Session
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.feature.screen.accountmanager.ScreenBetterSlotListAccountManager
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.filesystem.File
import su.mandora.tarasande.system.screen.accountmanager.account.ManagerAccount
import su.mandora.tarasande.system.screen.accountmanager.account.api.AccountInfo
import su.mandora.tarasande.util.extension.kotlinruntime.parseUUID
import java.util.*

class FileAccounts(private val accountManager: ScreenBetterSlotListAccountManager) : File("Accounts") {

    override fun save(): JsonElement {
        val jsonObject = JsonObject()
        val jsonArray = JsonArray()
        for (account in accountManager.accounts) {
            val saveData = account.save() ?: continue

            val accountObject = JsonObject()
            accountObject.addProperty("Type", account.javaClass.getAnnotation(AccountInfo::class.java).name)
            accountObject.add("Account", saveData)
            if (account.session != null) {
                val sessionObject = JsonObject()
                sessionObject.addProperty("Username", account.session?.username)
                sessionObject.addProperty("UUID", account.session?.uuidOrNull?.toString())
                sessionObject.addProperty("Access-Token", account.session?.accessToken)
                account.session?.xuid?.also {
                    if (it.isPresent)
                        sessionObject.addProperty("X-Uid", it.get())
                }
                account.session?.clientId?.also {
                    if (it.isPresent)
                        sessionObject.addProperty("Client-Uid", it.get())
                }
                sessionObject.addProperty("Account-Type", account.session?.accountType?.name)
                accountObject.add("Session", sessionObject)
            }
            val environment = JsonObject()
            environment.addProperty("Accounts-Host", account.environment.accountsHost)
            environment.addProperty("Session-Host", account.environment.sessionHost)
            environment.addProperty("Services-Host", account.environment.servicesHost)
            accountObject.add("Environment", environment)
            jsonArray.add(accountObject)
        }
        jsonObject.add("Accounts", jsonArray)
        accountManager.mainAccount?.apply {
            jsonObject.addProperty("Main-Account", this)
        }
        return jsonObject
    }

    override fun load(jsonElement: JsonElement) {
        val jsonObject: JsonObject = jsonElement as JsonObject
        for (account in jsonObject.getAsJsonArray("Accounts")) {
            for (accountClass in ManagerAccount.list) {
                val accountObject = account.asJsonObject
                if (accountClass.getAnnotation(AccountInfo::class.java).name == accountObject["Type"].asString) {
                    val accountImplementation = accountClass.getDeclaredConstructor().newInstance().load(accountObject["Account"].asJsonArray)

                    if (accountObject.has("Session")) {
                        val sessionObject = accountObject["Session"].asJsonObject
                        accountImplementation.session = Session(
                            sessionObject["Username"].asString,
                            parseUUID(sessionObject["UUID"].asString),
                            sessionObject["Access-Token"].asString,
                            if (sessionObject.has("X-Uid")) Optional.of(sessionObject["X-Uid"].asString) else Optional.empty(),
                            if (sessionObject.has("Client-Uid")) Optional.of(sessionObject["Client-Uid"].asString) else Optional.empty(),
                            Session.AccountType.valueOf(value = sessionObject["Account-Type"].asString)
                        )
                    }

                    val environment = accountObject.getAsJsonObject("Environment")
                    accountImplementation.environment = Environment(
                        environment["Accounts-Host"].asString,
                        environment["Session-Host"].asString,
                        environment["Services-Host"].asString,
                        TARASANDE_NAME
                    )
                    YggdrasilAuthenticationService(mc.networkProxy, accountImplementation.environment).also {
                        accountImplementation.yggdrasilAuthenticationService = it
                        accountImplementation.minecraftSessionService = it.createMinecraftSessionService()
                    }

                    accountManager.accounts.add(accountImplementation)
                }
            }
        }
        if (jsonObject.has("Main-Account"))
            accountManager.mainAccount = jsonObject["Main-Account"].asInt
    }
}