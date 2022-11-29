package net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.microsoft

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.NoticeScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.Session
import net.minecraft.text.Text
import net.minecraft.util.Util
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.Account
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.AccountInfo
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.ExtraInfo
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.azureapp.AzureAppPreset
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.ScreenExtensionSidebarMultiplayerScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.accountmanager.subscreen.ScreenBetterAzureApps
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import java.nio.charset.StandardCharsets
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.ThreadLocalRandom

@AccountInfo("Microsoft")
open class AccountMicrosoft : Account() {
    private val oauthAuthorizeUrl = "https://login.live.com/oauth20_authorize.srf"
    private val oauthTokenUrl = "https://login.live.com/oauth20_token.srf"
    private val xboxAuthenticateUrl = "https://user.auth.xboxlive.com/user/authenticate"
    private val xboxAuthorizeUrl = "https://xsts.auth.xboxlive.com/xsts/authorize"
    private val minecraftLoginUrl = "https://api.minecraftservices.com/authentication/login_with_xbox"
    private val minecraftProfileUrl = "https://api.minecraftservices.com/minecraft/profile"
    private var cancelled = false

    private var service: MinecraftSessionService? = null

    protected var msAuthProfile: MSAuthProfile? = null
    protected var redirectUri: String? = null
    private var code: String? = null

    var azureApp: AzureAppPreset = TarasandeMain.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen::class.java).screenBetterSlotListAccountManager.managerAzureApp.list.first()

    protected fun randomPort(): Int = ThreadLocalRandom.current().nextInt(0, Short.MAX_VALUE.toInt() * 2 /* unsigned */)

    protected open fun setupHttpServer(): ServerSocket {
        return try {
            val serverSocket = ServerSocket(randomPort())
            if (!serverSocket.isBound)
                error("Not bound")
            val t = Thread({
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
                        code = path.split("code=")[1].split(" ")[0].split("&")[0]
                        socket.getOutputStream().write("""HTTP/2 200 OK
content-type: text/plain

You can close this page now.""".toByteArray())
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        cancelled = true
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
            }, "Microsoft login http server")
            t.start()
            serverSocket
        } catch (t: Throwable) {
            setupHttpServer()
        }
    }

    override fun logIn() {
        code = null
        cancelled = false
        if (msAuthProfile != null &&
            (msAuthProfile?.xboxLiveAuth?.notAfter?.time?.compareTo(System.currentTimeMillis())?.let { it < 0 } == true ||
                    msAuthProfile?.xboxLiveSecurityTokens?.notAfter?.time?.compareTo(System.currentTimeMillis())?.let { it < 0 } == true)
        ) {
            msAuthProfile = msAuthProfile?.renew() // use refresh token to update the account
        }

        if (msAuthProfile == null) {
            val serverSocket = setupHttpServer()
            val prevScreen = MinecraftClient.getInstance().currentScreen
            RenderSystem.recordRenderCall {
                MinecraftClient.getInstance().setScreen(NoticeScreen({ cancelled = true }, Text.of("Microsoft Login"), Text.of("Your webbrowser should've opened.\nPlease authorize yourself!\nClosing this screen will cancel the process!"), Text.of("Cancel"), false))
            }
            redirectUri = azureApp.redirectUri + serverSocket.localPort
            Util.getOperatingSystem().open(URI(oauthAuthorizeUrl + "?" +
                    "redirect_uri=" + redirectUri + "&" +
                    "scope=" + URLEncoder.encode(this.azureApp.scope, StandardCharsets.UTF_8) + "&" +
                    "response_type=code&" +
                    "client_id=" + this.azureApp.clientId
            ))
            while (code == null) {
                Thread.sleep(100L)
                if (cancelled) {
                    RenderSystem.recordRenderCall {
                        MinecraftClient.getInstance().setScreen(prevScreen)
                    }
                    error("Cancelled")
                }
            }
            RenderSystem.recordRenderCall {
                MinecraftClient.getInstance().setScreen(prevScreen)
            }
            msAuthProfile = buildFromCode(code!!)
        }

        service = YggdrasilAuthenticationService(Proxy.NO_PROXY, "", environment).createMinecraftSessionService()
        if (msAuthProfile != null) {
            session = msAuthProfile?.asSession()!!
        } else {
            error("WHAT THE FUCK")
        }
    }

    private fun buildFromCode(code: String): MSAuthProfile {
        val str = post(oauthTokenUrl, 60 * 1000, HashMap<String, String>().also {
            it["client_id"] = this.azureApp.clientId.toString()
            it["code"] = code
            it["grant_type"] = "authorization_code"
            it["redirect_uri"] = redirectUri!!
            it["scope"] = this.azureApp.scope
            if (this.azureApp.clientSecret != null) {
                it["client_secret"] = this.azureApp.clientSecret!!
            }
        })
        val oAuthToken = TarasandeMain.get().gson.fromJson(str, JsonObject::class.java)
        return buildFromOAuthToken(oAuthToken)
    }

    protected fun buildFromRefreshToken(refreshToken: String): MSAuthProfile {
        val oAuthToken = TarasandeMain.get().gson.fromJson(post(oauthTokenUrl, 60 * 1000, HashMap<String, String>().also {
            it["client_id"] = this.azureApp.clientId.toString()
            it["refresh_token"] = refreshToken
            it["grant_type"] = "refresh_token"
            it["redirect_uri"] = redirectUri!!
            it["scope"] = this.azureApp.scope
            if (this.azureApp.clientSecret != null) {
                it["client_secret"] = this.azureApp.clientSecret!!
            }
        }), JsonObject::class.java)
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
        val xboxLiveAuth = TarasandeMain.get().gson.fromJson(post(xboxAuthenticateUrl, 60 * 1000, "application/json", req.toString()), JsonObject::class.java)
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
        val xboxLiveSecurityTokens = TarasandeMain.get().gson.fromJson(post(xboxAuthorizeUrl, 60 * 1000, "application/json", req.toString()), JsonObject::class.java)
        return buildFromXboxLiveSecurityTokens(oAuthToken, xboxLiveAuth, xboxLiveSecurityTokens)
    }

    private fun buildFromXboxLiveSecurityTokens(oAuthToken: JsonObject, xboxLiveAuth: JsonObject, xboxLiveSecurityTokens: JsonObject): MSAuthProfile {
        val req = JsonObject()
        req.addProperty("identityToken", "XBL3.0 x=" + xboxLiveSecurityTokens.getAsJsonObject("DisplayClaims").getAsJsonArray("xui")[0].asJsonObject["uhs"].asString + ";" + xboxLiveSecurityTokens["Token"].asString)
        val minecraftLogin = TarasandeMain.get().gson.fromJson(post(minecraftLoginUrl, 60 * 1000, "application/json", req.toString()), JsonObject::class.java)
        return buildFromMinecraftLogin(oAuthToken, xboxLiveAuth, xboxLiveSecurityTokens, minecraftLogin)
    }

    private fun buildFromMinecraftLogin(oAuthToken: JsonObject, xboxLiveAuth: JsonObject, xboxLiveSecurityTokens: JsonObject, minecraftLogin: JsonObject): MSAuthProfile {
        val minecraftProfile = TarasandeMain.get().gson.fromJson(get(minecraftProfileUrl, 60 * 1000, HashMap<String, String>().also {
            it["Authorization"] = "Bearer " + minecraftLogin["access_token"].asString
        }), JsonObject::class.java)
        return TarasandeMain.get().gson.let {
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

    operator fun get(url: String, timeout: Int, headers: HashMap<String, String>): String {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        headers.forEach(urlConnection::setRequestProperty)
        urlConnection.readTimeout = timeout
        urlConnection.connectTimeout = timeout
        urlConnection.requestMethod = "GET"
        urlConnection.connect()
        return urlConnection.inputStream.readAllBytes().decodeToString()
    }

    fun post(url: String, timeout: Int, arguments: HashMap<String, String>): String {
        val argumentsStr = StringBuilder()
        arguments.forEach {
            argumentsStr.append(URLEncoder.encode(it.key, StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(it.value, StandardCharsets.UTF_8)).append("&")
        }
        return post(url, timeout, "application/x-www-form-urlencoded", argumentsStr.substring(0, argumentsStr.length - 1))
    }

    fun post(url: String, timeout: Int, contentType: String, input: String): String {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        urlConnection.readTimeout = timeout
        urlConnection.connectTimeout = timeout
        urlConnection.requestMethod = "POST"
        urlConnection.doOutput = true
        urlConnection.setFixedLengthStreamingMode(input.length)
        urlConnection.setRequestProperty("Content-Type", contentType)
        urlConnection.setRequestProperty("Accept", contentType)
        urlConnection.connect()
        urlConnection.outputStream.write(input.toByteArray(StandardCharsets.UTF_8))
        urlConnection.outputStream.flush()
        return urlConnection.inputStream.readAllBytes().decodeToString()
    }

    @Suppress("unused")
    @ExtraInfo("Azure Apps")
    val azureAppsExtra: (Screen) -> Unit = {
        MinecraftClient.getInstance().setScreen(ScreenBetterAzureApps(it, azureApp) {
            azureApp = it
        })
    }

    override fun getDisplayName(): String {
        return if (session != null) session?.username!! else "Unnamed Microsoft-account"
    }

    override fun getSessionService(): MinecraftSessionService? = service

    override fun save(): JsonArray {
        val jsonArray = JsonArray()
        if (msAuthProfile != null)
            jsonArray.add(TarasandeMain.get().gson.toJsonTree(msAuthProfile))
        return jsonArray
    }

    override fun load(jsonArray: JsonArray): Account {
        return AccountMicrosoft().also { if (!jsonArray.isEmpty) it.msAuthProfile = TarasandeMain.get().gson.fromJson(jsonArray[0], MSAuthProfile::class.java) }
    }

    override fun create(credentials: List<String>) {
    }

    data class MSAuthProfile(val oAuthToken: OAuthToken, val xboxLiveAuth: XboxLiveAuth, val xboxLiveSecurityTokens: XboxLiveSecurityTokens, val minecraftLogin: MinecraftLogin, val minecraftProfile: MinecraftProfile, val azureApp: AzureAppPreset) {

        fun asSession() = Session(
            minecraftProfile.name,
            minecraftProfile.id,
            minecraftLogin.accessToken,
            Optional.of(xboxLiveAuth.token),
            Optional.of(azureApp.clientId.toString()), // I hate the jvm, I hate the bytecode, I hate the language, I hate me, I hate everything!
            Session.AccountType.MSA
        )

        fun renew(): MSAuthProfile { // I have no clue why I have to do this, but it crashes because "this" is null otherwise ._.
            val microsoft = AccountMicrosoft()
            microsoft.redirectUri = azureApp.redirectUri + microsoft.randomPort()
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