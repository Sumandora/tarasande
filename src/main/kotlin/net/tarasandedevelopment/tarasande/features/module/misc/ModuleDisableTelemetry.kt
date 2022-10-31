package net.tarasandedevelopment.tarasande.features.module.misc

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory

class ModuleDisableTelemetry : Module("Disable telemetry", "Disables the telemetry sender", ModuleCategory.MISC) {

    init {
        enabled = true
    }

}