package net.tarasandedevelopment.tarasande.module.misc

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventMouse
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import org.lwjgl.glfw.GLFW
import java.util.function.Consumer

class ModuleMidClick : Module("Mid click", "Friends mid-clicked players", ModuleCategory.MISC) {

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventMouse) {
            if (event.action == GLFW.GLFW_PRESS && event.button == 2 && mc.currentScreen == null) {
                val hitResult = PlayerUtil.getTargetedEntity(mc.options.viewDistance.value * 16.0, Rotation(mc.player!!), true)
                if (hitResult != null && hitResult.type == HitResult.Type.ENTITY && hitResult is EntityHitResult)
                    if (hitResult.entity is PlayerEntity)
                        TarasandeMain.get().friends.changeFriendState((hitResult.entity as PlayerEntity).gameProfile)
            }
        }
    }

}