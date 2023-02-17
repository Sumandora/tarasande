package de.florianmichael.tarasande_crasher

import de.florianmichael.tarasande_crasher.command.CommandItemFrameCrasher
import de.florianmichael.tarasande_crasher.crasher.ManagerCrasher
import de.florianmichael.tarasande_crasher.module.*
import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.network.ServerAddress
import net.minecraft.network.Packet
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.feature.statusrenderer.StatusRenderer
import net.tarasandedevelopment.tarasande.injection.accessor.IClientConnection
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.ManagerCommand
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher

fun errorMessage(text: String) {
    @Suppress("NAME_SHADOWING") val text = Formatting.RED.toString() + text
    if (mc.currentScreen == null) {
        CustomChat.printChatMessage(text)
    } else {
        StatusRenderer.setStatus(mc.currentScreen!!, text)
    }
}

fun forcePacket(packet: Packet<*>) {
    (mc.networkHandler!!.connection as IClientConnection).tarasande_forceSend(packet)
}

class TarasandeCrasher : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerScreenExtension.add(ScreenExtensionButtonList(DirectConnectScreen::class.java).apply {
                    for (crasher in ManagerCrasher.list) {
                        val name = crasher.name + " Crasher"
                        add(name, direction = ScreenExtensionButtonList.Direction.RIGHT) {
                            if (it == GLFW.GLFW_MOUSE_BUTTON_LEFT || it == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                                try {
                                    ServerAddress.parse((mc.currentScreen as DirectConnectScreen).addressField.text).apply {
                                        crasher.crash(address, port)
                                        StatusRenderer.setStatus(mc.currentScreen!!, Formatting.GREEN.toString() + "Successfully executed crasher $name")
                                    }
                                } catch (e: Exception) {
                                    StatusRenderer.setStatus(mc.currentScreen!!, Formatting.RED.toString() + "Failed to execute crasher $name, see logs for more details")
                                    e.printStackTrace()
                                }
                            } else if (it == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                                mc.setScreen(ScreenBetterOwnerValues(name, mc.currentScreen!!, crasher))
                            }
                        }
                    }
                }
            )

            ManagerModule.add(
                ModuleShutdownDuraCrasher(),
                ModuleDDOSOnPeekExploit(),
                ModuleBoatCrasher(),
                ModuleOffHandCrasher(),
                ModuleZeroSmasher(),
                ModuleSinglePacketCrasher(),
                ModuleFlyCrasher(),
                ModulePositionCrasher(),
                ModuleSwingCrasher(),
                ModulePluginCommandCrasher(),
                ModuleParticleCrasher(),
                ModuleWalkCrasher(),
                ModuleSkriptCrasher(),
                ModuleCryptoCrasher(),
                ModuleCreativeCrasher(),
                ModuleAutoCompleteCrasher(),
                ModuleCICCrasher(),
            )

            ManagerCommand.add(
                CommandItemFrameCrasher()
            )
        }
    }
}
