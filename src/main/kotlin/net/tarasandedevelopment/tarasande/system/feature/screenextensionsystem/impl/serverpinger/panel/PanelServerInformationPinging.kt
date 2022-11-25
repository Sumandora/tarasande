package net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.serverpinger.panel

import net.minecraft.client.network.ServerInfo
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.ScreenTexts
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.util.connection.AddressSaver
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper

class PanelServerInformationPinging : PanelServerInformation() {

    private val pingDelay = ValueNumber(this, "Ping delay", 100.0, 5000.0, 10000.0, 100.0)
    private val showPingProgress = ValueBoolean(this, "Show ping progress", true)
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
        if (force || timer.hasReached(pingDelay.value.toLong())) {
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
        ping()
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun renderTitleBar(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderTitleBar(matrices, mouseX, mouseY, delta)

        if (this.showPingProgress.value) {
            (((pingDelay.value + 1000) - (System.currentTimeMillis() - timer.time)) / 1000).toInt().toString().also {
                FontWrapper.textShadow(matrices, it, (x + panelWidth - FontWrapper.getWidth(it)).toFloat(), (y).toFloat())
            }
        }
    }
}

