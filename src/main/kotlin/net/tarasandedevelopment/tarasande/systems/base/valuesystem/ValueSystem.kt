package net.tarasandedevelopment.tarasande.systems.base.valuesystem

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.systems.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.file.impl.FileValuesBinds
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.file.impl.FileValuesNonBinds
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.ElementValueComponent
import su.mandora.event.EventDispatcher

class ManagerValue(fileSystem: ManagerFile) : Manager<Value>() {

    init {
        EventDispatcher.add(EventSuccessfulLoad::class.java, 9999) {
            fileSystem.add(FileValuesBinds(), FileValuesNonBinds())
        }
    }

    fun getValues(owner: Any): ArrayList<Value> {
        val arrayList = ArrayList<Value>()
        for (value in list)
            if (value.owner == owner)
                arrayList.add(value)
        return arrayList
    }
}

abstract class Value(var owner: Any, var name: String, private val valueComponent: Class<out ElementValueComponent>, manage: Boolean = true) {

    init {
        if (manage)
            TarasandeMain.managerValue().add(this)
    }

    open fun isEnabled() = true
    open fun onChange() {}

    abstract fun save(): JsonElement?
    abstract fun load(jsonElement: JsonElement)

    fun createValueComponent() = valueComponent.getDeclaredConstructor(Value::class.java).newInstance(this)!!
}