package su.mandora.tarasande.module.misc

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventMouse
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.player.PlayerUtil
import java.util.function.Consumer

class ModuleMidClick : Module("Mid click", "Friends mid-clicked players", ModuleCategory.MISC) {

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventMouse) {
            if (event.action == GLFW.GLFW_PRESS && event.button == 2) {
                val hitResult = PlayerUtil.getTargetedEntity(100.0, Rotation(mc.player!!), true)
                if (hitResult != null && hitResult.type == HitResult.Type.ENTITY && hitResult is EntityHitResult)
                    if (hitResult.entity is PlayerEntity)
                        TarasandeMain.get().friends?.changeFriendState((hitResult.entity as PlayerEntity).gameProfile)
            }
        }
    }

}