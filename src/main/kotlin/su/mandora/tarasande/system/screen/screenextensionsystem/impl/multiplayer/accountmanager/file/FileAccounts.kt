package su.mandora.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.file

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.authlib.Environment
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.minecraft.client.util.Session
import org.apache.commons.codec.binary.Hex
import oshi.SystemInfo
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.filesystem.File
import su.mandora.tarasande.system.screen.accountmanager.account.ManagerAccount
import su.mandora.tarasande.system.screen.accountmanager.account.api.AccountInfo
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.ScreenBetterSlotListAccountManager
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class FileAccounts(private val accountManager: ScreenBetterSlotListAccountManager) : File("Accounts") {

    private var encryptCipher: Cipher? = null
    private var decryptCipher: Cipher? = null

    init {
        val hwid = SystemInfo().hardware.computerSystem.hardwareUUID
        if (hwid != null && !hwid.equals("unknown")) { // if the HWID is nonexistent, we just don't encrypt at all
            var bytes = Base64.getEncoder().encode(String(Hex.encodeHex(hwid.toByteArray())).toByteArray())
            when {
                bytes.size > 32 -> bytes = Arrays.copyOfRange(bytes, 0, 32)
                bytes.size > 24 -> bytes = Arrays.copyOfRange(bytes, 0, 24)
                bytes.size > 16 -> bytes = Arrays.copyOfRange(bytes, 0, 16)
            }
            val keySpec = SecretKeySpec(bytes, 0, bytes.size, "AES")
            encryptCipher = Cipher.getInstance("AES").also {
                it.init(Cipher.ENCRYPT_MODE, keySpec)
            }
            decryptCipher = Cipher.getInstance("AES").also {
                it.init(Cipher.DECRYPT_MODE, keySpec)
            }
        }
    }

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
                sessionObject.addProperty("UUID", account.session?.uuid)
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
            environment.addProperty("Auth-Host", account.environment.authHost)
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
                            sessionObject["UUID"].asString,
                            sessionObject["Access-Token"].asString,
                            if (sessionObject.has("X-Uid")) Optional.of(sessionObject["X-Uid"].asString) else Optional.empty(),
                            if (sessionObject.has("Client-Uid")) Optional.of(sessionObject["Client-Uid"].asString) else Optional.empty(),
                            Session.AccountType.valueOf(value = sessionObject["Account-Type"].asString)
                        )
                    }

                    val environment = accountObject.getAsJsonObject("Environment")
                    accountImplementation.environment = Environment.create(
                        environment["Auth-Host"].asString,
                        environment["Accounts-Host"].asString,
                        environment["Session-Host"].asString,
                        environment["Services-Host"].asString,
                        TARASANDE_NAME
                    )

                    accountImplementation.service = YggdrasilAuthenticationService(mc.networkProxy, "", accountImplementation.environment).createMinecraftSessionService()

                    accountManager.accounts.add(accountImplementation)
                }
            }
        }
        if (jsonObject.has("Main-Account"))
            accountManager.mainAccount = jsonObject["Main-Account"].asInt
    }

    override fun encrypt(input: String): String {
        return String(Base64.getEncoder().encode(encryptCipher?.doFinal(input.toByteArray()) ?: return input))
    }

    override fun decrypt(input: String): String? {
        return try {
            String(decryptCipher?.doFinal(Base64.getDecoder().decode(input.toByteArray())) ?: return input)
        } catch (e: BadPaddingException) {
            e.printStackTrace()
            input
        }
    }
}