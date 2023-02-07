package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.sidebar

import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.ScreenBetterSlotListAccountManager
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntry

class SidebarEntryAccountManager : SidebarEntry("Account manager", "General") {

    val screenBetterSlotListAccountManager = ScreenBetterSlotListAccountManager()

    override fun onClick(mouseButton: Int) {
        mc.setScreen(screenBetterSlotListAccountManager.apply { prevScreen = mc.currentScreen })
    }
}
