package net.tarasandedevelopment.tarasande_protocol_hack.platform

import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande_protocol_hack.extension.andOlder
import net.tarasandedevelopment.tarasande_protocol_hack.extension.rangeTo
import net.tarasandedevelopment.tarasande_protocol_hack.util.ProtocolRange
import net.tarasandedevelopment.tarasande_protocol_hack.util.formatRange

object ProtocolHackValues {

    // General
    val filterItemGroups = ValueBoolean(this, "Filter item groups", true)

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

    // 1.9 -> 1.8.x
    val removeCooldowns = ValueBooleanProtocol("Remove cooldowns", ProtocolVersion.v1_8.andOlder())
    val sendIdlePacket = ValueBooleanProtocol("Send idle packet", ProtocolVersion.v1_8 .. LegacyProtocolVersion.r1_3_1_2)
}

open class ValueBooleanProtocol(name: String, vararg val version: ProtocolRange) : ValueBoolean(ProtocolHackValues, "$name (" + formatRange(*version) + ")", false)
