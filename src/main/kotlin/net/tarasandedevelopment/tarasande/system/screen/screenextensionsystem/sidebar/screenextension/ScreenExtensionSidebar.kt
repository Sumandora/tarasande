package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.screenextension

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtension
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.ManagerSidebar
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.panel.ClickableWidgetPanelSidebar

open class ScreenExtensionSidebar<T : Screen>(screen: Class<out T>) : ScreenExtension<T>(screen) {
    val sidebar = ManagerSidebar()
    private var sidebarWidget: ClickableWidgetPanelSidebar? = null

    override fun createElements(screen: T): MutableList<Element> {
        return mutableListOf(sidebarWidget ?: sidebar.build())
    }
}