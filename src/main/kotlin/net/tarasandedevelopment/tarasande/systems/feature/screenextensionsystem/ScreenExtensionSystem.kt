package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
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
            ScreenExtensionHandledScreensServersideClose(),

            // Server Pinger
            ScreenExtensionServerPingerDirectConnect(),
            ScreenExtensionServerPingerGameMenu()
        )

        EventDispatcher.add(EventChildren::class.java) { eventChildren ->
            list.distinctBy { it.javaClass.superclass }.forEach {
                it.creator(eventChildren.screen, list.filter { internal -> internal.javaClass.superclass == it.javaClass.superclass }).forEach {
                    eventChildren.add(it)
                }
            }
        }
    }
}

abstract class ScreenExtension<T : Screen>(val name: String, vararg val screens: Class<out T>) {

    abstract fun createElements(screen: Screen) : List<Element>
    abstract fun creator(screen: Screen, elements: List<ScreenExtension<*>>) : List<Element>
}

abstract class ScreenExtensionCustom<T : Screen>(name: String, vararg screens: Class<out T>) : ScreenExtension<T>(name, *screens) {

    override fun creator(screen: Screen, elements: List<ScreenExtension<*>>): List<Element> {
        val widgets = ArrayList<Element>()
        elements.forEach { element ->
            if (element.screens.any { it.isAssignableFrom(screen.javaClass) }) {
                widgets.addAll(element.createElements(screen))
            }
        }
        return widgets
    }
}

abstract class ScreenExtensionButton<T : Screen>(name: String, vararg screens: Class<out T>, val version: ProtocolRange? = null, private val alignment: Alignment = Alignment.LEFT) : ScreenExtension<T>(name, *screens) {

    abstract fun onClick(current: T)

    @Suppress("UNCHECKED_CAST") // trashy bypass
    private fun invoker(screen: Screen) = onClick(screen as T)

    override fun createElements(screen: Screen) = listOf<Element>()
    override fun creator(screen: Screen, elements: List<ScreenExtension<*>>): List<Element> {
        val buttons = ArrayList<Element>()

        for (alignment in Alignment.values()) {
            val xPos = when (alignment) {
                Alignment.LEFT -> 3
                Alignment.MIDDLE -> MinecraftClient.getInstance().window.scaledWidth / 2 - (98 / 2)
                Alignment.RIGHT -> MinecraftClient.getInstance().window.scaledWidth - 98 - 3
            }

            elements.filterIsInstance<ScreenExtensionButton<*>>().filter { it.alignment == alignment }.filter { it.screens.any { it.isAssignableFrom(screen.javaClass) } }.forEachIndexed { index, screenExtension ->
                buttons.add(PanelButton.createButton(xPos, 3 + (index * 30), 98, 25, screenExtension.name + (if (screenExtension.version != null) " (" + screenExtension.version + ")" else "")) {
                    screenExtension.invoker(screen)
                })
            }
        }

        return buttons
    }
}
