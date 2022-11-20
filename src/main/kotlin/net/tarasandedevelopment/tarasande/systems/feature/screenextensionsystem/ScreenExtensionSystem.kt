package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.event.EventChildren
import net.tarasandedevelopment.tarasande.protocolhack.util.ProtocolRange
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.*
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.accountmanager.screenextension.ScreenExtensionAccountManager
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.impl.button.PanelButton
import net.tarasandedevelopment.tarasande.util.render.helper.Alignment
import su.mandora.event.EventDispatcher

class ManagerScreenExtension : Manager<ScreenExtension<*>>() {

    init {
        add(
            // Account Manager
            ScreenExtensionAccountManager(),

            // Inventory
            ScreenExtensionInventoryCraftingDupe(),
            ScreenExtensionInventoryLecternCrash(),

            // Minecraft Menus
            ScreenExtensionMinecraftMenusSleepingChat(),
            ScreenExtensionMinecraftMenusDeath(),

            // Downloading Terrain
            ScreenExtensionDownloadingTerrainCancel(),
            ScreenExtensionDownloadingTerrainCancelAndDisconnect(),

            // Handled Screens
            ScreenExtensionHandledScreensClientsideClose(),
            ScreenExtensionHandledScreensServersideClose()
        )

        list.distinctBy { it.javaClass.superclass }.forEach {
            it.creator(list.filter { internal -> internal.javaClass.superclass == it.javaClass.superclass })
        }
    }
}

abstract class ScreenExtension<T : Screen>(val name: String, vararg val screens: Class<out T>) {

    abstract fun callback(eventChildren: EventChildren)
    open fun creator(elements: List<ScreenExtension<*>>) {
        EventDispatcher.add(EventChildren::class.java) { eventChildren ->
            elements.forEach {
                it.callback(eventChildren)
            }
        }
    }
}

abstract class ScreenExtensionButton<T : Screen>(name: String, vararg screens: Class<out T>, val version: ProtocolRange? = null, private val alignment: Alignment = Alignment.LEFT) : ScreenExtension<T>(name, *screens) {

    abstract fun onClick(current: T)

    // Callback -> onClick
    override fun callback(eventChildren: EventChildren) {
        // Bypass generics
        @Suppress("UNCHECKED_CAST")
        onClick(eventChildren.screen as T)
    }

    override fun creator(elements: List<ScreenExtension<*>>) {
        EventDispatcher.add(EventChildren::class.java) { eventChildren ->
            for (alignment in Alignment.values()) {
                val xPos = when (alignment) {
                    Alignment.LEFT -> 3
                    Alignment.MIDDLE -> MinecraftClient.getInstance().window.scaledWidth / 2 - (98 / 2)
                    Alignment.RIGHT -> MinecraftClient.getInstance().window.scaledWidth - 98 - 3
                }

                elements.filterIsInstance<ScreenExtensionButton<*>>().filter { it.alignment == alignment }.filter { it.screens.any { it.isAssignableFrom(eventChildren.screen.javaClass) } }.forEachIndexed { index, screenExtension ->
                    eventChildren.add(PanelButton.createButton(xPos, 3 + (index * 30), 98, 25, screenExtension.name + (if (screenExtension.version != null) " (" + screenExtension.version + ")" else "")) {
                        screenExtension.callback(eventChildren)
                    })
                }
            }
        }
    }
}
