package net.tarasandedevelopment.tarasande.protocolhack.fixes

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mojang.bridge.game.PackType
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion
import de.florianmichael.viaprotocolhack.ViaProtocolHack
import net.minecraft.GameVersion
import net.minecraft.SharedConstants
import net.tarasandedevelopment.tarasande.protocolhack.util.ExceptionOutdatedRegistry
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.system.exitProcess

object PackFormats {

    val map = HashMap<Int, GameVersion>()

    init {
        registerVersion(ProtocolVersion.v1_19_1, 9, "1.19.2")
        registerVersion(ProtocolVersion.v1_19, 9, "1.19")
        registerVersion(ProtocolVersion.v1_18_2, 8, "1.18.2")
        registerVersion(ProtocolVersion.v1_18, 8, "1.18")
        registerVersion(ProtocolVersion.v1_17_1, 7, "1.17.1")
        registerVersion(ProtocolVersion.v1_17, 7, "1.17")
        registerVersion(ProtocolVersion.v1_16_4, 6, "1.16.4")
        registerVersion(ProtocolVersion.v1_16_3, 6, "1.16.3")
        registerVersion(ProtocolVersion.v1_16_2, 6, "1.16.2")
        registerVersion(ProtocolVersion.v1_16_1, 5, "1.16.1")
        registerVersion(ProtocolVersion.v1_16, 5, "1.16")
        registerVersion(ProtocolVersion.v1_15_2, 5, "1.15.2")
        registerVersion(ProtocolVersion.v1_15_1, 5, "1.15.1")
        registerVersion(ProtocolVersion.v1_15, 5, "1.15")
        registerVersion(ProtocolVersion.v1_14_4, 4, "1.14.4")
        registerVersion(ProtocolVersion.v1_14_3, 4, "1.14.3")
        registerVersion(ProtocolVersion.v1_14_2, 4, "1.14.2", "1.14.2 / f647ba8dc371474797bee24b2b312ff4")
        registerVersion(ProtocolVersion.v1_14_1, 4, "1.14.1", "1.14.1 / a8f78b0d43c74598a199d6d80cda413f")
        registerVersion(ProtocolVersion.v1_14, 4, "1.14", "1.14 / 5dac5567e13e46bdb0c1d90aa8d8b3f7")
        registerVersion(ProtocolVersion.v1_13_2, 4, "1.13.2")
        registerVersion(ProtocolVersion.v1_13_1, 4, "1.13.1")
        registerVersion(ProtocolVersion.v1_13, 4, "1.13")
        registerVersion(ProtocolVersion.v1_12_2, 3, "1.12.2")
        registerVersion(ProtocolVersion.v1_12_1, 3, "1.12.1")
        registerVersion(ProtocolVersion.v1_12, 3, "1.12")
        registerVersion(ProtocolVersion.v1_11_1, 3, "1.11.1")
        registerVersion(ProtocolVersion.v1_11, 3, "1.11")
        registerVersion(ProtocolVersion.v1_10, 2, "1.10")
        registerVersion(ProtocolVersion.v1_9_3, 2, "1.9.4")
        registerVersion(ProtocolVersion.v1_9_2, 2, "1.9.2")
        registerVersion(ProtocolVersion.v1_9_1, 2, "1.9.1")
        registerVersion(ProtocolVersion.v1_9, 2, "1.9")
        registerVersion(ProtocolVersion.v1_8, 1, "1.8.9")
        registerVersion(LegacyProtocolVersion.v1_7_6, 1, "1.7.10")
        registerVersion(LegacyProtocolVersion.v1_7_1, 1, "1.7.5")
    }

    fun checkOutdated(nativeVersion: Int) {
        if(map[nativeVersion].let { it == null || it.name != SharedConstants.getGameVersion().name || it.id != SharedConstants.getGameVersion().id || it.getPackVersion(PackType.RESOURCE) != SharedConstants.getGameVersion().getPackVersion(PackType.RESOURCE) })
            throw ExceptionOutdatedRegistry("The current version has no pack format registered")
    }

    fun current(): GameVersion = map[ViaProtocolHack.instance().provider().clientsideVersion] ?: SharedConstants.getGameVersion()

    private fun registerVersion(version: ProtocolVersion, packFormat: Int, name: String = version.name, id: String = name) {
        map[version.version] = object : GameVersion {
            override fun getId() = id

            override fun getName() = name

            override fun getReleaseTarget() = null

            override fun getProtocolVersion() = version.version

            override fun getBuildTime() = null // wtf bro

            override fun isStable() = true

            override fun getSaveVersion() = null

            override fun getPackVersion(packType: PackType?): Int {
                if(packType == PackType.RESOURCE)
                    return packFormat
                return super.getPackVersion(packType)
            }
        }
    }

    // Generate all pack formats
    @JvmStatic
    fun main(args: Array<String>) {
        fun request(url: String) = URL(url).openConnection().getInputStream().readAllBytes()
        val gson = Gson()
        val launchermeta = gson.fromJson(request("https://launchermeta.mojang.com/mc/game/version_manifest.json").decodeToString(), JsonObject::class.java)

        launchermeta.getAsJsonArray("versions").forEach {
            it.asJsonObject.apply {
                val id = get("id").asString
                if(id.contains("w") || id.contains("a") || id.contains("rc") || id.contains("pre")) // who cares about snapshots lmao
                    return@forEach
                gson.fromJson(request(get("url").asString).decodeToString(), JsonObject::class.java).apply {
                    val bais = ByteArrayInputStream(request(getAsJsonObject("downloads").getAsJsonObject("client").get("url").asString))
                    val zis = ZipInputStream(bais)

                    var entry: ZipEntry?
                    while(zis.nextEntry.also { entry = it } != null) {
                        if(entry?.name?.equals("version.json") == true) {
                            gson.fromJson(zis.readAllBytes().decodeToString(), JsonObject::class.java)?.apply {
                                val packVersion = get("pack_version")
                                val name = get("name").asString
                                val id = get("id").asString
                                val packFormat = if(packVersion.isJsonObject) packVersion.asJsonObject.get("resource").asInt else packVersion.asInt

                                println("registerReplacement(" + packFormat + ", ProtocolVersion.v_" + name.replace(".", "_") + ", \"" + name + (if(name != id) "\", \"$id\")" else "\")"))
                            }
                        }
                        zis.closeEntry()
                    }

                    zis.close()
                }
                if(id.equals("1.14"))
                    exitProcess(1) // The versions below 1.14 don't have a version.json, we have to manually provide that :c
            }
        }

    }
}
