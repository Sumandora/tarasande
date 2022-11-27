package net.tarasandedevelopment.tarasande.system.base.valuesystem

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.system.base.valuesystem.file.impl.FileValuesBinds
import net.tarasandedevelopment.tarasande.system.base.valuesystem.file.impl.FileValuesNonBinds
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import su.mandora.event.EventDispatcher

class ManagerValue(fileSystem: ManagerFile) : Manager<Value>() {

    var closed = false

    init {
        EventDispatcher.add(EventSuccessfulLoad::class.java, 9999) {
            for(value in list) {
                if(list.filter { it != value }.any { it.name == value.name && it.owner.javaClass.name == value.owner.javaClass.name })
                    error("Name-and-owner-clash value registered (" + value.owner.javaClass.name + " -> " + value.name + ")")
            }
            fileSystem.add(FileValuesBinds(), FileValuesNonBinds())
            closed = true
        }
    }

    override fun insert(obj: Value, index: Int) {
        if(closed)
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

abstract class Value(var owner: Any, var name: String, private val valueComponent: Class<out ElementWidthValueComponent>, manage: Boolean = true) {

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