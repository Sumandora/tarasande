package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.sidebar.SidebarEntryToggleableClientBrandSpoofer
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.sidebar.SidebarEntryToggleableResourcePackSpoofer
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.screenextension.ScreenExtensionSidebar

class ScreenExtensionSidebarMultiplayerScreen : ScreenExtensionSidebar<MultiplayerScreen>(MultiplayerScreen::class.java) {

    init {
        sidebar.add(
            SidebarEntryToggleableClientBrandSpoofer(),
            SidebarEntryToggleableResourcePackSpoofer()
        )
    }
}
