package net.tarasandedevelopment.tarasande.base.features.screenextension

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.event.EventChildren
import net.tarasandedevelopment.tarasande.features.protocol.util.ProtocolRange
import net.tarasandedevelopment.tarasande.features.screenextension.*
import net.tarasandedevelopment.tarasande.panelsystem.base.Alignment
import net.tarasandedevelopment.tarasande.panelsystem.impl.button.PanelButton

class ManagerScreenExtension : Manager<ScreenExtension>() {

    init {
        add(
            // Inventory
            ScreenExtensionInventoryCraftingDupe(),
            ScreenExtensionInventoryLecternCrash(),

            // Minecraft Menus,
            ScreenExtensionMinecraftMenusClientMenu(),
            ScreenExtensionMinecraftMenusSleepingChat(),
            ScreenExtensionMinecraftMenusDeath(),

            // Downloading Terrain
            ScreenExtensionDownloadingTerrainCancel(),
            ScreenExtensionDownloadingTerrainCancelAndDisconnect(),

            // Handled Screens
            ScreenExtensionHandledScreensClientsideClose(),
            ScreenExtensionHandledScreensServersideClose()
        )

        TarasandeMain.get().managerEvent.add(EventChildren::class.java) { eventChildren ->
            for (alignment in Alignment.values()) {
                val xPos = when (alignment) {
                    Alignment.LEFT -> 5
                    Alignment.MIDDLE -> MinecraftClient.getInstance().window.scaledWidth / 2 - (98 / 2)
                    Alignment.RIGHT -> MinecraftClient.getInstance().window.scaledWidth - 98 - 5
                }

                list.filter { it.alignment == alignment }.filter { it.screens.any { it.isAssignableFrom(eventChildren.screen.javaClass) } }.forEachIndexed { index, screenExtension ->
                    eventChildren.add(PanelButton.createButton(xPos, 5 + (index * 30), 98, 25, screenExtension.name + (if (screenExtension.version != null) " (" + screenExtension.version + ")" else "")) {
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
