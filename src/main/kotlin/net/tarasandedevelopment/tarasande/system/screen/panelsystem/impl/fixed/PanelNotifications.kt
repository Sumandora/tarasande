package net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.fixed

import com.mojang.blaze3d.systems.RenderSystem
import de.florianmichael.ezeasing.EzEasing
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import net.minecraft.util.StringHelper
import net.minecraft.util.math.MathHelper
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.extension.withAlpha
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.render.helper.Alignment
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList

class Notification(val text: String, val length: Long) {

    val creationTime = System.currentTimeMillis()
}


class PanelNotifications : Panel("Notifications", 100.0, FontWrapper.fontHeight().toDouble()) {

    private var alert = false

    companion object {
        val notifications = CopyOnWriteArrayList<Notification>()

        fun notify(text: String) {
            val notificationPanel = TarasandeMain.managerPanel().get(PanelNotifications::class.java)
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
    private val timeMode = ValueMode(this, "Time: mode", false, "Absolute", "Length per char")
    private val timeAbsoluteTime = object : ValueNumber(this, "Time: absolute time", 0.0, 10000.0, 60000.0, 500.0) {
        override fun isEnabled() = timeMode.isSelected(0)
    }
    private val timeCharLength = object : ValueNumber(this, "Time: char length", 0.0, 250.0, 500.0, 10.0) {
        override fun isEnabled() = timeMode.isSelected(1)
    }
    private val timeAlignment = ValueMode(this, "Time alignment", false, "Before", "After", "None")

    private val speedIn = ValueNumber(this, "Speed: in", 0.001, 0.005, 0.02, 0.001)
    private val speedOut = ValueNumber(this, "Speed: out", 0.001, 0.005, 0.02, 0.001)

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
            if(!animations.containsKey(it))
                return
            val animation = animations[it]!!
            if (animation > 0.05) { // hack
                val color = TarasandeMain.clientValues().accentColor.getColor().withAlpha((animation * 255).toInt())
                RenderSystem.enableBlend()
                val animatedPosition = easing.ease(animation.toFloat())
                val formattedTime = StringHelper.formatTicks(((it.length + 1000 - (System.currentTimeMillis() - it.creationTime)) / 1000 * 20).toInt())

                val text = when {
                    timeAlignment.isSelected(0) -> { Formatting.GRAY.toString() + formattedTime + " " + Formatting.RESET + it.text }
                    timeAlignment.isSelected(1) -> { it.text + " " + Formatting.GRAY + formattedTime }
                    else -> { it.text } // mode 2
                }

                when (alignment) {
                    Alignment.LEFT -> FontWrapper.textShadow(matrices, text, (x - (FontWrapper.getWidth(text) * (1.0 - animatedPosition))).toFloat(), (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                    Alignment.MIDDLE -> FontWrapper.textShadow(matrices, text, x.toFloat() + panelWidth.toFloat() / 2.0F - FontWrapper.getWidth(text).toFloat() / 2.0F, (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
                    Alignment.RIGHT -> FontWrapper.textShadow(matrices, text, (x + panelWidth - FontWrapper.getWidth(text) * animatedPosition).toFloat(), (y + titleBarHeight + FontWrapper.fontHeight() * index).toFloat(), color.rgb, offset = 0.5F)
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
            if(animation < 0.0)
                notifications.remove(notification)
            animations[notification] = MathHelper.clamp(animation, 0.0, 1.0)
        }

        return animations.any { it.value > 0.0 }.also { if (!it) alert = false }
    }

    override fun renderTitleBar(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderTitleBar(matrices, mouseX, mouseY, delta)
        var x = this.x + 5.0
        if (alignment != Alignment.RIGHT)
            x = this.x + panelWidth - 5.0

        if (alert)
            FontWrapper.textShadow(matrices, "!", x.toFloat(), y.toFloat() + titleBarHeight / 2.0F - FontWrapper.fontHeight() / 2.0F * 0.5F, Color.red.rgb, centered = true, scale = 0.5F)
    }
}
