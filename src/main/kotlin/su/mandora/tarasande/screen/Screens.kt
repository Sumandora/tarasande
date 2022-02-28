package su.mandora.tarasande.screen

import net.minecraft.client.MinecraftClient
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventKey
import su.mandora.tarasande.event.EventMouse
import su.mandora.tarasande.screen.accountmanager.ScreenBetterAccountManager
import su.mandora.tarasande.screen.menu.ScreenMenu
import su.mandora.tarasande.screen.wheel.WheelMenu
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.player.PlayerUtil

class Screens {

    val betterScreenAccountManager = ScreenBetterAccountManager()
    val screenMenu = ScreenMenu()
    private val wheelMenu = WheelMenu()

    init {
        TarasandeMain.get().managerEvent?.add { event ->
            if (event is EventKey) {
                if (event.key == TarasandeMain.get().clientValues?.menuHotkey?.keyBind)
                    MinecraftClient.getInstance().setScreen(screenMenu)
            } else if (event is EventMouse) {
                if (MinecraftClient.getInstance().currentScreen == null)
                    if (event.button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                        if (!wheelMenu.active) {
                            val hitResult = PlayerUtil.getTargetedEntity(100.0, Rotation(MinecraftClient.getInstance().player!!))
                            if (hitResult == null || hitResult.type != HitResult.Type.ENTITY || hitResult !is EntityHitResult)
                                wheelMenu.entity = null
                            else
                                wheelMenu.entity = hitResult.entity
                            wheelMenu.active = true
                        }
                        event.setCancelled()
                    }
            }
        }
    }

}