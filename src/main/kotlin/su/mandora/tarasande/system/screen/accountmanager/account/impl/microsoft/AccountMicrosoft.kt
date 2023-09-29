package su.mandora.tarasande.system.screen.accountmanager.account.impl.microsoft

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.NoticeScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.session.Session
import net.minecraft.text.Text
import net.minecraft.util.Util
import su.mandora.tarasande.feature.screen.accountmanager.subscreen.ScreenBetterAzureApps
import su.mandora.tarasande.gson
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.accountmanager.account.Account
import su.mandora.tarasande.system.screen.accountmanager.account.api.AccountInfo
import su.mandora.tarasande.system.screen.accountmanager.account.api.ExtraInfo
import su.mandora.tarasande.system.screen.accountmanager.account.api.TextFieldInfo
import su.mandora.tarasande.system.screen.accountmanager.azureapp.AzureAppPreset
import su.mandora.tarasande.system.screen.accountmanager.azureapp.ManagerAzureApp
import su.mandora.tarasande.util.extension.javaruntime.Thread
import su.mandora.tarasande.util.extension.kotlinruntime.parseUUID
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import java.nio.charset.StandardCharsets
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.regex.Pattern

// OAuth
private const val OAUTH_AUTHORIZE_URL = "https://login.live.com/oauth20_authorize.srf"
private const val OAUTH_TOKEN_URL = "https://login.live.com/oauth20_token.srf"

// XBox Live
private const val XBOX_AUTHENTICATE_URL = "https://user.auth.xboxlive.com/user/authenticate"
private const val XBOX_AUTHORIZE_URL = "https://xsts.auth.xboxlive.com/xsts/authorize"

// Java Edition
private const val MINECRAFT_LOGIN_URL = "https://api.minecraftservices.com/authentication/login_with_xbox"
private const val MINECRAFT_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile"

@AccountInfo("Microsoft")
open class AccountMicrosoft : Account() {

    @TextFieldInfo("E-Mail", false)
    var email = ""

    @TextFieldInfo("Password", true)
    var password = ""

    protected var msAuthProfile: MSAuthProfile? = null
    private var redirectUri: String? = null

    private var code: String? = null

    var azureApp: AzureAppPreset = ManagerAzureApp.list.first()

    private fun setupHttpServer(code: (String?) -> Unit): ServerSocket {
        fun randomPort() = ThreadLocalRandom.current().nextInt(Short.MAX_VALUE.toInt() * 2 /* unsigned */)
        return try {
            val serverSocket = ServerSocket(randomPort())
            if (!serverSocket.isBound)
                error("Not bound")
            val t = Thread("Microsoft login http server") {
                try {
                    val socket = serverSocket.accept()
                    Thread.sleep(100L) // some browsers are slow for some reason
                    val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
                    val content = StringBuilder()
                    while (bufferedReader.ready()) {
                        val next = bufferedReader.read().toChar()
                        if (next == '\n') break
                        content.append(next)
                    }
                    val path = content.split("\n").first().split(" ")[1]
                    try {
                        code(path.split("code=")[1].split(" ")[0].split("&")[0])
                        socket.getOutputStream().write("""HTTP/2 200 OK
content-type: text/plain

You can close this page now.""".toByteArray())
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        code(null)
                        var errorDescription = path.split("&").map { it.split("=") }.firstOrNull { it[0].equals("error_description", true) }?.get(1) ?: "Couldn't parse error description"
                        errorDescription = URLDecoder.decode(errorDescription, StandardCharsets.UTF_8)
                        socket.getOutputStream().write("""HTTP/2 200 OK
content-type: text/plain

It seems that you have cancelled the operation.

ERROR:
$errorDescription""".toByteArray())
                    }
                    socket.close()
                    serverSocket.close()
                } catch (t: Throwable) {
                    t.printStackTrace()
                    serverSocket.close()
                }
            }
            t.start()
            serverSocket
        } catch (t: Throwable) {
            setupHttpServer(code)
        }
    }

    override fun logIn() {
        if (msAuthProfile != null) {
            val msAuthProfile = msAuthProfile!!
            if (
            //@formatter:off
                msAuthProfile.xboxLiveAuth          .notAfter.time < System.currentTimeMillis() ||
                msAuthProfile.xboxLiveSecurityTokens.notAfter.time < System.currentTimeMillis()
            //@formatter:on
            ) {
                this.msAuthProfile = msAuthProfile.renew() // use refresh token to update the account
            }
        } else {
            msAuthProfile = if (code != null) {
                buildFromCode(code!!)
            } else if (email.isNotEmpty() && password.isNotEmpty()) { // We are not going to safe these
                buildFromCredentials(email, password)
            } else
                error("No data was supplied")
        }
        YggdrasilAuthenticationService(mc.networkProxy, environment).also {
            yggdrasilAuthenticationService = it
            minecraftSessionService = it.createMinecraftSessionService()
        }
        if (msAuthProfile != null) {
            session = msAuthProfile?.asSession()!!
        } else {
            error("Auth profile is invalid")
        }
    }

    private fun buildFromCredentials(email: String, password: String): MSAuthProfile {
        val getResponse = get("$OAUTH_AUTHORIZE_URL?" + buildArguments(HashMap<String, String>().also {
            it["client_id"] = azureApp.clientId
            it["redirect_uri"] = "https://login.live.com/oauth20_desktop.srf".also { redirectUri = it }
            it["response_type"] = "code"
            it["scope"] = azureApp.scope
        }), 60 * 1000, HashMap())

        val content = getResponse.first
        val cookie = getResponse.second

        val urlPost = content
            .substring(content.indexOf("urlPost:"))
            .let { it.substring(it.indexOf("'") + 1) }
            .let { it.substring(0, it.indexOf("'")) }
        val sFTTag = content
            .substring(content.indexOf("sFTTag:"))
            .let { it.substring(it.indexOf("value=\"")) }
            .let { it.substring(it.indexOf("\"") + 1) }
            .let { it.substring(0, it.indexOf("\"")) }

        val postResponse = post(urlPost, 60 * 1000, HashMap<String, String>().also {
            it["login"] = email
            it["loginfmt"] = email
            it["passwd"] = password
            it["PPFT"] = sFTTag
        }, cookie)

        val matcher = Pattern.compile("[?|&]code=([\\w.-]+)").matcher(URLDecoder.decode(postResponse.second, StandardCharsets.UTF_8.name()))
        matcher.find()
        return buildFromCode(matcher.group(1))
    }

    private fun buildFromCode(code: String): MSAuthProfile {
        val oAuthToken = gson.fromJson(post(OAUTH_TOKEN_URL, 60 * 1000, HashMap<String, String>().also {
            it["client_id"] = this.azureApp.clientId
            it["code"] = code
            it["grant_type"] = "authorization_code"
            it["redirect_uri"] = redirectUri!!
            it["scope"] = this.azureApp.scope
            if (this.azureApp.clientSecret != null) {
                it["client_secret"] = this.azureApp.clientSecret!!
            }
        }).first, JsonObject::class.java)
        return buildFromOAuthToken(oAuthToken)
    }

    protected fun buildFromRefreshToken(refreshToken: String): MSAuthProfile {
        val oAuthToken = gson.fromJson(post(OAUTH_TOKEN_URL, 60 * 1000, HashMap<String, String>().also {
            it["client_id"] = this.azureApp.clientId
            it["refresh_token"] = refreshToken
            it["grant_type"] = "refresh_token"
            it["scope"] = this.azureApp.scope
            if (this.azureApp.clientSecret != null) {
                it["client_secret"] = this.azureApp.clientSecret!!
            }
        }).first, JsonObject::class.java)
        return buildFromOAuthToken(oAuthToken)
    }

    private fun buildFromOAuthToken(oAuthToken: JsonObject): MSAuthProfile {
        val req = JsonObject()
        val reqProps = JsonObject()
        reqProps.addProperty("AuthMethod", "RPS")
        reqProps.addProperty("SiteName", "user.auth.xboxlive.com")
        reqProps.addProperty("RpsTicket", "d=" + oAuthToken["access_token"])
        req.add("Properties", reqProps)
        req.addProperty("RelyingParty", "http://auth.xboxlive.com")
        req.addProperty("TokenType", "JWT")
        val xboxLiveAuth = gson.fromJson(post(XBOX_AUTHENTICATE_URL, 60 * 1000, "application/json", req.toString()).first, JsonObject::class.java)
        return buildFromXboxLive(oAuthToken, xboxLiveAuth)
    }

    private fun buildFromXboxLive(oAuthToken: JsonObject, xboxLiveAuth: JsonObject): MSAuthProfile {
        val req = JsonObject()
        val reqProps = JsonObject()
        val userTokens = JsonArray()
        userTokens.add(xboxLiveAuth["Token"].asString)
        reqProps.add("UserTokens", userTokens)
        reqProps.addProperty("SandboxId", "RETAIL")
        req.add("Properties", reqProps)
        req.addProperty("RelyingParty", "rp://api.minecraftservices.com/")
        req.addProperty("TokenType", "JWT")
        val xboxLiveSecurityTokens = gson.fromJson(post(XBOX_AUTHORIZE_URL, 60 * 1000, "application/json", req.toString()).first, JsonObject::class.java)
        return buildFromXboxLiveSecurityTokens(oAuthToken, xboxLiveAuth, xboxLiveSecurityTokens)
    }

    private fun buildFromXboxLiveSecurityTokens(oAuthToken: JsonObject, xboxLiveAuth: JsonObject, xboxLiveSecurityTokens: JsonObject): MSAuthProfile {
        val req = JsonObject()
        req.addProperty("identityToken", "XBL3.0 x=" + xboxLiveSecurityTokens.getAsJsonObject("DisplayClaims").getAsJsonArray("xui")[0].asJsonObject["uhs"].asString + ";" + xboxLiveSecurityTokens["Token"].asString)
        val minecraftLogin = gson.fromJson(post(MINECRAFT_LOGIN_URL, 60 * 1000, "application/json", req.toString()).first, JsonObject::class.java)
        return buildFromMinecraftLogin(oAuthToken, xboxLiveAuth, xboxLiveSecurityTokens, minecraftLogin)
    }

    private fun buildFromMinecraftLogin(oAuthToken: JsonObject, xboxLiveAuth: JsonObject, xboxLiveSecurityTokens: JsonObject, minecraftLogin: JsonObject): MSAuthProfile {
        val minecraftProfile = gson.fromJson(get(MINECRAFT_PROFILE_URL, 60 * 1000, HashMap<String, String>().also {
            it["Authorization"] = "Bearer " + minecraftLogin["access_token"].asString
        }).first, JsonObject::class.java)
        return gson.let {
            MSAuthProfile(
                it.fromJson(oAuthToken, MSAuthProfile.OAuthToken::class.java),
                it.fromJson(xboxLiveAuth, MSAuthProfile.XboxLiveAuth::class.java),
                it.fromJson(xboxLiveSecurityTokens, MSAuthProfile.XboxLiveSecurityTokens::class.java),
                it.fromJson(minecraftLogin, MSAuthProfile.MinecraftLogin::class.java),
                it.fromJson(minecraftProfile, MSAuthProfile.MinecraftProfile::class.java),
                azureApp
            )
        }
    }

    operator fun get(url: String, timeout: Int, headers: HashMap<String, String>): Pair<String /* Content */, String /* Cookies */> {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        headers.forEach(urlConnection::setRequestProperty)
        urlConnection.readTimeout = timeout
        urlConnection.connectTimeout = timeout
        urlConnection.requestMethod = "GET"
        urlConnection.doInput = true
        urlConnection.connect()
        return urlConnection.inputStream.readAllBytes().decodeToString() to urlConnection.getHeaderField("set-cookie")
    }

    private fun buildArguments(arguments: HashMap<String, String>): String {
        val stringBuilder = StringBuilder()
        arguments.forEach {
            stringBuilder.append(URLEncoder.encode(it.key, StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(it.value, StandardCharsets.UTF_8)).append("&")
        }
        return stringBuilder.toString()
    }

    fun post(url: String, timeout: Int, arguments: HashMap<String, String>, cookie: String? = null): Pair<String, String> {
        val argumentsStr = buildArguments(arguments)
        return post(url, timeout, "application/x-www-form-urlencoded", argumentsStr.substring(0, argumentsStr.length - 1), cookie)
    }

    fun post(url: String, timeout: Int, contentType: String, input: String, cookie: String? = null): Pair<String, String> {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        urlConnection.readTimeout = timeout
        urlConnection.connectTimeout = timeout
        urlConnection.requestMethod = "POST"
        urlConnection.doOutput = true
        urlConnection.doInput = true
        urlConnection.setRequestProperty("Content-Type", contentType)
        urlConnection.setRequestProperty("Accept", contentType)
        if (cookie != null)
            urlConnection.setRequestProperty("Cookie", cookie)
        urlConnection.connect()
        urlConnection.outputStream.write(input.toByteArray(StandardCharsets.UTF_8))
        urlConnection.outputStream.flush()
        return urlConnection.inputStream.readAllBytes().decodeToString() to urlConnection.url.toString()
    }

    @Suppress("unused")
    @ExtraInfo("Azure Apps")
    val azureAppsExtra: (Screen, Runnable) -> Unit = { screen, _ ->
        mc.setScreen(ScreenBetterAzureApps(screen, azureApp) { newAzureApp ->
            azureApp = newAzureApp
        })
    }

    @Suppress("unused")
    @ExtraInfo("Web Login")
    val webLogin: (Screen, Runnable) -> Unit = login@{ screen, close ->
        fun abort() = RenderSystem.recordRenderCall { mc.setScreen(screen) }

        val serverSocket = setupHttpServer(code = {
            code = it
            if (code != null) {
                close.run()
            } else {
                abort()
            }
        })

        RenderSystem.recordRenderCall {
            mc.setScreen(NoticeScreen(
                ::abort,
                Text.of("Microsoft Login"),
                Text.of("Your webbrowser should've opened.\nPlease authorize yourself!\nClosing this screen will cancel the process!"),
                Text.of("Cancel"),
                false
            ))
        }
        redirectUri = azureApp.redirectUri + serverSocket.localPort
        Util.getOperatingSystem().open(URI(OAUTH_AUTHORIZE_URL + "?" +
                "redirect_uri=" + redirectUri + "&" +
                "scope=" + URLEncoder.encode(this.azureApp.scope, StandardCharsets.UTF_8) + "&" +
                "response_type=code&" +
                "client_id=" + this.azureApp.clientId
        ))
    }

    override fun getDisplayName() = if (session != null) session?.username!! else if (email.isNotEmpty()) email else "Unnamed Microsoft-account"

    override fun save(): JsonArray? {
        if (msAuthProfile == null)
            return null

        return JsonArray().apply { add(gson.toJsonTree(msAuthProfile)) }
    }

    override fun load(jsonArray: JsonArray): Account {
        return AccountMicrosoft().also { if (!jsonArray.isEmpty) it.msAuthProfile = gson.fromJson(jsonArray[0], MSAuthProfile::class.java) }
    }

    override fun create(credentials: List<String>) {
        email = credentials[0]
        password = credentials[1]
    }

    data class MSAuthProfile(val oAuthToken: OAuthToken, val xboxLiveAuth: XboxLiveAuth, val xboxLiveSecurityTokens: XboxLiveSecurityTokens, val minecraftLogin: MinecraftLogin, val minecraftProfile: MinecraftProfile, val azureApp: AzureAppPreset) {

        fun asSession() = Session(
            minecraftProfile.name,
            parseUUID(minecraftProfile.id),
            minecraftLogin.accessToken,
            Optional.of(xboxLiveAuth.token),
            Optional.of(azureApp.clientId), // I hate the jvm, I hate the bytecode, I hate the language, I hate me, I hate everything!
            Session.AccountType.MSA
        )

        fun renew(): MSAuthProfile { // I have no clue why I have to do this, but it crashes because "this" is null otherwise ._.
            val microsoft = AccountMicrosoft()
            microsoft.azureApp = azureApp
            return microsoft.buildFromRefreshToken(oAuthToken.refreshToken)
        }

        data class DisplayClaim(
            @Suppress("ArrayInDataClass")
            @SerializedName("xui")
            val xui: Array<Xui>
        )

        data class Xui(
            @SerializedName("uhs")
            val uhs: String
        )

        data class OAuthToken(
            @SerializedName("token_type")
            val tokenType: String,
            @SerializedName("expires_in")
            val expiresIn: Int,
            @SerializedName("scope")
            val scope: String,
            @SerializedName("access_token")
            val accessToken: String,
            @SerializedName("refresh_token")
            val refreshToken: String,
            @SerializedName("authentication_token")
            val authenticationToken: String,
            @SerializedName("user_id")
            val userId: String
        )

        data class XboxLiveAuth(
            @SerializedName("IssueInstant")
            val issueInstant: Timestamp,
            @SerializedName("NotAfter")
            val notAfter: Timestamp,
            @SerializedName("Token")
            val token: String,
            @SerializedName("DisplayClaims")
            val displayClaims: DisplayClaim,
        )

        data class XboxLiveSecurityTokens(
            @SerializedName("IssueInstant")
            val issueInstant: Timestamp,
            @SerializedName("NotAfter")
            val notAfter: Timestamp,
            @SerializedName("Token")
            val token: String,
            @SerializedName("DisplayClaims")
            val displayClaims: DisplayClaim
        )

        data class MinecraftLogin(
            @SerializedName("username")
            val username: String,
            @Suppress("ArrayInDataClass")
            @SerializedName("roles")
            val roles: Array<Any>,
            @SerializedName("access_token")
            val accessToken: String,
            @SerializedName("token_type")
            val tokenType: String,
            @SerializedName("expires_in")
            val expiresIn: Int
        )

        data class Skin(
            @SerializedName("id")
            val id: String? = null,
            @SerializedName("state")
            val state: String? = null,
            @SerializedName("url")
            val url: String? = null,
            @SerializedName("variant")
            val variant: String? = null
        )

        data class Cape(
            @SerializedName("id")
            val id: String,
            @SerializedName("state")
            val state: String,
            @SerializedName("url")
            val url: String,
            @SerializedName("alias")
            val alias: String
        )

        data class MinecraftProfile(
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String,
            @Suppress("ArrayInDataClass")
            @SerializedName("skins")
            val skins: Array<Skin>,
            @Suppress("ArrayInDataClass")
            @SerializedName("capes")
            val capes: Array<Cape>
        )
    }

}