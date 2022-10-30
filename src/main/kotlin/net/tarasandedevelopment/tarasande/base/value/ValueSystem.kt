package net.tarasandedevelopment.tarasande.base.value

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager

class ManagerValue : Manager<Value>() {

    fun getValues(owner: Any): ArrayList<Value> {
        val arrayList = ArrayList<Value>()
        for (value in list)
            if (value.owner == owner)
                arrayList.add(value)
        return arrayList
    }

}

abstract class Value(var owner: Any, var name: String, manage: Boolean = true) {

    init {
        println(name)
    }

    init {
        if (manage)
            TarasandeMain.get().managerValue.add(this)
    }

    open fun isEnabled() = true
    open fun onChange() {}

    abstract fun save(): JsonElement?
    abstract fun load(jsonElement: JsonElement)
}