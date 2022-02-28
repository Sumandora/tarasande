package su.mandora.tarasande.base.value

import com.google.gson.JsonElement
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.Manager

class ManagerValue : Manager<Value>() {

    fun getValues(owner: Any): ArrayList<Value> {
        val arrayList = ArrayList<Value>()
        for (value in list)
            if (value.owner == owner)
                arrayList.add(value)
        return arrayList
    }

}

abstract class Value(var owner: Any, var name: String) {
    open fun isVisible() = true
    open fun onChange() {}

    abstract fun save(): JsonElement?
    abstract fun load(jsonElement: JsonElement)

    init {
        TarasandeMain.get().managerValue?.add(this)
    }
}