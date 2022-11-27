package net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger.panel

import net.minecraft.client.network.ServerInfo
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.ScreenTexts
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.util.connection.AddressSaver
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import java.util.function.Consumer

class PanelServerInformationPinging(consumer: Consumer<ServerInfo>) : PanelServerInformation(consumer) {

    companion object {
        private val autoPing = ValueBoolean(this, "Auto ping", false)
        private val pingDelay = object : ValueNumber(this, "Ping delay", 100.0, 5000.0, 10000.0, 100.0) {
            override fun isEnabled() = autoPing.value
        }
        private val showPingProgress = object : ValueBoolean(this, "Show ping progress", true) {
            override fun isEnabled() = autoPing.value
        }
    }
    private val timer = TimeUtil()

    override fun updateServerInfo() = AddressSaver.getAddress().let {
        ServerInfo(it, it, false).apply {
            ping = 0L
            label = ScreenTexts.EMPTY
            playerCountLabel = ScreenTexts.EMPTY
            online = false
        }
    }

    override fun init() {
    }

    fun ping(force: Boolean = false) {
        if (autoPing.value && (force || timer.hasReached(pingDelay.value.toLong()))) {
            AddressSaver.getAddress().apply {
                server.name = this
                server.address = this
            }
            server.online = false
            serverEntry = createEntry()
            timer.reset()
        }
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (autoPing.value) {
            ping()
        }
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + titleBarHeight + panelHeight)) {
            val oldAutoPing = autoPing.value
            autoPing.value = true
            ping(true)
            autoPing.value = oldAutoPing
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun renderTitleBar(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderTitleBar(matrices, mouseX, mouseY, delta)

        if (showPingProgress.value && autoPing.value) {
            (((pingDelay.value + 1000) - (System.currentTimeMillis() - timer.time)) / 1000).toInt().toString().also {
                FontWrapper.textShadow(matrices, it, (x + panelWidth - FontWrapper.getWidth(it)).toFloat(), y.toFloat() + 1, scale = 0.75F)
            }
        }
    }
}
