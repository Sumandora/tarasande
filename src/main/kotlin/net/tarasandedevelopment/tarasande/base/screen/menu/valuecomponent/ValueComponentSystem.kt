package net.tarasandedevelopment.tarasande.base.screen.menu.valuecomponent

import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.base.value.Value
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.Element
import net.tarasandedevelopment.tarasande.screen.menu.valuecomponent.*
import net.tarasandedevelopment.tarasande.value.*

class ManagerValueComponent : Manager<Pair<Class<out Value>, Class<out ValueComponent>>>() {

    val instances = ArrayList<ValueComponent>()

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
                return (pair.second.declaredConstructors[0].newInstance(value) as ValueComponent).also { instances.add(it) }
        return null
    }

}

abstract class ValueComponent(val value: Value) : Element(0.0)