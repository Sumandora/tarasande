package su.mandora.tarasande.feature.statusrenderer

import net.minecraft.client.gui.screen.Screen
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventScreenRender
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.feature.tarasandevalue.impl.AccessibilityValues
import su.mandora.tarasande.mc
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import java.util.concurrent.ConcurrentHashMap

object StatusRenderer {
    private val pendingStatuses = ConcurrentHashMap<Screen, Pair<String, TimeUtil>>()

    fun setStatus(screen: Screen, text: String) {
        if (text.isEmpty()) return
        if (pendingStatuses.containsKey(screen)) pendingStatuses.remove(screen)
        pendingStatuses[screen] = text to TimeUtil()
    }

    init {
        EventDispatcher.add(EventScreenRender::class.java) { event ->
            if (event.state == EventScreenRender.State.PRE) {
                pendingStatuses.forEach {
                    if (it.value.second.hasReached(AccessibilityValues.statusRenderTime.value.toLong())) {
                        pendingStatuses.remove(it.key)
                        return@forEach
                    }

                    if (mc.currentScreen!! == it.key) {
                        FontWrapper.textShadow(event.matrices, it.value.first, mc.window.scaledWidth / 2F, 3F, TarasandeValues.accentColor.getColor().rgb, centered = true)
                    } else {
                        pendingStatuses.remove(it.key)
                    }
                }
            }
        }
    }
}
