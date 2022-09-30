package su.mandora.tarasande.module.misc

import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory

class ModuleDisableTelemetry : Module("Disable telemetry", "Disables the telemetry sender", ModuleCategory.MISC) {

    init {
        enabled = true
    }

}