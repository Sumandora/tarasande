package net.tarasandedevelopment.tarasande.features.module.render

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.viaprotocolhack.util.VersionList
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.value.impl.ValueBoolean

class ModuleAntiParticleHide : Module("Anti particle hide", "Makes invisible effects visible", ModuleCategory.RENDER) {

    val inventory = ValueBoolean(this, "Inventory", true)
    val hud = object : ValueBoolean(this, "HUD", true) {
        override fun isEnabled() = VersionList.isNewerTo(ProtocolVersion.v1_12_2)
    }
}
