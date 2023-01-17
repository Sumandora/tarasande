package de.florianmichael.tarasande_protocol_hack.fix.global

import com.mojang.bridge.game.PackType
import de.florianmichael.vialoadingbase.ViaLoadingBase
import de.florianmichael.vialoadingbase.util.VersionListEnum
import net.minecraft.GameVersion
import net.minecraft.SharedConstants
import de.florianmichael.tarasande_protocol_hack.util.ExceptionOutdatedRegistry

object PackFormats {

    val map = HashMap<Int, GameVersion>()

    init {
        registerVersion(VersionListEnum.r1_19_3, 12, "1.19.3")
        registerVersion(VersionListEnum.r1_19_1tor1_19_2, 9, "1.19.2")
        registerVersion(VersionListEnum.r1_19, 9, "1.19")
        registerVersion(VersionListEnum.r1_18_2, 8, "1.18.2")
        registerVersion(VersionListEnum.r1_18tor1_18_1, 8, "1.18")
        registerVersion(VersionListEnum.r1_17_1, 7, "1.17.1")
        registerVersion(VersionListEnum.r1_17, 7, "1.17")
        registerVersion(VersionListEnum.r1_16_4tor1_16_5, 6, "1.16.5")
        registerVersion(VersionListEnum.r1_16_3, 6, "1.16.3")
        registerVersion(VersionListEnum.r1_16_2, 6, "1.16.2")
        registerVersion(VersionListEnum.r1_16_1, 5, "1.16.1")
        registerVersion(VersionListEnum.r1_16, 5, "1.16")
        registerVersion(VersionListEnum.r1_15_2, 5, "1.15.2")
        registerVersion(VersionListEnum.r1_15_1, 5, "1.15.1")
        registerVersion(VersionListEnum.r1_15, 5, "1.15")
        registerVersion(VersionListEnum.r1_14_4, 4, "1.14.4")
        registerVersion(VersionListEnum.r1_14_3, 4, "1.14.3")
        registerVersion(VersionListEnum.r1_14_2, 4, "1.14.2", "1.14.2 / f647ba8dc371474797bee24b2b312ff4")
        registerVersion(VersionListEnum.r1_14_1, 4, "1.14.1", "1.14.1 / a8f78b0d43c74598a199d6d80cda413f")
        registerVersion(VersionListEnum.r1_14, 4, "1.14", "1.14 / 5dac5567e13e46bdb0c1d90aa8d8b3f7")
        registerVersion(VersionListEnum.r1_13_2, 4, "1.13.2") // ids weren't sent over the http headers back then, why care...
        registerVersion(VersionListEnum.r1_13_1, 4, "1.13.1")
        registerVersion(VersionListEnum.r1_13, 4, "1.13")
        registerVersion(VersionListEnum.r1_12_2, 3, "1.12.2")
        registerVersion(VersionListEnum.r1_12_1, 3, "1.12.1")
        registerVersion(VersionListEnum.r1_12, 3, "1.12")
        registerVersion(VersionListEnum.r1_11_1to1_11_2, 3, "1.11.2")
        registerVersion(VersionListEnum.r1_11, 3, "1.11")
        registerVersion(VersionListEnum.r1_10, 2, "1.10.2")
        registerVersion(VersionListEnum.r1_9_3tor1_9_4, 2, "1.9.4")
        registerVersion(VersionListEnum.r1_9_2, 2, "1.9.2")
        registerVersion(VersionListEnum.r1_9_1, 2, "1.9.1")
        registerVersion(VersionListEnum.r1_9, 2, "1.9")
        registerVersion(VersionListEnum.r1_8, 1, "1.8.9")
        registerVersion(VersionListEnum.r1_7_6tor1_7_10, 1, "1.7.10")
        registerVersion(VersionListEnum.r1_7_2tor1_7_5, 1, "1.7.5")
    }

    fun checkOutdated(nativeVersion: Int) {
        if (map[nativeVersion].let { it == null || it.name != SharedConstants.getGameVersion().name || it.id != SharedConstants.getGameVersion().id || it.getPackVersion(PackType.RESOURCE) != SharedConstants.getGameVersion().getPackVersion(PackType.RESOURCE) })
            throw ExceptionOutdatedRegistry("The current version has no pack format registered")
    }

    fun current(): GameVersion = map[ViaLoadingBase.getTargetVersion().originalVersion] ?: SharedConstants.getGameVersion()

    private fun registerVersion(version: VersionListEnum, packFormat: Int, name: String = version.name, id: String = name) {
        map[version.version] = object : GameVersion {
            override fun getId() = id

            override fun getName() = name

            override fun getProtocolVersion() = version.version

            override fun getPackVersion(packType: PackType?): Int {
                if (packType == PackType.RESOURCE)
                    return packFormat
                else
                    throw UnsupportedOperationException()
            }

            override fun getBuildTime() = null // wtf bro

            override fun isStable() = true

            override fun getSaveVersion() = null
        }
    }
}
