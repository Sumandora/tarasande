package de.florianmichael.tarasande_crasher

import de.florianmichael.tarasande_crasher.crasher.ManagerCrasher
import de.florianmichael.tarasande_crasher.module.*
import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.network.ServerAddress
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.directconnect.ScreenExtensionButtonListDirectConnect
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.gamemenu.ScreenExtensionButtonListGameMenuScreen
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher

class TarasandeCrasher : ClientModInitializer {

    private fun invokeCrashers(screenExtension: ScreenExtensionButtonList<*>) {
        val directConnect = screenExtension.screen == DirectConnectScreen::class.java

        for (crasher in ManagerCrasher.list) {
            val name = crasher.name + " Crasher"
            screenExtension.add(name, direction = ScreenExtensionButtonList.Direction.RIGHT) {
                if (it == GLFW.GLFW_MOUSE_BUTTON_LEFT || it == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                    try {
                        ServerAddress.parse(if (directConnect) (mc.currentScreen as DirectConnectScreen).addressField.text else mc.currentServerEntry?.address ?: "").apply {
                            crasher.crash(address, port)
                        }
                    } catch (_: Exception) {
                    }
                } else if (it == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    mc.setScreen(ScreenBetterOwnerValues(name, mc.currentScreen!!, crasher))
                }
            }
        }
    }

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerScreenExtension.apply {
                invokeCrashers(get(ScreenExtensionButtonListDirectConnect::class.java))
                invokeCrashers(get(ScreenExtensionButtonListGameMenuScreen::class.java))
            }

            ManagerModule.add(
                ModuleBoatCrasher(),
                ModuleOffHandCrasher(),
                ModuleZeroSmasher(),
                ModuleSinglePacketCrasher(),
                ModuleFlyCrasher()
            )
        }
    }
}
