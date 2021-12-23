package su.mandora.tarasande.file

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.authlib.Environment
import oshi.SystemInfo
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.file.File
import su.mandora.tarasande.base.screen.accountmanager.account.Account
import su.mandora.tarasande.base.screen.accountmanager.account.AccountInfo
import java.security.MessageDigest
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class FileAccounts : File("Accounts") {
	private var encryptCipher: Cipher? = null
	private var decryptCipher: Cipher? = null

	init {
		val keySpec = SecretKeySpec(Arrays.copyOf(MessageDigest.getInstance("SHA-256").digest(Base64.getEncoder().encode(SystemInfo().hardware.computerSystem.hardwareUUID.toString().toByteArray())), 32), "AES")
		encryptCipher = Cipher.getInstance("AES")
		encryptCipher?.init(Cipher.ENCRYPT_MODE, keySpec)
		decryptCipher = Cipher.getInstance("AES")
		decryptCipher?.init(Cipher.DECRYPT_MODE, keySpec)
	}

	override fun save(): JsonElement {
		val jsonObject = JsonObject()
		val jsonArray = JsonArray()
		for (account in TarasandeMain.get().screens?.betterScreenAccountManager?.accounts!!) {
			val jsonObject2 = JsonObject()
			jsonObject2.addProperty("Type", account.javaClass.getAnnotation(AccountInfo::class.java).name)
			jsonObject2.add("Account", account.save())
			val jsonObject3 = JsonObject()
			jsonObject3.addProperty("Auth-Host", account.environment?.authHost)
			jsonObject3.addProperty("Accounts-Host", account.environment?.accountsHost)
			jsonObject3.addProperty("Session-Host", account.environment?.sessionHost)
			jsonObject3.addProperty("Services-Host", account.environment?.servicesHost)
			jsonObject2.add("Environment", jsonObject3)
			jsonArray.add(jsonObject2)
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
					val account = accountClass.getDeclaredMethod("load", JsonElement::class.java).invoke(accountClass.newInstance(), jsonObject2.get("Account")) as Account
					val jsonObject3 = jsonObject2.getAsJsonObject("Environment")
					account.environment = Environment.create(jsonObject3.get("Auth-Host").asString, jsonObject3.get("Accounts-Host").asString, jsonObject3.get("Session-Host").asString, jsonObject3.get("Services-Host").asString, "Custom")
					TarasandeMain.get().screens?.betterScreenAccountManager?.accounts?.add(account)
				}
			}
		}
		TarasandeMain.get().screens?.betterScreenAccountManager?.mainAccount = jsonObject.get("Main-Account").asInt
	}

	override fun encrypt(input: String) = String(Base64.getEncoder().encode(encryptCipher?.doFinal(input.toByteArray())))

	override fun decrypt(input: String) = try {
		String(decryptCipher!!.doFinal(Base64.getDecoder().decode(input.toByteArray())))
	} catch(ignored: BadPaddingException) {
		null
	}
}