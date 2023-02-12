package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.directconnect.serverpinger

import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtension
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.directconnect.serverpinger.panel.PanelServerInformation
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.directconnect.serverpinger.panel.copy
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.directconnect.serverpinger.panel.emptyServer
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import su.mandora.event.EventDispatcher
import kotlin.math.ceil

class ServerPingerBase(val parent: ScreenExtension<*>, private val addressProvider: () -> String) {

    val pingTask = TimeUtil()

    private val clickableWidgetPanel = ClickableWidgetPanel(object : PanelServerInformation(parent) {
        override fun renderTitleBar(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
            super.renderTitleBar(matrices, mouseX, mouseY, delta)
            if (showProgress.value && autoPing.value) {
                ceil((delay.value - (System.currentTimeMillis() - pingTask.time)) / 1000.0).toInt().toString().also {
                    FontWrapper.textShadow(
                        matrices,
                        it,
                        (x + panelWidth - FontWrapper.getWidth(it) * 0.75F - 1.0).toFloat(),
                        y.toFloat() + titleBarHeight / 2.0F - FontWrapper.fontHeight() / 2.0F * 0.75F,
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
            }
            pingTask.reset()
        }
    }

    fun widget() = clickableWidgetPanel
}
