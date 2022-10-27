package net.tarasandedevelopment.tarasande.module.render

import de.florianmichael.viaprotocolhack.util.VersionList
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory

class ModuleNoMessageSignatureIndicator : Module("No message signature indicator", "Disables message signature indicator in the chat", ModuleCategory.RENDER) {

    override fun isEnabled() = VersionList.isNewerOrEqualTo(VersionList.R1_19)
}
