@file:Suppress("ClassName")

package net.tarasandedevelopment.tarasande.features.protocol.platform

import com.mojang.bridge.game.PackType
import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.SharedConstants
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventConnectServer
import net.tarasandedevelopment.tarasande.features.protocol.util.ProtocolRange
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IFontStorage_Protocol
import net.tarasandedevelopment.tarasande.util.extension.andOlder
import net.tarasandedevelopment.tarasande.value.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.value.impl.ValueNumber
import net.tarasandedevelopment.tarasande.value.impl.ValueText
import net.tarasandedevelopment.tarasande.value.meta.ValueButton

object ProtocolHackValues {

    // General
    @Suppress("unused")
    private val viaVersionDebug = object : ValueBoolean(this, "ViaVersion Debug", false) {
        override fun onChange() {
            @Suppress("DEPRECATION")
            Via.getManager().isDebug = value
        }
    }
    val filterItemGroups = ValueBoolean(this, "Filter item groups", true)

    val changeResourcePackDownloadHeaders = ValueBoolean(this, "Change resource pack download headers", false)
    val versionName = object : ValueText(this, "Version name", SharedConstants.getGameVersion().name) {
        override fun isEnabled() = changeResourcePackDownloadHeaders.value
    }
    val packFormat = object : ValueNumber(this, "Pack format", 0.0, SharedConstants.getGameVersion().getPackVersion(PackType.RESOURCE).toDouble(), SharedConstants.getGameVersion().getPackVersion(PackType.RESOURCE).toDouble(), 1.0) {
        override fun isEnabled() = changeResourcePackDownloadHeaders.value
    }

    @Suppress("unused")
    val createViaDump = object : ValueButton(this, "Create via dump") {
        override fun isEnabled() = !MinecraftClient.getInstance().isInSingleplayer && MinecraftClient.getInstance().world != null
        override fun onChange() {
            Via.getManager().commandHandler.getSubCommand("dump")?.execute(ViaDumpBypassSender, arrayOf())
        }
    }

    // 1.19 -> 1.18.2
    val hideSignatureIndicator = ValueBooleanProtocol("Hide signature indicator", ProtocolVersion.v1_18_2.andOlder())
    val disableSequencing = ValueBooleanProtocol("Disable sequencing", ProtocolVersion.v1_18_2.andOlder())

    // 1.14 -> 1.13.2
    val smoothOutMerchantScreens = ValueBooleanProtocol("Smooth out merchant screens", ProtocolVersion.v1_13_2.andOlder())

    // 1.13 -> 1.12.2
    val removeNewTabCompletion = ValueBooleanProtocol("Remove new tab completion", ProtocolVersion.v1_12_2.andOlder())
    val fontCacheFix = object : ValueBooleanProtocol("Font cache fix", ProtocolVersion.v1_12_2.andOlder()) {
        override fun isEnabled() = !FabricLoader.getInstance().isModLoaded("dashloader")

        init {
            TarasandeMain.get().managerEvent.add(EventConnectServer::class.java) {
                if (value && isEnabled()) {
                    MinecraftClient.getInstance().fontManager.fontStorages.values.forEach {
                        (it as IFontStorage_Protocol).protocolhack_clearCaches()
                    }
                }
            }
        }
    }

    // 1.9 -> 1.8.x
    val removeCooldowns = ValueBooleanProtocol("Remove cooldowns", ProtocolVersion.v1_8.andOlder())


    fun update(protocol: ProtocolVersion) {
        // Owners may change, orientate on one setting
        TarasandeMain.get().valueSystem.getValues(viaVersionDebug.owner).forEach {
            if (it is ValueBooleanProtocol)
                it.value = it.version.any { range -> protocol in range }
        }
    }
}

open class ValueBooleanProtocol(name: String, vararg val version: ProtocolRange) : ValueBoolean(ProtocolHackValues, "$name (" + version.joinToString(", ") { it.toString() } + ")", false)
