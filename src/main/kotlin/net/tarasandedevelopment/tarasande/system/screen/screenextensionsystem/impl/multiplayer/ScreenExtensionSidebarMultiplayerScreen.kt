package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionSidebar
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.sidebar.SidebarEntryToggleableClientBrandSpoofer

class ScreenExtensionSidebarMultiplayerScreen : ScreenExtensionSidebar<MultiplayerScreen>(MultiplayerScreen::class.java) {

    init {
        sidebar.add(
            SidebarEntryToggleableClientBrandSpoofer()
        )
    }
}
