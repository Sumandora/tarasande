package su.mandora.tarasande.system.feature.modulesystem.impl.misc

import su.mandora.tarasande.feature.friend.Friends
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNoFriends : Module("No friends", "Makes you attack befriended players", ModuleCategory.MISC) {

    private val deathmatch = ValueBoolean(this, "Deathmatch", false)

    fun isActive() = enabled.value && (!deathmatch.value || mc.networkHandler!!.listedPlayerListEntries.filter { it.profile != mc.player?.gameProfile }.let { it.size == Friends.amount() && it.all { Friends.isFriend(it.profile) } })

}