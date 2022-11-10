package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import de.florianmichael.ezeasing.EzEasing
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import net.minecraft.util.StringHelper
import net.minecraft.util.math.MathHelper
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Alignment
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.util.extension.withAlpha
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber

class Notification(val text: String, val length: Long) {

    val creationTime = System.currentTimeMillis()
}


class PanelNotifications(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Notifications", x, y, 100.0, RenderUtil.font().fontHeight().toDouble(), background = false, resizable = false, fixed = true) {

    private var alert = false

    companion object {
        val notifications = ArrayList<Notification>()

        fun notify(text: String) {
            val notificationPanel = TarasandeMain.get().screenCheatMenu.panels.filterIsInstance(PanelNotifications::class.java).first()
            val notification = when {
                notificationPanel.timeMode.isSelected(0) -> {
                    Notification(text, notificationPanel.timeAbsoluteTime.value.toLong())
                }

                else -> { // mode 1
                    Notification(text, text.length * notificationPanel.timeCharLength.value.toLong())
                }
            }
            notifications.add(notification)
            if (!notificationPanel.opened)
                notificationPanel.alert = true
        }
    }

    private val animations = HashMap<Notification, Double>()

    private var easing = EzEasing.LINEAR

    private val speedIn = ValueNumber(this, "Speed: in", 0.001, 0.005, 0.02, 0.001)
    private val speedOut = ValueNumber(this, "Speed: out", 0.001, 0.005, 0.02, 0.001)
    private val timeMode = ValueMode(this, "Time: mode", false, "Absolute", "Length per char")
    private val timeAbsoluteTime = object : ValueNumber(this, "Time: absolute time", 0.0, 10000.0, 60000.0, 500.0) {
        override fun isEnabled() = timeMode.isSelected(0)
    }
    private val timeCharLength = object : ValueNumber(this, "Time: char length", 0.0, 250.0, 500.0, 10.0) {
        override fun isEnabled() = timeMode.isSelected(1)
    }

    private val timeAlignment = ValueMode(this, "Time alignment", false, "Before", "After", "None")
    init {
        object : ValueMode(this, "Easing function", false, *EzEasing.functionNames().toTypedArray()) {
            override fun onChange() {
                easing = EzEasing.byName(selected[0])
            }
        }
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        alert = false
        var index = 0.0
        notifications.forEach {
            val animation = animations[it]!!
            if (animation > 0.0) {
                val color = TarasandeMain.get().clientValues.accentColor.getColor().withAlpha((animation * 255).toInt())
                RenderSystem.enableBlend()
                val animatedPosition = easing.ease(animation.toFloat())
                val formattedTime = StringHelper.formatTicks(((((it.length + 1000) - (System.currentTimeMillis() - it.creationTime)) / 1000) * 20).toInt())

                val text = when {
                    timeAlignment.isSelected(0) -> { Formatting.GRAY.toString() + formattedTime + " " + Formatting.RESET + it.text }
                    timeAlignment.isSelected(1) -> { it.text + " " + Formatting.GRAY + formattedTime }
                    else -> { it.text } // mode 2
                }

                when (alignment) {
                    Alignment.LEFT -> RenderUtil.font().textShadow(matrices, text, (x - (RenderUtil.font().getWidth(text) * (1.0 - animatedPosition))).toFloat(), (y + titleBarHeight + RenderUtil.font().fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                    Alignment.MIDDLE -> RenderUtil.font().textShadow(matrices, text, x.toFloat() + panelWidth.toFloat() / 2.0f - RenderUtil.font().getWidth(text).toFloat() / 2.0f, (y + titleBarHeight + RenderUtil.font().fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                    Alignment.RIGHT -> RenderUtil.font().textShadow(matrices, text, (x + panelWidth - RenderUtil.font().getWidth(text) * animatedPosition).toFloat(), (y + titleBarHeight + RenderUtil.font().fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                }
                index += animatedPosition
            }
        }
    }

    override fun isVisible(): Boolean {
        for (notification in notifications) {
            var animation = animations.putIfAbsent(notification, 0.0)
            if (animation == null || animation.isNaN()) animation = 0.0 else {
                val diff = System.currentTimeMillis() - notification.creationTime

                if (diff >= notification.length) {
                    animation -= speedOut.value * RenderUtil.deltaTime
                } else {
                    animation += speedIn.value * RenderUtil.deltaTime
                }
            }
            animations[notification] = MathHelper.clamp(animation, 0.0, 1.0)
        }

        return animations.any { it.value > 0.01 }
    }

    override fun renderTitleBar(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderTitleBar(matrices, mouseX, mouseY, delta)
        RenderUtil.font().textShadow(matrices, "!", (x + panelWidth - 5.0).toFloat(), titleBarHeight / 2.0f - RenderUtil.font().fontHeight() / 2.0f, -1, centered = true, scale = 0.5F)
    }
}
