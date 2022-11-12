package net.tarasandedevelopment.tarasande.features.module.ghost

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventBoundingBoxOverride
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.impl.ValueNumber

class ModuleHitBox : Module("Hit box", "Makes enemy hit boxes larger", ModuleCategory.GHOST) {

    private val expand = ValueNumber(this, "Expand", 0.0, 0.0, 1.0, 0.1)

    init {
        registerEvent(EventBoundingBoxOverride::class.java) { event ->
            if (PlayerUtil.isAttackable(event.entity))
                event.boundingBox = event.boundingBox.expand(expand.value)
        }
    }

}