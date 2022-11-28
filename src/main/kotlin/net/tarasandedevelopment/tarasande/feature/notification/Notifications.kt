package net.tarasandedevelopment.tarasande.feature.notification

import de.florianmichael.ezeasing.EzEasing
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.feature.notification.panel.PanelNotifications
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import java.util.concurrent.CopyOnWriteArrayList

class Notifications {

    private val panel = PanelNotifications(this)

    val list = CopyOnWriteArrayList<Notification>()
    var alert = false
    var easing = EzEasing.LINEAR

    val time = ValueMode(this, "Time", false, "Off", "Before", "After")
    val timeMode = object : ValueMode(this, "Time: mode", false, "Absolute", "Length per char") {
        override fun isEnabled() = time.anySelected() && !time.isSelected(0)
    }
    private val timeAbsoluteTime = object : ValueNumber(this, "Time: absolute time", 0.0, 10000.0, 60000.0, 500.0) {
        override fun isEnabled() = timeMode.isSelected(0)
    }
    private val timeCharLength = object : ValueNumber(this, "Time: char length", 0.0, 250.0, 500.0, 10.0) {
        override fun isEnabled() = timeMode.isSelected(1)
    }

    val speedIn = ValueNumber(this, "Speed: in", 0.001, 0.005, 0.02, 0.001)
    val speedOut = ValueNumber(this, "Speed: out", 0.001, 0.005, 0.02, 0.001)

    init {
        object : ValueMode(this, "Easing function", false, *EzEasing.functionNames().toTypedArray()) {
            override fun onChange() {
                easing = EzEasing.byName(selected[0])
            }
        }
        TarasandeMain.managerPanel().add(panel)
    }

    fun notify(text: String) {
        val notification = when {
            timeMode.isSelected(0) -> {
                Notification(text, timeAbsoluteTime.value.toLong())
            }

            else -> { // mode 1
                Notification(text, text.length * timeCharLength.value.toLong())
            }
        }
        list.add(notification)

        if (!panel.opened) {
            alert = true
        }
    }
}
