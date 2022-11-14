package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.minecraftmenus.accountmanager.file

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.authlib.Environment
import net.minecraft.client.util.Session
import net.tarasandedevelopment.tarasande.systems.base.filesystem.File
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.api.AccountInfo
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.minecraftmenus.accountmanager.ScreenBetterSlotListAccountManager
import org.apache.commons.codec.binary.Hex
import oshi.SystemInfo
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class FileAccounts(private val accountManager: ScreenBetterSlotListAccountManager) : File("Accounts") {

    private var encryptCipher: Cipher? = null
    private var decryptCipher: Cipher? = null

    init {
        val hwid = SystemInfo().hardware.computerSystem.hardwareUUID
        if (hwid != null && !hwid.equals("unknown")) { // if the hwid is non-existant, we just don't encrypt at all
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
            val accountObject = JsonObject()
            accountObject.addProperty("Type", account.javaClass.getAnnotation(AccountInfo::class.java).name)
            accountObject.add("Account", account.save())
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
            environment.addProperty("Auth-Host", account.environment?.authHost)
            environment.addProperty("Accounts-Host", account.environment?.accountsHost)
            environment.addProperty("Session-Host", account.environment?.sessionHost)
            environment.addProperty("Services-Host", account.environment?.servicesHost)
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
        for (jsonElement2 in jsonObject.getAsJsonArray("Accounts")) {
            for (accountClass in accountManager.managerAccount.list) {
                val jsonObject2 = jsonElement2 as JsonObject
                if (accountClass.getAnnotation(AccountInfo::class.java).name == jsonObject2.get("Type").asString) {
                    val account = accountClass.getDeclaredConstructor().newInstance().load(jsonObject2.get("Account").asJsonArray)

                    if (jsonObject2.has("Session")) {
                        val sessionObject = jsonObject2.get("Session").asJsonObject
                        account.session = Session(
                            sessionObject.get("Username").asString,
                            sessionObject.get("UUID").asString,
                            sessionObject.get("Access-Token").asString,
                            if (sessionObject.has("X-Uid")) Optional.of(sessionObject.get("X-Uid").asString) else Optional.empty(),
                            if (sessionObject.has("Client-Uid")) Optional.of(sessionObject.get("Client-Uid").asString) else Optional.empty(),
                            Session.AccountType.valueOf(sessionObject.get("Account-Type").asString)
                        )
                    }

                    val environment = jsonObject2.getAsJsonObject("Environment")
                    account.environment = Environment.create(
                        environment.get("Auth-Host").asString,
                        environment.get("Accounts-Host").asString,
                        environment.get("Session-Host").asString,
                        environment.get("Services-Host").asString,
                        "Custom"
                    )
                    accountManager.accounts.add(account)
                }
            }
        }
        if (jsonObject.has("Main-Account"))
            accountManager.mainAccount = jsonObject.get("Main-Account").asInt
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