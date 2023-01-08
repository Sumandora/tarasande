package net.tarasandedevelopment.tarasande.feature.notification.panel

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import net.minecraft.util.StringHelper
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.feature.notification.Notification
import net.tarasandedevelopment.tarasande.feature.notification.Notifications
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.withAlpha
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.render.helper.Alignment
import java.awt.Color


class PanelNotifications(private val notifications: Notifications) : Panel("Notifications", 100.0, FontWrapper.fontHeight().toDouble()) {

    private val animations = HashMap<Notification, Double>()

    override fun getValueOwner() = notifications

    override fun renderContent(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        notifications.alert = false
        var index = 0.0
        notifications.list.forEach {
            if (!animations.containsKey(it))
                return
            val animation = animations[it]!!
            if (animation > notifications.speedIn.min) { // hack
                val color = TarasandeMain.clientValues().accentColor.getColor().withAlpha((animation * 255).toInt())
                RenderSystem.enableBlend()
                val animatedPosition = notifications.easing.ease(animation.toFloat())
                val formattedTime = StringHelper.formatTicks(((it.length + 1000 - (System.currentTimeMillis() - it.creationTime)) / 1000 * 20).toInt())

                val text = when {
                    notifications.time.isSelected(1) -> {
                        Formatting.GRAY.toString() + formattedTime + " " + Formatting.RESET + it.text
                    }

                    notifications.time.isSelected(2) -> {
                        it.text + " " + Formatting.GRAY + formattedTime
                    }

                    else -> {
                        it.text
                    } // mode 2
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
        for (notification in notifications.list) {
            var animation = animations.putIfAbsent(notification, 0.0)
            if (animation == null || animation.isNaN()) animation = 0.0 else {
                val diff = System.currentTimeMillis() - notification.creationTime

                if (diff >= notification.length) {
                    animation -= notifications.speedOut.value * RenderUtil.deltaTime
                } else {
                    animation += notifications.speedIn.value * RenderUtil.deltaTime
                }
            }
            if (animation < 0.0) {
                notifications.list.remove(notification)
                animations.remove(notification)
                continue
            }
            animations[notification] = animation.coerceIn(0.0, 1.0)
        }

        return animations.any { it.value > 0.0 }.also { if (!it) notifications.alert = false }
    }

    override fun renderTitleBar(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderTitleBar(matrices, mouseX, mouseY, delta)
        var x = this.x + 5.0
        if (alignment != Alignment.RIGHT)
            x = this.x + panelWidth - 5.0

        if (notifications.alert)
            FontWrapper.textShadow(matrices, "!", x.toFloat(), y.toFloat() + titleBarHeight / 2.0F - FontWrapper.fontHeight() / 2.0F * 0.5F, Color.red.rgb, centered = true, scale = 0.5F)
    }
}
