package su.mandora.tarasande.system.screen.screenextensionsystem

import net.minecraft.client.gui.screen.Screen
import su.mandora.tarasande.Manager
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventChildren
import su.mandora.tarasande.injection.accessor.IScreen
import su.mandora.tarasande.system.screen.panelsystem.impl.button.PanelButton
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.*
import su.mandora.tarasande.util.BUTTON_PADDING
import su.mandora.tarasande.util.DEFAULT_BUTTON_WIDTH

object ManagerScreenExtension : Manager<ScreenExtension<*>>() {

    init {
        add(
            ScreenExtensionButtonListTitleScreen(),
            ScreenExtensionButtonListDownloadingTerrainScreen(),
            ScreenExtensionButtonListDeathScreen(),
            ScreenExtensionButtonListPackScreen(),
            ScreenExtensionButtonListHandledScreen(),
            ScreenExtensionButtonListSleepingChatScreen()
        )

        EventDispatcher.add(EventChildren::class.java) { event ->
            list.filter { it.screen.isAssignableFrom(event.screen.javaClass) }.forEach {
                fun <T : Screen> ScreenExtension<T>.createElements(screen: Screen) {
                    @Suppress("UNCHECKED_CAST")
                    createElements(screen as T)
                }
                it.createElements(event.screen)
            }
        }
    }
}

abstract class ScreenExtension<T : Screen>(val screen: Class<out T>) {

    abstract fun createElements(screen: T)
}

open class ScreenExtensionButtonList<T : Screen>(screen: Class<out T>) : ScreenExtension<T>(screen) {
    class Button(
        val text: String,
        val visible: () -> Boolean = { true },
        val position: Position = Position.LEFT,
        val pressAction: (button: Int) -> Unit)

    private val buttons = ArrayList<Button>()

    fun add(button: Button) {
        buttons.add(button)
    }

    override fun createElements(screen: T) {
        for (position in Position.entries) {
            var y = BUTTON_PADDING
            for (button in buttons.filter { it.position == position }) {
                if (!button.visible()) continue

                val panelButton = PanelButton.createButtonWidget(when (position) {
                    Position.LEFT -> BUTTON_PADDING
                    Position.MIDDLE -> screen.width / 2 - DEFAULT_BUTTON_WIDTH / 2
                    Position.RIGHT -> screen.width - BUTTON_PADDING - DEFAULT_BUTTON_WIDTH
                }, y, DEFAULT_BUTTON_WIDTH, 25, button.text, button.pressAction).also {
                    (screen as IScreen).tarasande_addDrawableChild(it)
                }
                y += panelButton.height + BUTTON_PADDING
            }
        }
    }

    enum class Position {
        LEFT, MIDDLE, RIGHT
    }
}
