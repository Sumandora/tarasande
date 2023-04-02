package su.mandora.tarasande.system.screen.screenextensionsystem

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import su.mandora.tarasande.Manager
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventChildren
import su.mandora.tarasande.system.screen.panelsystem.impl.button.PanelButton
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.*
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionButtonListMultiplayerScreen

object ManagerScreenExtension : Manager<ScreenExtension<*>>() {

    init {
        add(
            ScreenExtensionButtonListMultiplayerScreen(),
            ScreenExtensionButtonListTitleScreen(),
            ScreenExtensionDownloadingTerrainScreen(),
            ScreenExtensionButtonListDeathScreen(),
            ScreenExtensionButtonListPackScreen(),
            ScreenExtensionButtonListHandledScreen(),
            ScreenExtensionButtonListSleepingChatScreen()
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

open class ScreenExtensionButtonList<T : Screen>(screen: Class<out T>) : ScreenExtension<T>(screen) {
    private val buttons = LinkedHashMap<String, Triple<() -> Boolean, Direction, (button: Int) -> Unit>>()

    fun add(text: String, visible: () -> Boolean = { true }, direction: Direction = Direction.LEFT, pressAction: (button: Int) -> Unit) {
        buttons[text] = Triple(visible, direction, pressAction)
    }

    override fun createElements(screen: T): MutableList<Element> {
        val list = ArrayList<Element>()
        for (value in Direction.values()) {
            var y = 3
            for (button in buttons.filter { it.value.second == value }) {
                if (!button.value.first()) continue

                list.add(PanelButton.createButton(if (value == Direction.LEFT) 3 else screen.width - 98 - 3, y, 98, 25, button.key, button.value.third))
                y += 25 + 3
            }
        }

        return list
    }

    enum class Direction {
        LEFT, RIGHT
    }
}
