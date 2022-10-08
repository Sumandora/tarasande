package net.tarasandedevelopment.tarasande.module.render

import de.florianmichael.viaprotocolhack.util.VersionList
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.value.ValueBoolean

class ModuleAntiParticleHide : Module("Anti particle hide", "Makes invisible effects visible", ModuleCategory.RENDER) {

    val inventory = ValueBoolean(this, "Inventory", true)
    val hud = object : ValueBoolean(this, "HUD", true) {
        override fun isEnabled() = VersionList.isNewerTo(VersionList.R1_12_2)
    }
}
