package net.tarasandedevelopment.tarasande.base.screen.cheatmenu.valuecomponent

import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.base.value.Value
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.Element
import net.tarasandedevelopment.tarasande.screen.cheatmenu.valuecomponent.*
import net.tarasandedevelopment.tarasande.value.*
import net.tarasandedevelopment.tarasande.value.meta.ValueButton
import net.tarasandedevelopment.tarasande.value.meta.ValueButtonItem
import net.tarasandedevelopment.tarasande.value.meta.ValueSpacer

class ManagerValueComponent : Manager<Pair<Class<out Value>, Class<out ElementValueComponent>>>() {

    val instances = ArrayList<ElementValueComponent>()

    init {
        add(
            Pair(ValueBoolean::class.java, ElementValueComponentBoolean::class.java),
            Pair(ValueBind::class.java, ElementValueComponentBind::class.java),
            Pair(ValueMode::class.java, ElementValueComponentMode::class.java),
            Pair(ValueNumber::class.java, ElementValueComponentNumber::class.java),
            Pair(ValueNumberRange::class.java, ElementValueComponentNumberRange::class.java),
            Pair(ValueText::class.java, ElementValueComponentText::class.java),
            Pair(ValueColor::class.java, ElementValueComponentColor::class.java),
            Pair(ValueRegistry::class.java, ElementValueComponentRegistry::class.java),
            Pair(ValueTextList::class.java, ElementValueComponentTextList::class.java),
            Pair(ValueButton::class.java, ElementValueComponentButton::class.java),
            Pair(ValueSpacer::class.java, ElementValueComponentSpacer::class.java),
            Pair(ValueButtonItem::class.java, ElementValueComponentButtonItem::class.java)
        )
    }

    fun newInstance(value: Value): ElementValueComponent? {
        for (pair in list)
            if (pair.first.isInstance(value))
                return (pair.second.getDeclaredConstructor(Value::class.java).newInstance(value) as ElementValueComponent).also { instances.add(it) }
        return null
    }

}

abstract class ElementValueComponent(val value: Value) : Element(0.0)