package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventTextVisit
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.mc

class ModuleNameProtect : Module("Name protect", "Hides your in-game name", ModuleCategory.RENDER) {

    private val protectedName = ValueText(this, "Protected name", TarasandeMain.get().name)

    private val border = "( |[^a-z]|\\b)"

    private fun replaceName(str: String, substring: String, replacement: String): String {
        val regex = Regex(substring)
        return regex.replace(str) {
            var newStr = ""

            val before = str[it.range.first]
            if (substring.first() != before)
                newStr = before.toString()


            newStr += replacement

            val after = str[it.range.last]
            if (substring.last() != after)
                newStr += after.toString()

            newStr
        }
    }

    init {
        registerEvent(EventTextVisit::class.java) { event ->
            if(mc.world == null)
                return@registerEvent
            event.string = replaceName(event.string, mc.session.profile.name, protectedName.value)

            for (pair in TarasandeMain.friends().names()) {
                event.string = replaceName(event.string, pair.key, pair.value)
            }
        }
    }
}
