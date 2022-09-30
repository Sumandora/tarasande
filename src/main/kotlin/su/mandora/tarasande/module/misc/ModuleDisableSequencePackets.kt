package su.mandora.tarasande.module.misc

import de.enzaxd.viaforge.equals.ProtocolEquals
import de.enzaxd.viaforge.equals.VersionList
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory

class ModuleDisableSequencePackets : Module("Disable sequence packets", "Disables sequencing", ModuleCategory.MISC) {

    override fun isEnabled(): Boolean {
        return ProtocolEquals.isNewerOrEqualTo(VersionList.R1_19)
    }
}