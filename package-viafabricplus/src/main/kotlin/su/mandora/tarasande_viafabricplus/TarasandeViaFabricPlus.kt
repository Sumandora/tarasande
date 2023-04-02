package su.mandora.tarasande_viafabricplus

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import su.mandora.tarasande_viafabricplus.tarasande.EveryItemOnArmor
import de.florianmichael.viafabricplus.event.SkipIdlePacketCallback
import de.florianmichael.viafabricplus.settings.groups.DebugSettings
import de.florianmichael.vialoadingbase.ViaLoadingBase
import de.florianmichael.vialoadingbase.platform.ProtocolRange
import net.fabricmc.api.ClientModInitializer
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.impl.exploit.ModuleTickBaseManipulation
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleInventoryMove

class TarasandeViaFabricPlus : ClientModInitializer {

    companion object {
        lateinit var cancelOpenPacket: ValueBoolean
    }

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            cancelOpenPacket = ValueBoolean(ManagerModule.get(ModuleInventoryMove::class.java), "Cancel open packet (" + ProtocolRange.andOlder(ProtocolVersion.v1_11_1) + ")", false, isEnabled = { ViaLoadingBase.getClassWrapper().targetVersion.isOlderThanOrEqualTo(ProtocolVersion.v1_11_1) })

            EveryItemOnArmor

            ManagerModule.get(ModuleTickBaseManipulation::class.java).apply {
                val chargeOnIdlePacketSkip = ValueBoolean(this, "Charge on idle packet skip (" + DebugSettings.INSTANCE.sendIdlePacket.protocolRange + ")", false, isEnabled = { !DebugSettings.INSTANCE.sendIdlePacket.value })

                SkipIdlePacketCallback.EVENT.register(SkipIdlePacketCallback {
                    if (chargeOnIdlePacketSkip.isEnabled() && chargeOnIdlePacketSkip.value)
                        shifted += mc.renderTickCounter.tickTime.toLong()
                })
            }
        }
    }
}
