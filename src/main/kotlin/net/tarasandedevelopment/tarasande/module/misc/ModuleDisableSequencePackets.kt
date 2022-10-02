package net.tarasandedevelopment.tarasande.module.misc

import de.florianmichael.viaprotocolhack.util.VersionList
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory

class ModuleDisableSequencePackets : Module("Disable sequence packets", "Disables sequencing", ModuleCategory.MISC) {

    override fun isEnabled(): Boolean {
        return VersionList.isNewerOrEqualTo(VersionList.R1_19)
    }
}