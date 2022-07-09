package su.mandora.tarasande.base.screen.menu.valuecomponent

import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.base.value.Value
import su.mandora.tarasande.screen.menu.utils.IElement
import su.mandora.tarasande.screen.menu.valuecomponent.*
import su.mandora.tarasande.value.*

class ManagerValueComponent : Manager<Pair<Class<out Value>, Class<out ValueComponent>>>() {

    init {
        add(
            Pair(ValueBoolean::class.java, ValueComponentBoolean::class.java),
            Pair(ValueBind::class.java, ValueComponentBind::class.java),
            Pair(ValueMode::class.java, ValueComponentMode::class.java),
            Pair(ValueNumber::class.java, ValueComponentNumber::class.java),
            Pair(ValueNumberRange::class.java, ValueComponentNumberRange::class.java),
            Pair(ValueText::class.java, ValueComponentText::class.java),
            Pair(ValueColor::class.java, ValueComponentColor::class.java),
            Pair(ValueRegistry::class.java, ValueComponentRegistry::class.java),
            Pair(ValueButton::class.java, ValueComponentButton::class.java)
        )
    }

    fun newInstance(value: Value): ValueComponent? {
        for (pair in list)
            if (pair.first.isInstance(value))
                return pair.second.declaredConstructors[0].newInstance(value)
        return null
    }

}

abstract class ValueComponent(val value: Value) : IElement {
    var width = 0.0
}