package net.tarasandedevelopment.tarasande.module.misc

import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory

class ModuleDisableTelemetry : Module("Disable telemetry", "Disables the telemetry sender", ModuleCategory.MISC) {

    init {
        enabled = true
    }

}