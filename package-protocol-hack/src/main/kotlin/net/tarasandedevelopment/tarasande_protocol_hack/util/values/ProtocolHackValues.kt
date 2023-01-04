package net.tarasandedevelopment.tarasande_protocol_hack.util.values

import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.vialoadingbase.util.VersionListEnum
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.command.ViaDumpBypassSender
import net.tarasandedevelopment.tarasande_protocol_hack.util.extension.andOlder
import net.tarasandedevelopment.tarasande_protocol_hack.util.extension.rangeTo

object ProtocolHackValues {

    // General
    val autoChangeValuesDependentOnVersion = ValueBoolean(this, "Auto change values dependent on version", true)
    val betaCraftAuth = ValueBoolean(this, "BetaCraft auth", true)

    @Suppress("unused")
    val createViaDump = object : ValueButton(this, "Create via dump") {
        override fun isEnabled() = !MinecraftClient.getInstance().isInSingleplayer && MinecraftClient.getInstance().world != null
        override fun onChange() {
            Via.getManager().commandHandler.getSubCommand("dump")?.execute(ViaDumpBypassSender, arrayOf())
        }
    }

    // 1.19.2 -> 1.19
    val disableSecureChatWarning = ValueBooleanProtocol("Disable secure chat warning", VersionListEnum.r1_19.andOlder())

    // 1.19 -> 1.18.2
    val hideSignatureIndicator = ValueBooleanProtocol("Hide signature indicator", VersionListEnum.r1_18_2.andOlder())
    val disableSequencing = ValueBooleanProtocol("Disable sequencing", VersionListEnum.r1_18_2.andOlder())

    // 1.14 -> 1.13.2
    val smoothOutMerchantScreens = ValueBooleanProtocol("Smooth out merchant screens", VersionListEnum.r1_13_2.andOlder())

    // 1.13 -> 1.12.2
    val removeNewTabCompletion = ValueBooleanProtocol("Remove new tab completion", VersionListEnum.r1_12_2.andOlder())
    val executeInputsInSync = ValueBooleanProtocol("Execute inputs in sync", VersionListEnum.r1_12_2.andOlder())

    // 1.9 -> 1.8.x
    val removeCooldowns = ValueBooleanProtocol("Remove cooldowns", VersionListEnum.r1_8.andOlder())
    val sendIdlePacket = ValueBooleanProtocol("Send idle packet", VersionListEnum.r1_8 .. VersionListEnum.r1_3_1tor1_3_2)
}

open class ValueBooleanProtocol(name: String, vararg val version: ProtocolRange) : ValueBoolean(ProtocolHackValues, "$name (" + formatRange(*version) + ")", false)
