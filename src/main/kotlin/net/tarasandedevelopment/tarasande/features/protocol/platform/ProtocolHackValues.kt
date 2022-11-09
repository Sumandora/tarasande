@file:Suppress("ClassName")

package net.tarasandedevelopment.tarasande.features.protocol.platform

import com.mojang.bridge.game.PackType
import com.viaversion.viaversion.api.Via
import net.minecraft.SharedConstants
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueNumber
import net.tarasandedevelopment.tarasande.value.ValueText
import net.tarasandedevelopment.tarasande.value.meta.ValueButton

object ProtocolHackValues {

    // General
    private val viaVersionDebug = object : ValueBoolean(this, "ViaVersion Debug", false) {
        override fun onChange() {
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
    val createViaDump = object : ValueButton(this, "Create via dump") {
        override fun isEnabled() = !MinecraftClient.getInstance().isInSingleplayer && MinecraftClient.getInstance().world != null
        override fun onChange() {
            Via.getManager().commandHandler.getSubCommand("dump")?.execute(ViaDumpBypassSender, arrayOf())
        }
    }

    // 1.19 -> 1.18.2
    val hideSignatureIndicator = ValueBoolean(this, "Hide signature indicator (1.19 -> 1.18.2)", false)
    val disableSequencing = ValueBoolean(this, "Disable sequencing (1.19 -> 1.18.2)", false)

    // 1.14 -> 1.13.2
    val smoothOutMerchantScreens = ValueBoolean(this, "Smooth out merchant screens (1.14 -> 1.13.2)", true)

    // 1.13 -> 1.12.2
    val removeNewTabCompletion = ValueBoolean(this, "Remove new tab completion (1.13 -> 1.12.2)", true)

    // 1.9 -> 1.8.x
    val removeCooldowns = ValueBoolean(this, "Remove cooldowns (1.9 -> 1.8.x)", false)
}
