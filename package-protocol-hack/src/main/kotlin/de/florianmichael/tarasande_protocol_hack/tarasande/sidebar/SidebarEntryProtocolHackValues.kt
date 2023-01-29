package de.florianmichael.tarasande_protocol_hack.tarasande.sidebar

import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues
import net.minecraft.client.gui.screen.GameMenuScreen
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntry

class SidebarEntryProtocolHackValues : SidebarEntry("Protocol Hack Values", "Protocol Hack") {

    init {
        ManagerScreenExtension.add(object : ScreenExtensionButtonList<GameMenuScreen>(GameMenuScreen::class.java) {
            init {
                "Protocol Hack Values".apply {
                    add(this, direction = Direction.RIGHT) {
                        onClick(it)
                    }
                }
            }
        })
    }

    override fun onClick(mouseButton: Int) {
        mc.setScreen(ScreenBetterOwnerValues(name, mc.currentScreen!!, ProtocolHackValues))
    }
}
