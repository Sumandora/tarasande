package su.mandora.tarasande.util.player.friends

import com.mojang.authlib.GameProfile
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Formatting
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventIsEntityAttackable
import su.mandora.tarasande.event.EventPlayerListName
import su.mandora.tarasande.event.EventTagName
import su.mandora.tarasande.module.misc.ModuleNoFriends

class Friends {

    private val friends = ArrayList<Pair<GameProfile, String?>>()

    init {
        TarasandeMain.get().managerEvent?.add { event ->
            when (event) {
                is EventIsEntityAttackable -> {
                    if(TarasandeMain.get().managerModule?.get(ModuleNoFriends::class.java)?.enabled == true)
                        return@add
                    if (event.attackable && event.entity != null && event.entity is PlayerEntity)
                        if (friends.any { it.first == event.entity.gameProfile })
                            event.attackable = false
                }

                is EventTagName -> {
                    if (event.entity is PlayerEntity) {
                        val profile = (event.entity as PlayerEntity).gameProfile
                        for (friend in friends)
                            if (friend.first == profile && friend.second != null && friend.second != profile.name)
                                event.displayName = event.displayName.copy().append(Formatting.RESET.toString() + Formatting.GRAY.toString() + " (" + Formatting.WHITE.toString() + friend.second + Formatting.GRAY + ")" + Formatting.RESET /* maybe other mods are too incompetent to put this here */)
                    }
                }

                is EventPlayerListName -> {
                    for (friend in friends)
                        if (friend.first == event.playerListEntry.profile && friend.second != null && friend.second != event.playerListEntry.profile.name)
                            event.displayName = event.displayName.copy().append(Formatting.RESET.toString() + Formatting.GRAY.toString() + " (" + Formatting.WHITE.toString() + friend.second + Formatting.GRAY + ")" + Formatting.RESET /* maybe other mods are too incompetent to put this here */)
                }
            }
        }
    }

    fun addFriend(gameProfile: GameProfile, alias: String? = null) {
        if (friends.any { it.first == gameProfile }) return
        friends.add(Pair(gameProfile, alias ?: gameProfile.name))
    }

    fun remFriend(gameProfile: GameProfile) {
        friends.removeIf { it.first == gameProfile }
    }

    fun clear() = friends.clear()

    fun isFriend(gameProfile: GameProfile) = friends.any { it.first == gameProfile }

    fun changeFriendState(gameProfile: GameProfile, alias: String? = null) {
        if (isFriend(gameProfile)) remFriend(gameProfile)
        else addFriend(gameProfile, alias)
    }

    fun setAlias(gameProfile: GameProfile, newAlias: String?) {
        if (isFriend(gameProfile)) {
            remFriend(gameProfile)
            addFriend(gameProfile, newAlias)
        }
    }

}