package de.florianmichael.tarasande_protocol_hack.tarasande.module

import de.florianmichael.tarasande_protocol_hack.util.extension.andOlder
import de.florianmichael.vialoadingbase.ViaLoadingBase
import de.florianmichael.vialoadingbase.util.VersionListEnum
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleInventoryMove

lateinit var cancelOpenPacket: ValueBoolean

fun modifyModuleInventoryMove() {
    cancelOpenPacket = object : ValueBoolean(ManagerModule.get(ModuleInventoryMove::class.java), "Cancel open packet (" + VersionListEnum.r1_11_1to1_11_2.andOlder() + ")", false) {
        override fun isEnabled() = ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_11_1to1_11_2)
    }
}
