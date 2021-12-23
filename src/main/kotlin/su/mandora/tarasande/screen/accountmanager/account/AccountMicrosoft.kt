/**
 * Based on https://github.com/Ratsiiel/minecraft-auth-library
 */
package su.mandora.tarasande.screen.accountmanager.account

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.authlib.exceptions.AuthenticationException
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.minecraft.client.util.Session
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.accountmanager.account.Account
import su.mandora.tarasande.base.screen.accountmanager.account.AccountInfo
import su.mandora.tarasande.base.screen.accountmanager.account.TextFieldInfo
import java.io.*
import java.net.*
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors

@AccountInfo("Microsoft", true)
class AccountMicrosoft(
    @TextFieldInfo("E-Mail", false)
    val email: String,
    @TextFieldInfo("Password", true)
    val password: String,
) : Account() {
    private val clientId = "00000000402b5328"
    private val scopeUrl = "service::user.auth.xboxlive.com::MBI_SSL"

    private var loginUrl: String? = null
    private var loginCookie: String? = null
    private var loginPPFT: String? = null

    constructor() : this("", "")

    var service: MinecraftSessionService? = null

    override fun logIn() {
        val microsoftToken = generateTokenPair(generateLoginCode(email, password))
        val xboxLiveToken = generateXboxTokenPair(microsoftToken!!)
        val xboxToken = generateXboxTokenPair(xboxLiveToken!!)

        val url = URL(environment?.servicesHost + "/authentication/login_with_xbox")
        val urlConnection = url.openConnection()
        val httpURLConnection = urlConnection as HttpURLConnection
        httpURLConnection.requestMethod = "POST"
        httpURLConnection.doOutput = true
        val request = JsonObject()
        request.add("identityToken", JsonPrimitive("XBL3.0 x=" + xboxToken?.uhs + ";" + xboxToken?.token))
        val requestBody = request.toString()
        httpURLConnection.setFixedLengthStreamingMode(requestBody.length)
        httpURLConnection.setRequestProperty("Content-Type", "application/json")
        httpURLConnection.setRequestProperty("Host", URI(environment?.servicesHost!!).path)
        httpURLConnection.connect()
        httpURLConnection.outputStream.use { outputStream -> outputStream.write(requestBody.toByteArray(StandardCharsets.US_ASCII)) }
        val jsonObject: JsonObject = parseResponseData(httpURLConnection)

        val minecraftProfile = checkOwnership(jsonObject.get("access_token").asString)

        service = YggdrasilAuthenticationService(Proxy.NO_PROXY, "", environment).createMinecraftSessionService()
        session = Session(minecraftProfile.username, minecraftProfile.uuid.toString(), jsonObject.get("access_token").asString, Optional.of(xboxLiveToken.token), Optional.of(clientId), Session.AccountType.MSA)
    }

    private fun checkOwnership(minecraftToken: String): MinecraftProfile {
        return try {
            val url = URL("https://api.minecraftservices.com/minecraft/profile")
            val urlConnection = url.openConnection()
            val httpURLConnection = urlConnection as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.setRequestProperty("Authorization", "Bearer $minecraftToken")
            httpURLConnection.setRequestProperty("Host", URI(environment?.servicesHost!!).path)
            httpURLConnection.connect()
            val jsonObject = parseResponseData(httpURLConnection)
            val uuid: UUID = generateUUID(jsonObject.get("id").asString)
            val name: String = jsonObject.get("name").asString
            MinecraftProfile(uuid, name)
        } catch (exception: IOException) {
            throw AuthenticationException(String.format("Authentication error. Request could not be made! Cause: '%s'", exception.message))
        }
    }

    private fun generateUUID(trimmedUUID: String): UUID {
        val builder = StringBuilder(trimmedUUID.trim { it <= ' ' })
        builder.insert(20, "-")
        builder.insert(16, "-")
        builder.insert(12, "-")
        builder.insert(8, "-")
        return UUID.fromString(builder.toString())
    }

    private fun generateLoginCode(email: String, password: String): String {
        try {
            val url = URL("https://login.live.com/oauth20_authorize.srf?redirect_uri=https://login.live.com/oauth20_desktop.srf&scope=$scopeUrl&display=touch&response_type=code&locale=en&client_id=$clientId")
            val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream: InputStream = if (httpURLConnection.responseCode == 200) httpURLConnection.inputStream else httpURLConnection.errorStream
            loginCookie = httpURLConnection.getHeaderField("set-cookie")
            val responseData = BufferedReader(InputStreamReader(inputStream)).lines().collect(Collectors.joining())
            var bodyMatcher: Matcher = Pattern.compile("sFTTag:[ ]?'.*value=\"(.*)\"/>'").matcher(responseData)
            loginPPFT = if (bodyMatcher.find()) {
                bodyMatcher.group(1)
            } else {
                throw AuthenticationException("Authentication error. Could not find 'LOGIN-PFTT' tag from response!")
            }
            bodyMatcher = Pattern.compile("urlPost:[ ]?'(.+?(?='))").matcher(responseData)
            loginUrl = if (bodyMatcher.find()) {
                bodyMatcher.group(1)
            } else {
                throw AuthenticationException("Authentication error. Could not find 'LOGIN-URL' tag from response!")
            }
            if (loginCookie == null || loginPPFT == null || loginUrl == null) throw AuthenticationException("Authentication error. Error in authentication process!")
        } catch (exception: IOException) {
            throw AuthenticationException(String.format("Authentication error. Request could not be made! Cause: '%s'", exception.message))
        }
        return sendCodeData(email, password)
    }

    private fun sendCodeData(email: String, password: String): String {
        val authToken: String
        val requestData: MutableMap<String, String> = HashMap()
        requestData["login"] = email
        requestData["loginfmt"] = email
        requestData["passwd"] = password
        requestData["PPFT"] = loginPPFT!!
        val postData = encodeURL(requestData)
        authToken = try {
            val data: ByteArray = postData.toByteArray(StandardCharsets.UTF_8)
            val connection: HttpURLConnection = URL(loginUrl).openConnection() as HttpURLConnection
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
            connection.setRequestProperty("Content-Length", data.size.toString())
            connection.setRequestProperty("Cookie", loginCookie)
            connection.doInput = true
            connection.doOutput = true
            connection.outputStream.use { outputStream -> outputStream.write(data) }
            if (connection.responseCode != 200 || connection.url.toString() == loginUrl) {
                throw AuthenticationException("Authentication error. Username or password is not valid.")
            }
            val pattern: Pattern = Pattern.compile("[?|&]code=([\\w.-]+)")
            val tokenMatcher: Matcher = pattern.matcher(URLDecoder.decode(connection.url.toString(), StandardCharsets.UTF_8.name()))
            if (tokenMatcher.find()) {
                tokenMatcher.group(1)
            } else {
                throw AuthenticationException("Authentication error. Could not handle data from response.")
            }
        } catch (exception: IOException) {
            throw AuthenticationException(String.format("Authentication error. Request could not be made! Cause: '%s'", exception.message))
        }
        loginUrl = null
        loginCookie = null
        loginPPFT = null
        return authToken
    }

    private fun sendXboxRequest(httpURLConnection: HttpURLConnection, request: JsonObject, properties: JsonObject) {
        request.add("Properties", properties)
        val requestBody: String = request.toString()
        httpURLConnection.setFixedLengthStreamingMode(requestBody.length)
        httpURLConnection.setRequestProperty("Content-Type", "application/json")
        httpURLConnection.setRequestProperty("Accept", "application/json")
        httpURLConnection.connect()
        httpURLConnection.outputStream.use { outputStream -> outputStream.write(requestBody.toByteArray(StandardCharsets.US_ASCII)) }
    }

    private fun generateTokenPair(authToken: String): MicrosoftToken? {
        return try {
            val arguments: MutableMap<String, String> = HashMap()
            arguments["client_id"] = clientId
            arguments["code"] = authToken
            arguments["grant_type"] = "authorization_code"
            arguments["redirect_uri"] = "https://login.live.com/oauth20_desktop.srf"
            arguments["scope"] = scopeUrl
            val argumentBuilder = StringJoiner("&")
            for ((key, value) in arguments) {
                argumentBuilder.add(encodeURL(key) + "=" + encodeURL(value))
            }
            val data: ByteArray = argumentBuilder.toString().toByteArray(StandardCharsets.UTF_8)
            val url = URL("https://login.live.com/oauth20_token.srf")
            val urlConnection: URLConnection = url.openConnection()
            val httpURLConnection: HttpURLConnection = urlConnection as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true
            httpURLConnection.setFixedLengthStreamingMode(data.size)
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            httpURLConnection.connect()
            httpURLConnection.outputStream.use { outputStream -> outputStream.write(data) }
            val jsonObject: JsonObject = parseResponseData(httpURLConnection)
            MicrosoftToken(jsonObject.get("access_token").asString, jsonObject.get("refresh_token").asString)
        } catch (exception: IOException) {
            throw AuthenticationException(String.format("Authentication error. Request could not be made! Cause: '%s'", exception.message))
        }
    }

    private fun generateXboxTokenPair(microsoftToken: MicrosoftToken): XboxLiveToken? {
        return try {
            val url = URL("https://user.auth.xboxlive.com/user/authenticate")
            val urlConnection: URLConnection = url.openConnection()
            val httpURLConnection: HttpURLConnection = urlConnection as HttpURLConnection
            httpURLConnection.doOutput = true
            val request = JsonObject()
            request.add("RelyingParty", JsonPrimitive("http://auth.xboxlive.com"))
            request.add("TokenType", JsonPrimitive("JWT"))
            val properties = JsonObject()
            properties.add("AuthMethod", JsonPrimitive("RPS"))
            properties.add("SiteName", JsonPrimitive("user.auth.xboxlive.com"))
            properties.add("RpsTicket", JsonPrimitive(microsoftToken.token))
            sendXboxRequest(httpURLConnection, request, properties)
            val jsonObject: JsonObject = parseResponseData(httpURLConnection)
            val uhs: String = (jsonObject.get("DisplayClaims").asJsonObject.getAsJsonArray("xui").get(0) as JsonObject).get("uhs").asString
            XboxLiveToken(jsonObject.get("Token").asString, uhs)
        } catch (exception: IOException) {
            throw AuthenticationException(String.format("Authentication error. Request could not be made! Cause: '%s'", exception.message))
        }
    }

    private fun generateXboxTokenPair(xboxLiveToken: XboxLiveToken): XboxToken? {
        return try {
            val url = URL("https://xsts.auth.xboxlive.com/xsts/authorize")
            val urlConnection: URLConnection = url.openConnection()
            val httpURLConnection: HttpURLConnection = urlConnection as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true
            val request = JsonObject()
            request.add("RelyingParty", JsonPrimitive("rp://api.minecraftservices.com/"))
            request.add("TokenType", JsonPrimitive("JWT"))
            val properties = JsonObject()
            properties.add("SandboxId", JsonPrimitive("RETAIL"))
            val userTokens = JsonArray()
            userTokens.add(JsonPrimitive(xboxLiveToken.token))
            properties.add("UserTokens", userTokens)
            sendXboxRequest(httpURLConnection, request, properties)
            if (httpURLConnection.responseCode == 401) {
                throw AuthenticationException("No xbox account was found!")
            }
            val jsonObject: JsonObject = parseResponseData(httpURLConnection)
            val uhs: String = (jsonObject.get("DisplayClaims").asJsonObject.get("xui").asJsonArray.get(0) as JsonObject).get("uhs").asString
            XboxToken(jsonObject.get("Token").asString, uhs)
        } catch (exception: IOException) {
            throw AuthenticationException(String.format("Authentication error. Request could not be made! Cause: '%s'", exception.message))
        }
    }

    private fun parseResponseData(httpURLConnection: HttpURLConnection): JsonObject {
        val bufferedReader: BufferedReader = if (httpURLConnection.responseCode != 200) {
            BufferedReader(InputStreamReader(httpURLConnection.errorStream))
        } else {
            BufferedReader(InputStreamReader(httpURLConnection.inputStream))
        }
        val lines = bufferedReader.lines().collect(Collectors.joining())
        val jsonObject: JsonObject = TarasandeMain.get().gson.fromJson(lines, JsonObject::class.java)!!
        if (jsonObject.has("error")) {
            throw AuthenticationException(jsonObject.get("error").toString() + ": " + jsonObject.get("error_description"))
        }
        return jsonObject
    }

    private fun encodeURL(url: String): String {
        return try {
            URLEncoder.encode(url, "UTF-8")
        } catch (exception: UnsupportedEncodingException) {
            throw UnsupportedOperationException(exception)
        }
    }

    private fun encodeURL(map: MutableMap<String, String>): String {
        val sb = StringBuilder()
        for ((key, value) in map) {
            if (sb.isNotEmpty()) {
                sb.append("&")
            }
            sb.append(String.format("%s=%s", encodeURL(key), encodeURL(value)))
        }
        return sb.toString()
    }

    override fun getDisplayName(): String {
        return if (session != null) session?.username!! else email
    }

    override fun getSessionService(): MinecraftSessionService? = service

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        jsonArray.add(email)
        jsonArray.add(password)
        jsonArray.add(TarasandeMain.get().gson.toJsonTree(session))
        return jsonArray
    }

    override fun load(jsonElement: JsonElement): Account {
        val jsonArray = jsonElement.asJsonArray
        val account = AccountMicrosoft(jsonArray[0].asString, jsonArray[1].asString)
        account.session = TarasandeMain.get().gson.fromJson(jsonArray[2], Session::class.java)
        return account
    }

    override fun create(credentials: List<String>) = AccountMicrosoft(credentials[0], credentials[1])
}

class MicrosoftToken(val token: String, val refreshToken: String)
class XboxLiveToken(val token: String /* xuid */, val uhs: String)
class XboxToken(val token: String, val uhs: String)

class MinecraftProfile(val uuid: UUID, val username: String)