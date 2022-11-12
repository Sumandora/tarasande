package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.ghost

import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.events.impl.EventBoundingBoxOverride
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleHitBox : Module("Hit box", "Makes enemy hit boxes larger", ModuleCategory.GHOST) {

    private val expand = ValueNumber(this, "Expand", 0.0, 0.0, 1.0, 0.1)

    init {
        registerEvent(EventBoundingBoxOverride::class.java) { event ->
            if (PlayerUtil.isAttackable(event.entity))
                event.boundingBox = event.boundingBox.expand(expand.value)
        }
    }

}