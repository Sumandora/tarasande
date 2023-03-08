package de.florianmichael.tarasande_viafabricplus

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.tarasande_viafabricplus.viafabricplus.CheatingSettings
import de.florianmichael.viafabricplus.ViaFabricPlus
import de.florianmichael.viafabricplus.ViaFabricPlusAddon
import de.florianmichael.viafabricplus.definition.v1_8_x.IdlePacketExecutor
import de.florianmichael.viafabricplus.settings.groups.DebugSettings
import de.florianmichael.vialoadingbase.ViaLoadingBase
import de.florianmichael.vialoadingbase.platform.ProtocolRange
import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.EventDispatcher
import net.tarasandedevelopment.tarasande.event.impl.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.exploit.ModuleTickBaseManipulation
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleInventoryMove

class TarasandeViaFabricPlus : ClientModInitializer, ViaFabricPlusAddon {

    companion object {
        lateinit var cancelOpenPacket: ValueBoolean
    }

    override fun onLoad() {
        ViaFabricPlus.getClassWrapper().loadGroup(CheatingSettings)
    }

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            cancelOpenPacket = ValueBoolean(ManagerModule.get(ModuleInventoryMove::class.java), "Cancel open packet (" + ProtocolRange.andOlder(ProtocolVersion.v1_11_1) + ")", false, isEnabled = { ViaLoadingBase.getClassWrapper().targetVersion.isOlderThanOrEqualTo(ProtocolVersion.v1_11_1) })

            ManagerModule.get(ModuleTickBaseManipulation::class.java).apply {
                val chargeOnIdlePacketSkip = ValueBoolean(this, "Charge on idle packet skip (" + DebugSettings.getClassWrapper().sendIdlePacket.protocolRange + ")", false, isEnabled = { !DebugSettings.getClassWrapper().sendIdlePacket.value })

                IdlePacketExecutor.registerIdlePacketSkipExecute {
                    if (chargeOnIdlePacketSkip.isEnabled() && chargeOnIdlePacketSkip.value)
                        shifted += mc.renderTickCounter.tickTime.toLong()
                }
            }
        }
    }
}
