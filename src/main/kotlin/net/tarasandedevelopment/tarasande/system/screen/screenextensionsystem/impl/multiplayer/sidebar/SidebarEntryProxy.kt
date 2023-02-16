package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.sidebar

import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.subscreen.ScreenBetterProxy
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntry

class SidebarEntryProxy : SidebarEntry("Proxy", "General") {

    val screenBetterProxy = ScreenBetterProxy()

    override fun onClick() {
        mc.setScreen(screenBetterProxy.apply { prevScreen = mc.currentScreen })
    }
}
