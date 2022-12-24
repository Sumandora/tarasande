package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.event.EventChildren
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.button.PanelButton
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.*
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.directconnect.ScreenExtensionButtonListDirectConnect
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.directconnect.ScreenExtensionDirectConnectScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionButtonListMultiplayerScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.ManagerEntrySidebarPanel
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.panel.ClickableWidgetPanelSidebar
import su.mandora.event.EventDispatcher

class ManagerScreenExtension : Manager<ScreenExtension<*>>() {

    init {
        add(
            ScreenExtensionSidebarMultiplayerScreen(),
            ScreenExtensionButtonListMultiplayerScreen(),
            ScreenExtensionDownloadingTerrainScreen(),
            ScreenExtensionButtonListDeathScreen(),
            ScreenExtensionButtonListPackScreen(),
            ScreenExtensionButtonListHandledScreen(),
            ScreenExtensionButtonListSleepingChatScreen(),
            ScreenExtensionDirectConnectScreen(),
            ScreenExtensionGameMenuScreen(),
            ScreenExtensionButtonListTitleScreen(),
            ScreenExtensionButtonListDirectConnect()
        )

        EventDispatcher.add(EventChildren::class.java) { eventChildren ->
            list.filter { it.screen.isAssignableFrom(eventChildren.screen.javaClass) }.forEach {
                eventChildren.elements.addAll(it.invoker(eventChildren.screen))
            }
        }
    }
}

abstract class ScreenExtension<T : Screen>(val screen: Class<out T>) {

    @Suppress("UNCHECKED_CAST") // trashy bypass
    fun invoker(screen: Screen) = createElements(screen as T)
    abstract fun createElements(screen: T): MutableList<Element>
}

open class ScreenExtensionSidebar<T : Screen>(screen: Class<out T>) : ScreenExtension<T>(screen) {
    val sidebar = ManagerEntrySidebarPanel()
    private var sidebarWidget: ClickableWidgetPanelSidebar? = null

    override fun createElements(screen: T): MutableList<Element> {
        return mutableListOf(sidebarWidget ?: sidebar.build())
    }
}

open class ScreenExtensionButtonList<T : Screen>(screen: Class<out T>) : ScreenExtension<T>(screen) {
    private val buttons = LinkedHashMap<String, Pair<() -> Boolean, () -> Unit>>()

    fun add(text: String, visible: () -> Boolean = { true }, pressAction: () -> Unit) {
        buttons[text] = Pair(visible, pressAction)
    }

    override fun createElements(screen: T): MutableList<Element> {
        val list = ArrayList<Element>()
        var x = 3
        var y = 3
        for (button in buttons) {
            if (!button.value.first()) continue

            list.add(PanelButton.createButton(x, y, 98, 25, button.key, button.value.second))
            x += 98 + 3
            if (x + 135 >= MinecraftClient.getInstance().window.scaledWidth) {
                x = 3
                y += 20 + 3
            }
        }
        return list
    }
}
