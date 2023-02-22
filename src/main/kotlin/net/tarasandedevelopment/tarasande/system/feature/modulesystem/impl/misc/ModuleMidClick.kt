package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.EntityHitResult
import net.tarasandedevelopment.tarasande.event.impl.EventMouse
import net.tarasandedevelopment.tarasande.feature.friend.Friends
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.minecraft.isEntityHitResult
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import org.lwjgl.glfw.GLFW

class ModuleMidClick : Module("Mid click", "Friends mid-clicked players", ModuleCategory.MISC) {

    init {
        registerEvent(EventMouse::class.java) { event ->
            if (event.action == GLFW.GLFW_PRESS && event.button == 2 && mc.currentScreen == null) {
                val hitResult = PlayerUtil.getTargetedEntity(mc.gameRenderer.method_32796().toDouble(), Rotation(mc.player!!), true)
                if (hitResult.isEntityHitResult()) {
                    hitResult as EntityHitResult
                    if (hitResult.entity is PlayerEntity)
                        Friends.changeFriendState((hitResult.entity as PlayerEntity).gameProfile)
                }
            }
        }
    }

}