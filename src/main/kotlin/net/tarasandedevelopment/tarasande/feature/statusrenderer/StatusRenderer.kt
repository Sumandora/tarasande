package net.tarasandedevelopment.tarasande.feature.statusrenderer

import net.minecraft.client.gui.screen.Screen
import net.tarasandedevelopment.tarasande.event.EventScreenRender
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.TarasandeValues
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.impl.AccessibilityValues
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import su.mandora.event.EventDispatcher
import java.util.concurrent.ConcurrentHashMap

object StatusRenderer {
    private val renderer = ConcurrentHashMap<Screen, Pair<String, TimeUtil>>()

    fun setStatus(screen: Screen, text: String) {
        if (text.isEmpty()) return
        renderer[screen] = text to TimeUtil()
    }

    init {
        EventDispatcher.add(EventScreenRender::class.java) { event ->
            if (event.state == EventScreenRender.State.POST) {
                renderer.forEach {
                    if (it.value.second.hasReached(AccessibilityValues.statusRenderTime.value.toLong())) {
                        renderer.remove(it.key)
                        return@forEach
                    }

                    if (mc.currentScreen!! == it.key) {
                        FontWrapper.textShadow(event.matrices, it.value.first, mc.window.scaledWidth / 2F, 3F, TarasandeValues.accentColor.getColor().rgb, centered = true)
                    } else {
                        renderer.remove(it.key)
                    }
                }
            }
        }
    }
}
