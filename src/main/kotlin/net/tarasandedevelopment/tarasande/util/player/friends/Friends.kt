package net.tarasandedevelopment.tarasande.util.player.friends

import com.mojang.authlib.GameProfile
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventIsEntityAttackable
import net.tarasandedevelopment.tarasande.event.EventPlayerListName
import net.tarasandedevelopment.tarasande.event.EventTagName
import net.tarasandedevelopment.tarasande.module.misc.ModuleNoFriends
import net.tarasandedevelopment.tarasande.module.render.ModuleNameProtect

class Friends {

    val friends = ArrayList<Pair<GameProfile, String?>>()

    init {
        TarasandeMain.get().managerEvent.also {
            it.add(EventIsEntityAttackable::class.java) {
                if (TarasandeMain.get().managerModule.get(ModuleNoFriends::class.java).enabled)
                    return@add

                if (it.attackable && it.entity is PlayerEntity)
                    if (friends.any { friend -> friend.first == it.entity.gameProfile })
                        it.attackable = false
            }
            it.add(EventTagName::class.java) {
                if (TarasandeMain.get().managerModule.get(ModuleNameProtect::class.java).enabled) // Name protect will replace the names, so this is redundant
                    return@add

                if (it.entity is PlayerEntity) {
                    val profile = (it.entity as PlayerEntity).gameProfile
                    for (friend in friends)
                        if (friend.first == profile && friend.second != null && friend.second != profile.name)
                            it.displayName = it.displayName.copy().append(Formatting.RESET.toString() + Formatting.GRAY.toString() + " (" + Formatting.WHITE.toString() + friend.second + Formatting.GRAY + ")" + Formatting.RESET /* maybe other mods are too incompetent to put this here */)
                }
            }
            it.add(EventPlayerListName::class.java) {
                if (TarasandeMain.get().managerModule.get(ModuleNameProtect::class.java).enabled) // Name protect will replace the names, so this is redundant
                    return@add

                for (friend in friends)
                    if (friend.first == it.playerListEntry.profile && friend.second != null && friend.second != it.playerListEntry.profile.name) {
                        it.displayName = it.displayName.copy().append(Formatting.RESET.toString() + Formatting.GRAY.toString() + " (" + Formatting.WHITE.toString() + friend.second + Formatting.GRAY + ")" + Formatting.RESET /* maybe other mods are too incompetent to put this here */)
                    }

            }
        }
    }

    private fun addFriend(gameProfile: GameProfile, alias: String? = null) {
        if (friends.any { it.first == gameProfile }) return
        friends.add(Pair(gameProfile, alias ?: gameProfile.name))
    }

    private fun remFriend(gameProfile: GameProfile) {
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