
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.system.exitProcess

// Generate all pack formats
fun main(args: Array<String>) {
    fun request(url: String) = URL(url).openConnection().getInputStream().readAllBytes()
    val gson = Gson()
    val launchermeta = gson.fromJson(request("https://launchermeta.mojang.com/mc/game/version_manifest.json").decodeToString(), JsonObject::class.java)

    launchermeta.getAsJsonArray("versions").forEach {
        it.asJsonObject.apply {
            val id = get("id").asString
            if (id.contains("w") || id.contains("a") || id.contains("rc") || id.contains("pre")) // who cares about snapshots lmao
                return@forEach
            gson.fromJson(request(get("url").asString).decodeToString(), JsonObject::class.java).apply {
                val bais = ByteArrayInputStream(request(getAsJsonObject("downloads").getAsJsonObject("client").get("url").asString))
                val zis = ZipInputStream(bais)

                var entry: ZipEntry?
                while (zis.nextEntry.also { entry = it } != null) {
                    if (entry?.name?.equals("version.json") == true) {
                        gson.fromJson(zis.readAllBytes().decodeToString(), JsonObject::class.java)?.apply {
                            val packVersion = get("pack_version")
                            val name = get("name").asString
                            val id = get("id").asString
                            val packFormat = if (packVersion.isJsonObject) packVersion.asJsonObject.get("resource").asInt else packVersion.asInt

                            println("registerReplacement(" + packFormat + ", ProtocolVersion.v_" + name.replace(".", "_") + ", \"" + name + (if (name != id) "\", \"$id\")" else "\")"))
                        }
                    }
                    zis.closeEntry()
                }

                zis.close()
            }
            if (id.equals("1.14"))
                exitProcess(1) // The versions below 1.14 don't have a version.json, we have to manually provide that :c
        }
    }
}
