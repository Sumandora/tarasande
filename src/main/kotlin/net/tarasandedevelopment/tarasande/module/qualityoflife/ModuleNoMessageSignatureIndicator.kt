package net.tarasandedevelopment.tarasande.module.qualityoflife

import de.florianmichael.viaprotocolhack.util.VersionList
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory

class ModuleNoMessageSignatureIndicator : Module("No message signature indicator", "Disables message signature indicator in the chat", ModuleCategory.QUALITY_OF_LIFE) {

    override fun isEnabled() = VersionList.isNewerOrEqualTo(VersionList.R1_19)
}
