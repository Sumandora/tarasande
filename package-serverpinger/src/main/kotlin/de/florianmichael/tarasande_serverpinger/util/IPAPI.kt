package de.florianmichael.tarasande_serverpinger.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.minecraft.client.network.ServerAddress
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.Thread
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

const val url = "http://ip-api.com/json/%s?fields=status,message,continent,continentCode,country,countryCode,region,regionName,city,district,zip,lat,lon,timezone,currency,isp,org,as,asname,reverse,mobile,proxy,query"

object IPAPI {
    private val gson = Gson().newBuilder().create()

    fun request(address: String, finish: (output: Array<String>) -> Unit) {
        Thread("IP API lookup thread") {
            URL(url.format(ServerAddress.parse(address).address)).apply {
                val reader = BufferedReader(InputStreamReader(openStream()))
                val output = StringBuilder()
                for (line in reader.lines()) output.append(line)
                reader.close()

                gson.fromJson(output.toString(), JsonObject::class.java).apply {
                    val data = ArrayList<String>()
                    if (has("query")) {
                        val query = get("query").asString
                        if (query.isNotEmpty()) data.add("IP/Query address: $query")
                    }
                    if (has("reverse")) {
                        val reverse = get("reverse").asString
                        if (reverse.isNotEmpty()) data.add("Reverse: $reverse")
                    }
                    if (has("asname")) {
                        val asname = get("asname").asString
                        if (asname.isNotEmpty()) data.add("Net name: $asname")
                    }
                    if (has("as")) {
                        val asDescription = get("as").asString
                        if (asDescription.isNotEmpty()) data.add("Net description: $asDescription")
                    }
                    if (has("isp")) {
                        val isp = get("isp").asString
                        if (isp.isNotEmpty()) data.add("Internet service provider: $isp")
                    }
                    if (has("org")) {
                        val org = get("org").asString
                        if (org.isNotEmpty()) data.add("Organisation name: $org")
                    }
                    if (has("countryCode")) {
                        val countryCode = get("countryCode").asString
                        if (countryCode.isNotEmpty()) data.add("Country: $countryCode")
                    }
                    finish.invoke(data.toTypedArray())
                }
            }
        }.start()
    }
}
