package net.tarasandedevelopment.tarasande.protocolhack.fix

import com.mojang.bridge.game.PackType
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion
import de.florianmichael.viaprotocolhack.ViaProtocolHack
import net.minecraft.GameVersion
import net.minecraft.SharedConstants
import net.tarasandedevelopment.tarasande.protocolhack.util.ExceptionOutdatedRegistry

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
        registerVersion(ProtocolVersion.v1_13_2, 4, "1.13.2") // ids weren't sent over the http headers back then, why care...
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
        if (map[nativeVersion].let { it == null || it.name != SharedConstants.getGameVersion().name || it.id != SharedConstants.getGameVersion().id || it.getPackVersion(PackType.RESOURCE) != SharedConstants.getGameVersion().getPackVersion(PackType.RESOURCE) })
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
                if (packType == PackType.RESOURCE)
                    return packFormat
                return super.getPackVersion(packType)
            }
        }
    }
}
