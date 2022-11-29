package net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.event.EventChildren
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.*
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.accountmanager.screenextension.ScreenExtensionButtonAccountManager
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.downloadingterrain.ScreenExtensionButtonCancel
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.downloadingterrain.ScreenExtensionButtonCancelAndDisconnect
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.handled.ScreenExtensionButtonClientsideClose
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.handled.ScreenExtensionButtonServersideClose
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.pack.ScreenExtensionButtonDumpServerPack
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.pack.ScreenExtensionButtonUnloadServerPack
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger.ScreenExtensionCustomDirectConnect
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger.ScreenExtensionCustomGameMenu
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.button.PanelButton
import net.tarasandedevelopment.tarasande.util.render.helper.Alignment
import su.mandora.event.EventDispatcher

class ManagerScreenExtension : Manager<ScreenExtension<*>>() {

    init {
        add(
            // Account Manager
            ScreenExtensionButtonAccountManager(),

            // Minecraft Menus
            ScreenExtensionButtonSleepingChat(),
            ScreenExtensionButtonDeath(),

            // Downloading Terrain
            ScreenExtensionButtonCancel(),
            ScreenExtensionButtonCancelAndDisconnect(),

            // Handled Screens
            ScreenExtensionButtonClientsideClose(),
            ScreenExtensionButtonServersideClose(),

            // Server Pinger
            ScreenExtensionCustomDirectConnect(),
            ScreenExtensionCustomGameMenu(),

            // Resource Packs
            ScreenExtensionButtonDumpServerPack(),
            ScreenExtensionButtonUnloadServerPack()
        )

        EventDispatcher.add(EventChildren::class.java) { eventChildren ->

            list.distinctBy { it.javaClass.superclass }.forEach {
                it.creator(eventChildren.screen, list.filter { internal -> internal.isVisible() }.filter { internal -> internal.javaClass.superclass == it.javaClass.superclass }).forEach {
                    eventChildren.add(it)
                }
            }
        }
    }
}

abstract class ScreenExtension<T : Screen>(val name: String, vararg val screens: Class<out T>) {

    abstract fun createElements(screen: Screen): List<Element>
    abstract fun creator(screen: Screen, elements: List<ScreenExtension<*>>): List<Element>

    open fun isVisible() = true
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

abstract class ScreenExtensionButton<T : Screen>(name: String, vararg screens: Class<out T>, private val alignment: Alignment = Alignment.LEFT) : ScreenExtension<T>(name, *screens) {

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
                buttons.add(PanelButton.createButton(xPos, 3 + (index * 30), 98, 25, screenExtension.name) {
                    screenExtension.invoker(screen)
                })
            }
        }

        return buttons
    }
}
