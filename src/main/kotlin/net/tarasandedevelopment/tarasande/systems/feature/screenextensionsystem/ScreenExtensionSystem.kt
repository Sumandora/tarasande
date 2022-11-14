package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import su.mandora.event.EventDispatcher
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.event.EventChildren
import net.tarasandedevelopment.tarasande.protocolhack.util.ProtocolRange
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.*
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.accountmanager.screenextension.ScreenExtensionAccountManager
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.impl.button.PanelButton
import net.tarasandedevelopment.tarasande.util.render.helper.Alignment

class ManagerScreenExtension : Manager<ScreenExtension>() {

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

        EventDispatcher.add(EventChildren::class.java) { eventChildren ->
            for (alignment in Alignment.values()) {
                val xPos = when (alignment) {
                    Alignment.LEFT -> 3
                    Alignment.MIDDLE -> MinecraftClient.getInstance().window.scaledWidth / 2 - (98 / 2)
                    Alignment.RIGHT -> MinecraftClient.getInstance().window.scaledWidth - 98 - 3
                }

                list.filter { it.alignment == alignment }.filter { it.screens.any { it.isAssignableFrom(eventChildren.screen.javaClass) } }.forEachIndexed { index, screenExtension ->
                    eventChildren.add(PanelButton.createButton(xPos, 3 + (index * 30), 98, 25, screenExtension.name + (if (screenExtension.version != null) " (" + screenExtension.version + ")" else "")) {
                        screenExtension.onClick(MinecraftClient.getInstance().currentScreen!!)
                    })
                }
            }
        }
    }
}

abstract class ScreenExtension(val name: String, vararg val screens: Class<out Screen>, val version: ProtocolRange? = null, val alignment: Alignment = Alignment.LEFT) {

    abstract fun onClick(current: Screen)
}
