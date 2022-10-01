package su.mandora.tarasande.module.render

import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventTextVisit
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueText
import java.util.function.Consumer

class ModuleNameProtect : Module("Name protect", "Hides your in-game name", ModuleCategory.RENDER) {

    private val protectedName = ValueText(this, "Protected name", TarasandeMain.get().name)
    val hidePersonalName = ValueBoolean(this, "Hide personal name", true)

    private val border = "( |[^a-z]|\\b)"

    fun replaceNames(str: String): String {
        var str = str

        str = replaceName(str, mc.session.profile.name, protectedName.value)

        for (pair in TarasandeMain.get().friends.friends) {
            str = replaceName(str, pair.first.name, pair.second ?: continue)
        }

        return str
    }

    private fun replaceName(str: String, substring: String, replacement: String): String {
        val regex = Regex(border + substring + border)
        return regex.replace(str) {
            var newStr = ""

            val before = str[it.range.first]
            if (!isAlphabetical(before))
                newStr = before.toString()


            newStr += replacement

            val after = str[it.range.last]
            if (!isAlphabetical(after))
                newStr += after.toString()

            newStr
        }
    }

    private fun isAlphabetical(c: Char): Boolean = c in 'a'..'z' || c in 'A'..'Z'

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventTextVisit)
            event.string = replaceNames(event.string)
    }

}