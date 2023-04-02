package su.mandora.tarasande.system.feature.modulesystem.impl.render

import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.event.impl.EventTextVisit
import su.mandora.tarasande.feature.friend.Friends
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueText
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNameProtect : Module("Name protect", "Hides your in-game name", ModuleCategory.RENDER) {

    private val protectedName = ValueText(this, "Protected name", TARASANDE_NAME)

    private val border = "( |[^a-z]|\\b)"

    private fun replaceName(str: String, substring: String, replacement: String): String {
        val regex = Regex(border + substring + border)
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
            if (mc.world == null)
                return@registerEvent
            event.string = replaceName(event.string, mc.session.profile.name, protectedName.value)

            for (pair in Friends.names()) {
                event.string = replaceName(event.string, pair.key, pair.value)
            }
        }
    }
}
