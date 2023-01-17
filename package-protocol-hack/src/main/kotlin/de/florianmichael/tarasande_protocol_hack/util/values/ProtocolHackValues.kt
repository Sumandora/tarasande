package de.florianmichael.tarasande_protocol_hack.util.values

import com.viaversion.viaversion.api.Via
import de.florianmichael.vialoadingbase.util.VersionListEnum
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton
import de.florianmichael.tarasande_protocol_hack.util.extension.andOlder
import de.florianmichael.tarasande_protocol_hack.util.extension.rangeTo
import de.florianmichael.tarasande_protocol_hack.util.extension.singleton
import de.florianmichael.tarasande_protocol_hack.util.values.command.ViaDumpBypassSender

object ProtocolHackValues {

    // General
    val autoChangeValuesDependentOnVersion = ValueBoolean(this, "Auto change values dependent on version", true)
    val filterItemGroups = ValueBoolean(this, "Filter item groups", true)
    val betaCraftAuth = ValueBoolean(this, "BetaCraft auth", true)

    @Suppress("unused")
    val createViaDump = object : ValueButton(this, "Create via dump") {
        override fun isEnabled() = !mc.isInSingleplayer && mc.world != null
        override fun onClick() {
            Via.getManager().commandHandler.getSubCommand("dump")?.execute(ViaDumpBypassSender, arrayOf())
        }
    }

    // 1.19.2 -> 1.19
    val disableSecureChatWarning = ValueBooleanProtocol("Disable secure chat warning", VersionListEnum.r1_19.andOlder())

    // 1.19 -> 1.18.2
    val hideSignatureIndicator = ValueBooleanProtocol("Hide signature indicator", VersionListEnum.r1_18_2.andOlder())
    val disableSequencing = ValueBooleanProtocol("Disable sequencing", VersionListEnum.r1_18_2.andOlder())

    // 20w14 infinite -> 1.16
    val emulateWrongPlayerAbilities = ValueBooleanProtocol("Emulate (wrong!) player abilities", VersionListEnum.s20w14infinite.singleton())

    // 1.14 -> 1.13.2
    val smoothOutMerchantScreens = ValueBooleanProtocol("Smooth out merchant screens", VersionListEnum.r1_13_2.andOlder())

    // 1.13 -> 1.12.2
    val removeNewTabCompletion = ValueBooleanProtocol("Remove new tab completion", VersionListEnum.r1_12_2.andOlder())
    val executeInputsInSync = ValueBooleanProtocol("Execute inputs in sync", VersionListEnum.r1_12_2.andOlder())
    val emulateMouseInputs = ValueBooleanProtocol("Emulate mouse inputs", VersionListEnum.r1_12_2.andOlder())

    // 1.9 -> 1.8.x
    val removeCooldowns = ValueBooleanProtocol("Remove cooldowns", VersionListEnum.r1_8.andOlder())
    val emulateSignGUIModification = ValueBooleanProtocol("Emulate sign gui modification", VersionListEnum.r1_8.andOlder())
    val sendIdlePacket = ValueBooleanProtocol("Send idle packet", VersionListEnum.r1_8 .. VersionListEnum.r1_3_1tor1_3_2)
    val emulatePlayerMovement = ValueBooleanProtocol("Emulate player movement", VersionListEnum.r1_8.andOlder())

    // a1_0_15 -> c0_28toc0_30
    val replaceCreativeInventory = ValueBooleanProtocol("Replace creative inventory", VersionListEnum.c0_28toc0_30.andOlder())
}

open class ValueBooleanProtocol(name: String, vararg val version: ProtocolRange) : ValueBoolean(ProtocolHackValues, "$name (" + formatRange(*version) + ")", false)
