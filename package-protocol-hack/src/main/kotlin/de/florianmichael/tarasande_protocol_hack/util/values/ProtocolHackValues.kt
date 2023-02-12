package de.florianmichael.tarasande_protocol_hack.util.values

import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.clampclient.injection.instrumentation_1_8.definition.MathHelper_1_8
import de.florianmichael.rmath.mathtable.MathTableRegistry
import de.florianmichael.tarasande_protocol_hack.util.extension.andOlder
import de.florianmichael.tarasande_protocol_hack.util.extension.rangeTo
import de.florianmichael.tarasande_protocol_hack.util.extension.singleton
import de.florianmichael.tarasande_protocol_hack.util.values.command.ViaDumpBypassSender
import de.florianmichael.viabeta.api.BetaProtocols
import de.florianmichael.viasnapshot.api.SnapshotProtocols
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton

object ProtocolHackValues {

    // General
    val autoChangeValuesDependentOnVersion = ValueBoolean(this, "Auto change values dependent on version", true)
    val filterItemGroups = ValueBoolean(this, "Filter item groups", true)
    val betaCraftAuth = ValueBoolean(this, "BetaCraft auth", true)
    val entityDimensionReplacements = ValueBoolean(this, "Entity dimension replacements", true)

    @Suppress("unused")
    val createViaDump = object : ValueButton(this, "Create via dump", isEnabled = { !mc.isInSingleplayer && mc.world != null }) {
        override fun onClick() {
            Via.getManager().commandHandler.getSubCommand("dump")?.execute(ViaDumpBypassSender, arrayOf())
        }
    }

    // 1.19.2 -> 1.19
    val disableSecureChatWarning = ValueBooleanProtocol("Disable secure chat warning", ProtocolVersion.v1_19.andOlder())

    // 1.19 -> 1.18.2
    val hideSignatureIndicator = ValueBooleanProtocol("Hide signature indicator", ProtocolVersion.v1_18_2.andOlder())
    val disableSequencing = ValueBooleanProtocol("Disable sequencing", ProtocolVersion.v1_18_2.andOlder())

    // 20w14 infinite -> 1.16
    val emulateWrongPlayerAbilities = ValueBooleanProtocol("Emulate (wrong!) player abilities", SnapshotProtocols.s20w14infinite.singleton())

    // 1.14 -> 1.13.2
    val smoothOutMerchantScreens = ValueBooleanProtocol("Smooth out merchant screens", ProtocolVersion.v1_13_2.andOlder())

    // 1.13 -> 1.12.2
    val removeNewTabCompletion = ValueBooleanProtocol("Remove new tab completion", ProtocolVersion.v1_12_2.andOlder())
    val executeInputsInSync = ValueBooleanProtocol("Execute inputs in sync", ProtocolVersion.v1_12_2.andOlder())
    val emulateMouseInputs = ValueBooleanProtocol("Emulate mouse inputs", ProtocolVersion.v1_12_2.andOlder())
    val sneakInstant = ValueBooleanProtocol("Sneak instant", ProtocolVersion.v1_8..ProtocolVersion.v1_12_2)
    val replaceRayTrace = ValueBooleanProtocol("Replace ray trace", ProtocolVersion.v1_12_2.andOlder())
    val replacePetrifiedOakSlab = ValueBooleanProtocol("Replace petrified oak slab", ProtocolVersion.v1_12_2..BetaProtocols.r1_3_1tor1_3_2)

    // 1.9 -> 1.8.x
    val removeCooldowns = ValueBooleanProtocol("Remove cooldowns", ProtocolVersion.v1_8.andOlder())
    val emulateSignGUIModification = ValueBooleanProtocol("Emulate sign gui modification", ProtocolVersion.v1_8.andOlder())
    val sendIdlePacket = ValueBooleanProtocol("Send idle packet", ProtocolVersion.v1_8..BetaProtocols.r1_3_1tor1_3_2)
    val emulatePlayerMovement = ValueBooleanProtocol("Emulate player movement", ProtocolVersion.v1_8.andOlder())
    init {
        object : ValueMode(this, "Fast math tables", false, *MathTableRegistry.values().map { it.toString() }.toTypedArray()) {
            override fun onChange(index: Int, oldSelected: Boolean, newSelected: Boolean) {
                MathHelper_1_8.mathTable = MathTableRegistry.values()[index]
            }
        }
    }
    val bruteforceRaytraceFastMathTables = ValueMode(this, "Bruteforce Raytrace: Fast math tables", true, *MathTableRegistry.values().map { it.toString() }.toTypedArray())
    val emulateArmorHud = ValueBooleanProtocol("Emulate armor hud", ProtocolVersion.v1_8.andOlder())
    val replaceAttributeModifiers = ValueBooleanProtocol("Replace attribute modifiers", ProtocolVersion.v1_8.andOlder())

    // 1.8 -> 1.7
    val replaceSneaking = ValueBooleanProtocol("Replace sneaking", ProtocolVersion.v1_7_6.andOlder())
    val adjustLongSneaking = ValueBooleanProtocol("Adjust long sneaking", ProtocolVersion.v1_7_6.andOlder())

    // a1_0_15 -> c0_28toc0_30
    val replaceCreativeInventory = ValueBooleanProtocol("Replace creative inventory", BetaProtocols.c0_28toc0_30.andOlder())
}

open class ValueBooleanProtocol(name: String, vararg val version: ProtocolRange) : ValueBoolean(ProtocolHackValues, "$name (" + formatRange(*version) + ")", false)
