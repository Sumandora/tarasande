package su.mandora.tarasande.system.screen.informationsystem.impl

import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.screen.informationsystem.Information

class InformationServerBrand : Information("Server", "Server Brand") {

    private val compiledRegex = Regex("\\(.*?\\) ")
    private val regex = ValueBoolean(this, "Regex", true)

    override fun getMessage(): String? {
        var brand = mc.networkHandler?.brand ?: return null
        if (regex.value)
            brand = brand.replace(compiledRegex, "")
        return brand
    }
}
