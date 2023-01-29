package de.florianmichael.tarasande_protocol_hack.tarasande.module

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.tarasande_protocol_hack.util.extension.andOlder
import de.florianmichael.vialoadingbase.ViaLoadingBase
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleInventoryMove

lateinit var cancelOpenPacket: ValueBoolean

fun modifyModuleInventoryMove() {
    cancelOpenPacket = object : ValueBoolean(ManagerModule.get(ModuleInventoryMove::class.java), "Cancel open packet (" + ProtocolVersion.v1_11_1.andOlder() + ")", false) {
        override fun isEnabled() = ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_11_1)
    }
}
