package su.mandora.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.EntityHitResult
import su.mandora.tarasande.event.impl.EventMouse
import su.mandora.tarasande.feature.friend.Friends
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.isEntityHitResult
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.player.PlayerUtil
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