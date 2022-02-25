package su.mandora.tarasande.file

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.authlib.Environment
import net.minecraft.client.util.Session
import org.apache.commons.codec.binary.Hex
import oshi.SystemInfo
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.file.File
import su.mandora.tarasande.base.screen.accountmanager.account.AccountInfo
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class FileAccounts : File("Accounts") {

    private val fallbackKey = "y1dHz81YFjuCLWjrpGIirF6nauvYiGJT"

    private var encryptCipher: Cipher? = null
    private var decryptCipher: Cipher? = null

    init {
        var hwid = SystemInfo().hardware.computerSystem.hardwareUUID
        if(hwid.equals("unknown")) {
            // can't get hwid, use pre-specified one (worst security ever)
            hwid = fallbackKey
        }
        var bytes = Base64.getEncoder().encode(String(Hex.encodeHex(hwid.toByteArray())).toByteArray())
        when {
            bytes.size > 32 -> bytes = Arrays.copyOfRange(bytes, 0, 32)
            bytes.size > 24 -> bytes = Arrays.copyOfRange(bytes, 0, 24)
            bytes.size > 16 -> bytes = Arrays.copyOfRange(bytes, 0, 16)
        }
        val keySpec = SecretKeySpec(bytes, 0, bytes.size, "AES")
        encryptCipher = Cipher.getInstance("AES")
        encryptCipher?.init(Cipher.ENCRYPT_MODE, keySpec)
        decryptCipher = Cipher.getInstance("AES")
        decryptCipher?.init(Cipher.DECRYPT_MODE, keySpec)
    }

    override fun save(): JsonElement {
        val jsonObject = JsonObject()
        val jsonArray = JsonArray()
        for (account in TarasandeMain.get().screens?.betterScreenAccountManager?.accounts!!) {
            val accountObject = JsonObject()
            accountObject.addProperty("Type", account.javaClass.getAnnotation(AccountInfo::class.java).name)
            accountObject.add("Account", account.save())
            if (account.session != null) {
                val sessionObject = JsonObject()
                sessionObject.addProperty("Username", account.session?.username)
                sessionObject.addProperty("UUID", account.session?.uuid)
                sessionObject.addProperty("Access-Token", account.session?.accessToken)
                if (account.session?.xuid?.isPresent!!)
                    sessionObject.addProperty("X-Uid", account.session?.xuid?.get())
                if (account.session?.clientId?.isPresent!!)
                    sessionObject.addProperty("Client-Uid", account.session?.clientId?.get())
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
        jsonObject.addProperty("Main-Account", TarasandeMain.get().screens?.betterScreenAccountManager?.mainAccount)
        return jsonObject
    }

    override fun load(jsonElement: JsonElement) {
        val jsonObject: JsonObject = jsonElement as JsonObject
        for (jsonElement2 in jsonObject.getAsJsonArray("Accounts")) {
            for (accountClass in TarasandeMain.get().screens?.betterScreenAccountManager?.managerAccount?.list!!) {
                val jsonObject2 = jsonElement2 as JsonObject
                if (accountClass.getAnnotation(AccountInfo::class.java).name == jsonObject2.get("Type").asString) {
                    val account = accountClass.newInstance().load(jsonObject2.get("Account").asJsonArray)

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
                    account.environment = Environment.create(environment.get("Auth-Host").asString, environment.get("Accounts-Host").asString, environment.get("Session-Host").asString, environment.get("Services-Host").asString, "Custom")
                    TarasandeMain.get().screens?.betterScreenAccountManager?.accounts?.add(account)
                }
            }
        }
        TarasandeMain.get().screens?.betterScreenAccountManager?.mainAccount = jsonObject.get("Main-Account").asInt
    }

    override fun encrypt(input: String) = String(Base64.getEncoder().encode(encryptCipher?.doFinal(input.toByteArray())))

    override fun decrypt(input: String) = try {
        String(decryptCipher?.doFinal(Base64.getDecoder().decode(input.toByteArray()))!!)
    } catch (e: BadPaddingException) {
        e.printStackTrace()
        null
    }
}