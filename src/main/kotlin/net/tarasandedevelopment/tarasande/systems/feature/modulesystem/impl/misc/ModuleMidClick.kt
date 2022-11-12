package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.misc

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.events.impl.EventMouse
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import org.lwjgl.glfw.GLFW

class ModuleMidClick : Module("Mid click", "Friends mid-clicked players", ModuleCategory.MISC) {

    init {
        registerEvent(EventMouse::class.java) { event ->
            if (event.action == GLFW.GLFW_PRESS && event.button == 2 && mc.currentScreen == null) {
                val hitResult = PlayerUtil.getTargetedEntity(mc.options.viewDistance.value * 16.0, Rotation(mc.player!!), true)
                if (hitResult != null && hitResult.type == HitResult.Type.ENTITY && hitResult is EntityHitResult)
                    if (hitResult.entity is PlayerEntity)
                        TarasandeMain.get().friends.changeFriendState((hitResult.entity as PlayerEntity).gameProfile)
            }
        }
    }

}