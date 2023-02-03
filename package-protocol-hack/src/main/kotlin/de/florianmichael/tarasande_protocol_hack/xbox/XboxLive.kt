package de.florianmichael.tarasande_protocol_hack.xbox

import com.google.common.primitives.Longs
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import de.florianmichael.viabedrock.api.BedrockProtocols
import de.florianmichael.viabedrock.api.auth.JoseStuff
import net.tarasandedevelopment.tarasande.gson
import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.math.BigInteger
import java.net.URL
import java.security.Signature
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.time.Instant
import java.util.*
import javax.net.ssl.HttpsURLConnection

/*
 * This note refers to all auth/JWT related classes, I know there are JWT parsing dependencies(already built into the protocol
 * dependence) but I want to to be "up front", meaning I/we see everything that is going on, meaning we can see exactly how
 * the header, payload and signature are formed, rather then using some library that make take a couple minutes to understand(how
 * it works), I also do know, at some points it would be easier to use the JWT dependence but my stance above still applies,
 * maybe ill make my own simple class :shrug:
 */
//based off https://github.com/Sandertv/gophertunnel/tree/master/minecraft/auth
class XboxLive( //go here, log in, and in the redirected url you will have your access token, https://login.live.com/oauth20_authorize.srf?client_id=00000000441cc96b&redirect_uri=https://login.live.com/oauth20_desktop.srf&response_type=token&display=touch&scope=service::user.auth.xboxlive.com::MBI_SSL&locale=en
    //then add -DXboxAccessToken=YOURS to your jvm arguments
    private val accessToken: String
) {
    fun getUserToken(publicKey: ECPublicKey, privateKey: ECPrivateKey): String {
        val jsonObject = JsonObject()
        jsonObject.addProperty("RelyingParty", "http://auth.xboxlive.com")
        jsonObject.addProperty("TokenType", "JWT")
        val properties = JsonObject()
        jsonObject.add("Properties", properties)
        properties.addProperty("AuthMethod", "RPS")
        properties.addProperty("SiteName", "user.auth.xboxlive.com")
        properties.addProperty("RpsTicket", "d=$accessToken")
        val proofKey = JsonObject()
        properties.add("ProofKey", proofKey)
        proofKey.addProperty("crv", "P-256")
        proofKey.addProperty("alg", "ES256")
        proofKey.addProperty("use", "sig")
        proofKey.addProperty("kty", "EC")
        proofKey.addProperty("x", getProofKeyX(publicKey))
        proofKey.addProperty("y", getProofKeyY(publicKey))
        val url = URL(xboxUserAuthURL)
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("x-xbl-contract-version", "1")
        addSignatureHeader(connection, jsonObject, privateKey)
        writeJsonObjectToPost(connection, jsonObject)
        val responce = String(IOUtils.toByteArray(connection.inputStream))
        val responceJsonObject = gson.fromJson(responce, JsonObject::class.java)
        return responceJsonObject["Token"].asString
    }

    fun getDeviceToken(publicKey: ECPublicKey, privateKey: ECPrivateKey): String {
        val jsonObject = JsonObject()
        jsonObject.addProperty("RelyingParty", "http://auth.xboxlive.com")
        jsonObject.addProperty("TokenType", "JWT")
        val properties = JsonObject()
        jsonObject.add("Properties", properties)
        properties.addProperty("AuthMethod", "ProofOfPossession")
        properties.addProperty("DeviceType", "Nintendo")
        properties.addProperty("Id", UUID.randomUUID().toString())
        properties.addProperty("SerialNumber", UUID.randomUUID().toString())
        properties.addProperty("Version", "0.0.0.0")
        val proofKey = JsonObject()
        properties.add("ProofKey", proofKey)
        proofKey.addProperty("crv", "P-256")
        proofKey.addProperty("alg", "ES256")
        proofKey.addProperty("use", "sig")
        proofKey.addProperty("kty", "EC")
        proofKey.addProperty("x", getProofKeyX(publicKey))
        proofKey.addProperty("y", getProofKeyY(publicKey))
        val url = URL(xboxDeviceAuthURL)
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("x-xbl-contract-version", "1")
        addSignatureHeader(connection, jsonObject, privateKey)
        writeJsonObjectToPost(connection, jsonObject)
        val responce = String(IOUtils.toByteArray(connection.inputStream))
        val responceJsonObject = gson.fromJson(responce, JsonObject::class.java)
        return responceJsonObject["Token"].asString
    }

    fun getTitleToken(publicKey: ECPublicKey, privateKey: ECPrivateKey, deviceToken: String?): String {
        val jsonObject = JsonObject()
        jsonObject.addProperty("RelyingParty", "http://auth.xboxlive.com")
        jsonObject.addProperty("TokenType", "JWT")
        val properties = JsonObject()
        jsonObject.add("Properties", properties)
        properties.addProperty("AuthMethod", "RPS")
        properties.addProperty("DeviceToken", deviceToken)
        properties.addProperty("SiteName", "user.auth.xboxlive.com")
        properties.addProperty("RpsTicket", "d=$accessToken")
        val proofKey = JsonObject()
        properties.add("ProofKey", proofKey)
        proofKey.addProperty("crv", "P-256")
        proofKey.addProperty("alg", "ES256")
        proofKey.addProperty("use", "sig")
        proofKey.addProperty("kty", "EC")
        proofKey.addProperty("x", getProofKeyX(publicKey))
        proofKey.addProperty("y", getProofKeyY(publicKey))
        val url = URL(xboxTitleAuthURL)
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("x-xbl-contract-version", "1")
        addSignatureHeader(connection, jsonObject, privateKey)
        writeJsonObjectToPost(connection, jsonObject)
        val responce = String(IOUtils.toByteArray(connection.inputStream))
        val responceJsonObject = gson.fromJson(responce, JsonObject::class.java)
        return responceJsonObject["Token"].asString
    }

    fun getXstsToken(
        userToken: String?,
        deviceToken: String?,
        titleToken: String?,
        publicKey: ECPublicKey,
        privateKey: ECPrivateKey
    ): String {
        val jsonObject = JsonObject()
        jsonObject.addProperty("RelyingParty", "https://multiplayer.minecraft.net/")
        jsonObject.addProperty("TokenType", "JWT")
        val properties = JsonObject()
        jsonObject.add("Properties", properties)
        val userTokens = JsonArray()
        userTokens.add(userToken)
        properties.addProperty("DeviceToken", deviceToken)
        properties.addProperty("TitleToken", titleToken)
        properties.add("UserTokens", userTokens)
        properties.addProperty("SandboxId", "RETAIL")
        val proofKey = JsonObject()
        properties.add("ProofKey", proofKey)
        proofKey.addProperty("crv", "P-256")
        proofKey.addProperty("alg", "ES256")
        proofKey.addProperty("use", "sig")
        proofKey.addProperty("kty", "EC")
        proofKey.addProperty("x", getProofKeyX(publicKey))
        proofKey.addProperty("y", getProofKeyY(publicKey))
        val url = URL(xboxAuthorizeURL)
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        connection.setRequestProperty("x-xbl-contract-version", "1")
        addSignatureHeader(connection, jsonObject, privateKey)
        writeJsonObjectToPost(connection, jsonObject)
        return String(IOUtils.toByteArray(connection.inputStream))
    }

    fun requestMinecraftChain(xsts: String?, publicKey: ECPublicKey): String {
        val xstsObject = gson.fromJson(xsts, JsonObject::class.java)
        val pubKeyData = Base64.getEncoder().encodeToString(publicKey.encoded)
        val jsonObject = JsonObject()
        jsonObject.addProperty("identityPublicKey", pubKeyData)
        val url = URL(minecraftAuthURL)
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty(
            "Authorization",
            "XBL3.0 x=" + xstsObject["DisplayClaims"].asJsonObject.getAsJsonArray("xui").asJsonArray[0].asJsonObject["uhs"].asString + ";" + xstsObject["Token"].asString
        )
        connection.setRequestProperty("User-Agent", "MCPE/UWP")
        connection.setRequestProperty("Client-Version", BedrockProtocols.VERSION_NAME)
        writeJsonObjectToPost(connection, jsonObject)
        return String(IOUtils.toByteArray(connection.inputStream))
    }

    private fun writeJsonObjectToPost(connection: HttpsURLConnection, jsonObject: JsonObject) {
        connection.doOutput = true
        val dataOutputStream = DataOutputStream(connection.outputStream)
        dataOutputStream.writeBytes(Gson().toJson(jsonObject))
        dataOutputStream.flush()
    }

    private fun getProofKeyX(ecPublicKey: ECPublicKey): String {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bigIntegerToByteArray(ecPublicKey.w.affineX))
    }

    private fun getProofKeyY(ecPublicKey: ECPublicKey): String {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bigIntegerToByteArray(ecPublicKey.w.affineY))
    }

    private fun addSignatureHeader(
        httpsURLConnection: HttpsURLConnection,
        postData: JsonObject,
        privateKey: ECPrivateKey
    ) {
        val currentTime = windowsTimestamp()
        val bytesToSign = ByteArrayOutputStream()
        bytesToSign.write(byteArrayOf(0, 0, 0, 1, 0))
        bytesToSign.write(Longs.toByteArray(currentTime))
        bytesToSign.write(byteArrayOf(0))
        bytesToSign.write("POST".toByteArray())
        bytesToSign.write(byteArrayOf(0))
        var query = httpsURLConnection.url.query
        if (query == null) {
            query = ""
        }
        bytesToSign.write((httpsURLConnection.url.path + query).toByteArray())
        bytesToSign.write(byteArrayOf(0))
        var authorization = httpsURLConnection.getRequestProperty("Authorization")
        if (authorization == null) {
            authorization = ""
        }
        bytesToSign.write(authorization.toByteArray())
        bytesToSign.write(byteArrayOf(0))
        bytesToSign.write(Gson().toJson(postData).toByteArray())
        bytesToSign.write(byteArrayOf(0))
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        signature.update(bytesToSign.toByteArray())
        val signatureBytes = JoseStuff.DERToJOSE(signature.sign(), JoseStuff.AlgorithmType.ECDSA256)
        val byteArrayOutputStream = ByteArrayOutputStream()
        byteArrayOutputStream.write(byteArrayOf(0, 0, 0, 1))
        byteArrayOutputStream.write(Longs.toByteArray(currentTime))
        byteArrayOutputStream.write(signatureBytes)
        httpsURLConnection.addRequestProperty(
            "Signature",
            Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
        )
    }

    // windowsTimestamp returns a Windows specific timestamp. It has a certain offset from Unix time which must be accounted for.
    private fun windowsTimestamp(): Long {
        return (Instant.now().epochSecond + 11644473600L) * 10000000L
    }

    companion object {
        private const val xboxUserAuthURL = "https://user.auth.xboxlive.com/user/authenticate"
        private const val xboxAuthorizeURL = "https://xsts.auth.xboxlive.com/xsts/authorize"
        private const val xboxDeviceAuthURL = "https://device.auth.xboxlive.com/device/authenticate"
        private const val xboxTitleAuthURL = "https://title.auth.xboxlive.com/title/authenticate"
        private const val minecraftAuthURL = "https://multiplayer.minecraft.net/authentication"

        //so sometimes getAffineX/Y toByteArray returns 33 or 31(really rare) bytes we are suppose to get 32 bytes, as said in these stackoverflows, they basically say if byte 0 is 0(33 bytes?) we can remove it
        //https://stackoverflow.com/questions/57379134/bouncy-castle-ecc-key-pair-generation-produces-different-sizes-for-the-coordinat
        //https://stackoverflow.com/questions/4407779/biginteger-to-byte
        private fun bigIntegerToByteArray(bigInteger: BigInteger): ByteArray {
            val array = bigInteger.toByteArray()
            if (array[0].toInt() == 0) {
                val newArray = ByteArray(array.size - 1)
                System.arraycopy(array, 1, newArray, 0, newArray.size)
                return newArray
            }
            return array
        }
    }
}