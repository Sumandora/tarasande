package su.mandora.tarasande_server_pinger.base

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.network.ServerInfo
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtension
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande_server_pinger.base.panel.PanelServerInformation
import su.mandora.tarasande_server_pinger.base.panel.copy
import su.mandora.tarasande_server_pinger.base.panel.emptyServer
import kotlin.math.ceil

class ServerPingerBase(val parent: ScreenExtension<*>, private val addressProvider: () -> String, private val finish: (server: ServerInfo) -> Unit) {

    val pingTask = TimeUtil()

    private val clickableWidgetPanel = ClickableWidgetPanel(object : PanelServerInformation(parent, finish) {
        override fun renderTitleBar(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
            super.renderTitleBar(context, mouseX, mouseY, delta)
            if (showProgress.value && autoPing.value) {
                ceil((delay.value - (System.currentTimeMillis() - pingTask.time)) / 1000.0).toInt().toString().also {
                    FontWrapper.textShadow(
                        context,
                        it,
                        (x + panelWidth - FontWrapper.getWidth(it) * 0.75F - 1.0).toFloat(),
                        y.toFloat() + titleBarHeight / 2F - FontWrapper.fontHeight() / 2F * 0.75F,
                        scale = 0.75F
                    )
                }
            }
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            if (RenderUtil.isHovered(mouseX, mouseY, x, y + titleBarHeight, x + panelWidth, y + panelHeight)) {
                ping(true)
                return true
            }
            return super.mouseClicked(mouseX, mouseY, button)
        }
    }, true)

    private val autoPing = ValueBoolean(parent, "Auto ping", false)
    val delay = ValueNumber(parent, "Delay", 100.0, 5000.0, 10000.0, 100.0, isEnabled = { autoPing.value })
    private val showProgress = ValueBoolean(parent, "Show progress", true, isEnabled = { autoPing.value })

    init {
        EventDispatcher.add(EventTick::class.java) {
            mc.currentScreen?.apply {
                if (javaClass.isAssignableFrom(parent.screen) && autoPing.value) {
                    ping()
                }
            }
        }
    }

    private fun ping(force: Boolean = false) {
        if (force || (autoPing.value && pingTask.hasReached(delay.value.toLong()))) {
            (clickableWidgetPanel.panel as PanelServerInformation).apply {
                server = emptyServer.copy().apply {
                    name = addressProvider()
                    address = name
                }
                createEntry()
                finish(server)
            }
            pingTask.reset()
        }
    }

    fun widget() = clickableWidgetPanel
}
