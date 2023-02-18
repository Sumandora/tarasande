package de.florianmichael.tarasande_protocol_hack.tarasande.module

import de.florianmichael.tarasande_protocol_hack.tarasande.values.ProtocolHackValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleNoWeb

lateinit var removeVelocityReset: ValueBoolean

fun modifyModuleNoWeb() {
    removeVelocityReset = ValueBoolean(ManagerModule.get(ModuleNoWeb::class.java), "Remove velocity reset (" + ProtocolHackValues.emulatePlayerMovement.name + ")", false, isEnabled = { ProtocolHackValues.emulatePlayerMovement.value })
}
