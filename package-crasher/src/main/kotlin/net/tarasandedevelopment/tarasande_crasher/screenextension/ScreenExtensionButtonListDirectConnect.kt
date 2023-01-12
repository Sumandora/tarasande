package net.tarasandedevelopment.tarasande_crasher.screenextension

import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.network.ServerAddress
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande_crasher.crasher.ManagerCrasher
import org.lwjgl.glfw.GLFW

class ScreenExtensionButtonListDirectConnect : ScreenExtensionButtonList<DirectConnectScreen>(DirectConnectScreen::class.java) {

    init {
        for (crasher in ManagerCrasher.list) {
            val name = crasher.name + " Crasher"
            add(name, direction = Direction.RIGHT) {
                if (it == GLFW.GLFW_MOUSE_BUTTON_LEFT || it == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                    try {
                        ServerAddress.parse((mc.currentScreen as DirectConnectScreen).addressField.text).apply {
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
}
