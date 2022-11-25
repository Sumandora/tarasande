package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.viaprotocolhack.util.VersionList
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleAntiParticleHide : Module("Anti particle hide", "Makes invisible effects visible", ModuleCategory.RENDER) {

    val inventory = ValueBoolean(this, "Inventory", true)
    val hud = object : ValueBoolean(this, "HUD", true) {
        override fun isEnabled() = VersionList.isNewerTo(ProtocolVersion.v1_12_2)
    }
}
