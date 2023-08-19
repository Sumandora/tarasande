package su.mandora.tarasande.system.base.valuesystem

import com.google.gson.JsonElement
import su.mandora.tarasande.Manager
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.system.base.filesystem.ManagerFile
import su.mandora.tarasande.system.base.valuesystem.file.FileValues
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent

object ManagerValue : Manager<Value>() {

    private var closed = false

    init {
        EventDispatcher.add(EventSuccessfulLoad::class.java, 9999) {
            for (value in list) {
                if (list.filter { it != value }.any { it.name == value.name && it.owner.javaClass.name == value.owner.javaClass.name })
                    error("Name-and-owner-clash value registered (" + value.owner.javaClass.name + " -> " + value.name + ")")
            }
            ManagerFile.add(FileValues())
            closed = true
        }
    }

    override fun insert(obj: Value, index: Int) {
        if (closed)
            error("ValueSystem is closed")
        super.insert(obj, index)
    }

    fun getValues(owner: Any): ArrayList<Value> {
        val arrayList = ArrayList<Value>()
        for (value in list)
            if (value.owner == owner)
                arrayList.add(value)
        return arrayList
    }
}

@Suppress("LeakingThis")
abstract class Value(var owner: Any, val name: String, val visible: Boolean, val isEnabled: () -> Boolean, private val valueComponent: Class<out ElementWidthValueComponent<*>>, manage: Boolean = true) {

    init {
        if (manage)
            ManagerValue.add(this)
    }

    abstract fun save(): JsonElement?
    abstract fun load(jsonElement: JsonElement)

    fun createValueComponent() =
        if (visible)
            valueComponent.getDeclaredConstructor(Value::class.java).newInstance(this)!!
        else
            null
}