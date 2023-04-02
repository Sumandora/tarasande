package su.mandora.tarasande.system.feature.modulesystem.impl.ghost

import net.minecraft.entity.LivingEntity
import su.mandora.tarasande.event.impl.EventBoundingBoxOverride
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleHitBox : Module("Hit box", "Makes enemy hit boxes larger", ModuleCategory.GHOST) {

    private val expand = ValueNumber(this, "Expand", 0.0, 0.0, 1.0, 0.1)
    private val disableInterpolation = ValueBoolean(this, "Disable interpolation", false)

    init {
        registerEvent(EventBoundingBoxOverride::class.java, 1001) { event ->
            if (disableInterpolation.value) {
                if (event.entity is LivingEntity) {
                    val center = event.boundingBox.center
                    event.boundingBox =
                        event.boundingBox
                            .offset(-center.x, -center.y, -center.z) // Move to 0,0,0
                            .offset(event.entity.serverX, event.entity.serverY, event.entity.serverZ) // Move to position
                }
            }
            if (PlayerUtil.isAttackable(event.entity))
                event.boundingBox = event.boundingBox.expand(expand.value)
        }
    }

}