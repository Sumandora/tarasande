package su.mandora.tarasande.module.misc

import de.florianmichael.viaprotocolhack.util.VersionList
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory

class ModuleDisableSequencePackets : Module("Disable sequence packets", "Disables sequencing", ModuleCategory.MISC) {

    override fun isEnabled(): Boolean {
        return VersionList.isNewerOrEqualTo(VersionList.R1_19)
    }
}