package su.mandora.tarasande.system.feature.modulesystem.impl.render

import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.event.impl.EventTextVisit
import su.mandora.tarasande.feature.friend.Friends
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueText
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNameProtect : Module("Name protect", "Hides your in-game name", ModuleCategory.RENDER) {

    private val protectedName = ValueText(this, "Protected name", TARASANDE_NAME)
    private val protectEveryone = ValueBoolean(this, "Protect everyone", false)
    private val checkForBoundary = ValueBoolean(this, "Check for boundary (slow)", true)
    private val protectFriends = ValueBoolean(this, "Protect friends", true)

    private val border = "( |[^a-z]|\\b)"

    private fun replaceName(str: String, substring: String, replacement: String): String {
        if (!checkForBoundary.value)
            return str.replace(substring, replacement)
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

            val protectedNames =
                if (protectEveryone.value)
                    (mc.networkHandler?.playerList ?: return@registerEvent).map { it.profile.name }.sortedByDescending { it.length }
                else
                    listOf(mc.session.username)

            for (player in protectedNames)
                event.string = replaceName(event.string, player, protectedName.value)

            if(protectFriends.value)
                for (pair in Friends.names())
                    if(pair.first != pair.second)
                        event.string = replaceName(event.string, pair.first, pair.second)
        }
    }
}
