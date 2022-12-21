package de.florianmichael.tarasande_protocol_spoofer.spoofer

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueTextList
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.EntrySidebarPanelToggleable
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.ManagerEntrySidebarPanel

class EntrySidebarPanelToggleablePluginMessageFilter(sidebar: ManagerEntrySidebarPanel) : EntrySidebarPanelToggleable(sidebar, "Plugin message filter", "Spoofer") {

    val channels = ValueTextList(this, "Channels", mutableListOf("fabric"))
}