package de.florianmichael.tarasande_protocol_hack.xbox

import org.apache.http.NameValuePair
import org.apache.http.client.CookieStore
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.LaxRedirectStrategy
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object XboxUserAuthentication {
    const val USER_AGENT =
        "MCPE/Android" //"Mozilla/5.0 (XboxReplay; XboxLiveAuth/3.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
    private const val XBOX_APP = "00000000441cc96b"
    private const val URI_AUTHORIZE = "https://login.live.com/oauth20_authorize.srf"
    fun authenticate(email: String, password: String): String {
        val cookieStore: CookieStore = BasicCookieStore()
        val client: HttpClient = HttpClientBuilder.create()
            .setRedirectStrategy(LaxRedirectStrategy())
            .setDefaultCookieStore(cookieStore).build()
        val ctx = HttpClientContext.create()
        return authenticate(client, ctx, email, password)
    }

    private fun preAuth(client: HttpClient, ctx: HttpClientContext): PreAuthResponse {
        val queries = "client_id=" + XBOX_APP +
                "&redirect_uri=" + URLEncoder.encode("https://login.live.com/oauth20_desktop.srf", StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode("service::user.auth.xboxlive.com::MBI_SSL", StandardCharsets.UTF_8) +
                "&display=touch" +
                "&response_type=token" +
                "&locale=en"
        val requestUrl = "$URI_AUTHORIZE?$queries"
        val httpUriRequest: HttpUriRequest = HttpGet(requestUrl)
        httpUriRequest.setHeader("User-Agent", USER_AGENT)
        httpUriRequest.setHeader("Accept-encoding", "gzip")
        httpUriRequest.setHeader("Accept-Language", "en-US")
        val response = client.execute(httpUriRequest, ctx)
        val body = EntityUtils.toString(response.entity, StandardCharsets.UTF_8)
        if (response.statusLine.statusCode != 200) {
            error("Failed to authenticate: Server responded with code: " + response.statusLine.statusCode)
        }
        var idx = body.indexOf("sFTTag:")
        if (idx < 0) {
            error("Failed to authenticate: sFTTag not found.")
        }
        idx = body.indexOf("value=\"", idx)
        if (idx < 0) {
            error("Failed to authenticate: sFTTag value not found.")
        }
        var sFTTag = body.substring(idx + "value=\"".length)
        for (i in sFTTag.indices) {
            val c = sFTTag[i]
            if (c == '"') {
                sFTTag = sFTTag.substring(0, i)
                break
            }
        }
        idx = body.indexOf("urlPost:'")
        if (idx < 0) {
            error("Failed to authenticate: UrlPost not found.")
        }
        var urlPost = body.substring(idx + "urlPost:'".length)
        for (i in urlPost.indices) {
            val c = urlPost[i]
            if (c == '\'') {
                urlPost = urlPost.substring(0, i)
                break
            }
        }
        return PreAuthResponse(sFTTag, urlPost)
    }

    private fun authenticate(client: HttpClient, ctx: HttpClientContext, email: String, password: String): String {
        val preAuthResponse = preAuth(client, ctx)
        val httpPost = HttpPost(preAuthResponse.postUrl)
        val postData: MutableList<NameValuePair> = ArrayList()
        postData.add(BasicNameValuePair("login", email))
        postData.add(BasicNameValuePair("loginfmt", email))
        postData.add(BasicNameValuePair("passwd", password))
        postData.add(BasicNameValuePair("PPFT", preAuthResponse.sFTTag))
        httpPost.entity = UrlEncodedFormEntity(postData, "UTF-8")
        httpPost.setHeader("User-Agent", USER_AGENT)
        httpPost.setHeader("Accept-encoding", "gzip")
        httpPost.setHeader("Accept-Language", "en-US")
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded")
        val response = client.execute(httpPost, ctx)
        if (response.statusLine.statusCode != 200) {
            error("Failed to authenticate: Server responded with code: " + response.statusLine.statusCode)
        }
        var location = httpPost.uri.toString()
        val locations = ctx.redirectLocations
        if (locations != null) {
            location = locations[locations.size - 1].toString()
        }
        val idx = location.indexOf("#access_token=")
        if (idx < 0) {
            error("Failed to authenticate: Invalid credentials.")
        }
        var accessToken = location.substring(idx + "#access_token=".length)
        for (i in accessToken.indices) {
            val c = accessToken[i]
            if (c == '&') {
                accessToken = accessToken.substring(0, i)
                break
            }
        }
        return accessToken
    }

    private class PreAuthResponse(val sFTTag: String, val postUrl: String)
}
