package de.florianmichael.tarasande_protocol_hack.xbox

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import com.nukkitx.protocol.bedrock.util.EncryptionUtils
import de.florianmichael.viabedrock.api.auth.AuthUtils
import net.minecraft.client.util.Session
import java.nio.charset.StandardCharsets
import java.security.KeyPair
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

//based off https://github.com/Sandertv/gophertunnel/tree/master/minecraft/auth
// TODO: Implement https://github.com/wagyourtail/WagYourLauncher/tree/main/src/main/java/xyz/wagyourtail/launcher/auth
class XboxLiveSession : Session {
    private val accessToken: String
    private val isCracked: Boolean
    private var publicKey: ECPublicKey? = null
    private var privateKey: ECPrivateKey? = null
    var keyPair: KeyPair? = null
        private set
    var bedrockXuid: String? = null
        private set
    private var identity: UUID? = null
    private var displayName: String? = null
    var chainData: String? = null
        private set

    constructor(username: String, identity: UUID) : super(
        username,
        identity.toString(),
        "0",
        Optional.empty<String>(),
        Optional.empty<String>(),
        AccountType.MOJANG
    ) {
        this.accessToken = "0"
        displayName = username
        this.identity = UUID.nameUUIDFromBytes("OfflinePlayer:$username".toByteArray(StandardCharsets.UTF_8))
        bedrockXuid = identity.leastSignificantBits.toString()
        isCracked = true
        val ecdsa256KeyPair = EncryptionUtils.createKeyPair()
        publicKey = ecdsa256KeyPair.public as ECPublicKey
        privateKey = ecdsa256KeyPair.private as ECPrivateKey
        keyPair = ecdsa256KeyPair
        displayName = username
        try {
            chainData = AuthUtils.getOfflineChainData(username, privateKey, publicKey)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private constructor(accessToken: String) : super(
        "",
        "",
        accessToken,
        Optional.empty<String>(),
        Optional.empty<String>(),
        AccountType.MOJANG
    ) {
        isCracked = false
        this.accessToken = accessToken
    }

    private val onlineChainData: String?
        get() {
            if (chainData != null) {
                return chainData
            }
            val gson = Gson()
            val ecdsa256KeyPair = AuthUtils.createKeyPair() //for xbox live, xbox live requests use, ES256, ECDSA256
            publicKey = ecdsa256KeyPair.public as ECPublicKey
            privateKey = ecdsa256KeyPair.private as ECPrivateKey
            val xboxLive = XboxLive(this.accessToken)
            val userToken = xboxLive.getUserToken(publicKey!!, privateKey!!)
            val deviceToken = xboxLive.getDeviceToken(publicKey!!, privateKey!!)
            val titleToken = xboxLive.getTitleToken(publicKey!!, privateKey!!, deviceToken)
            val xsts = xboxLive.getXstsToken(userToken, deviceToken, titleToken, publicKey!!, privateKey!!)
            val ecdsa384KeyPair = EncryptionUtils.createKeyPair() //use ES384, ECDSA384
            publicKey = ecdsa384KeyPair.public as ECPublicKey
            privateKey = ecdsa384KeyPair.private as ECPrivateKey
            keyPair = ecdsa384KeyPair

            /*
		 * So we get a "chain"(json array with info(that has 2 objects)) from minecraft.net using our xsts token
		 * from there we have to add our own chain at the beginning of the chain(json array that minecraft.net sent us),
		 * When is all said and done, we have 3 chains(they are jwt objects, header.payload.signature)
		 * which we send to the server to check
		 */
            val chainData = xboxLive.requestMinecraftChain(xsts, publicKey!!)
            val chainDataObject = gson.fromJson(chainData, JsonObject::class.java)
            val minecraftNetChain = chainDataObject["chain"].asJsonArray
            var firstChainHeader = minecraftNetChain[0].asString
            firstChainHeader = firstChainHeader.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[0] //get the jwt header(base64)
            firstChainHeader = String(Base64.getDecoder().decode(firstChainHeader.toByteArray())) //decode the jwt base64 header
            val firstKeyx5u = gson.fromJson(firstChainHeader, JsonObject::class.java).asJsonObject["x5u"].asString
            val newFirstChain = JsonObject()
            newFirstChain.addProperty("certificateAuthority", true)
            newFirstChain.addProperty("exp", Instant.now().epochSecond + TimeUnit.HOURS.toSeconds(6))
            newFirstChain.addProperty("identityPublicKey", firstKeyx5u)
            newFirstChain.addProperty("nbf", Instant.now().epochSecond - TimeUnit.HOURS.toSeconds(6))
            run {
                val publicKeyBase64 = Base64.getEncoder().encodeToString(this.publicKey!!.encoded)
                val jwtHeader = JsonObject()
                jwtHeader.addProperty("alg", "ES384")
                jwtHeader.addProperty("x5u", publicKeyBase64)
                val header =
                    Base64.getUrlEncoder().withoutPadding().encodeToString(gson.toJson(jwtHeader).toByteArray())
                val payload =
                    Base64.getUrlEncoder().withoutPadding().encodeToString(gson.toJson(newFirstChain).toByteArray())
                val dataToSign = "$header.$payload".toByteArray()
                val signatureString = this.signBytes(dataToSign)
                val jwt = "$header.$payload.$signatureString"
                chainDataObject.add(
                    "chain",
                    this.addChainToBeginning(jwt, minecraftNetChain)
                ) //replace the chain with our new chain
            }
            run {

                // We are now going to get some data from a chain minecraft sent us(the last chain)
                val lastChain = minecraftNetChain[minecraftNetChain.size() - 1].asString
                var lastChainPayload = lastChain.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1] //get the middle(payload) jwt thing
                lastChainPayload = String(Base64.getDecoder().decode(lastChainPayload.toByteArray())) //decode the base64
                val payloadObject = gson.fromJson(lastChainPayload, JsonObject::class.java)
                val extraData = payloadObject["extraData"].asJsonObject
                this.bedrockXuid = extraData["XUID"].asString
                this.identity = UUID.fromString(extraData["identity"].asString)
                this.displayName = extraData["displayName"].asString
            }
            this.chainData = gson.toJson(chainDataObject)
            return this.chainData
        }

    fun signBytes(dataToSign: ByteArray?): String {
        return AuthUtils.signBytes(privateKey, dataToSign)
    }

    //thanks gson for not adding a add at index method :pensive:
    private fun addChainToBeginning(chain: String, chainArray: JsonArray): JsonArray {
        val newArray = JsonArray()
        newArray.add(chain)
        for (jsonElement in chainArray) {
            newArray.add(jsonElement)
        }
        return newArray
    }

    override fun getUsername(): String {
        return displayName!!
    }

    override fun getProfile(): GameProfile {
        return GameProfile(identity, displayName)
    }

    override fun getAccessToken(): String {
        return accessToken
    }

    override fun getUuid(): String {
        return identity.toString()
    }

    override fun getSessionId(): String {
        return "token:" + this.accessToken + ":" + identity
    }

    companion object {
        fun create(accessToken: String): XboxLiveSession {
            val session = XboxLiveSession(accessToken)
            session.onlineChainData
            return session
        }
    }
}