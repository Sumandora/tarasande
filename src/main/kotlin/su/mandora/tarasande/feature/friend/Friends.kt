package su.mandora.tarasande.feature.friend

import com.mojang.authlib.GameProfile
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Formatting
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventIsEntityAttackable
import su.mandora.tarasande.event.impl.EventPlayerListName
import su.mandora.tarasande.event.impl.EventTagName
import su.mandora.tarasande.feature.friend.module.ModuleNoFriends
import su.mandora.tarasande.feature.friend.panel.PanelElementsFriends
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNameProtect
import su.mandora.tarasande.system.screen.panelsystem.ManagerPanel
import su.mandora.tarasande.util.extension.javaruntime.clearAndGC

object Friends {

    private val friends = ArrayList<Pair<GameProfile, String>>()

    init {
        EventDispatcher.apply {
            val moduleNoFriends by lazy { ManagerModule.get(ModuleNoFriends::class.java) }
            add(EventIsEntityAttackable::class.java) {
                if (moduleNoFriends.isActive())
                    return@add

                if (it.attackable && it.entity is PlayerEntity)
                    if (friends.any { friend -> friend.first == it.entity.gameProfile })
                        it.attackable = false
            }
            val moduleNameProtect by lazy { ManagerModule.get(ModuleNameProtect::class.java) }
            add(EventTagName::class.java) {
                if (moduleNameProtect.enabled.value) // Name protect will replace the names, so this is redundant
                    return@add

                if (it.entity is PlayerEntity) {
                    val profile = (it.entity as PlayerEntity).gameProfile
                    for (friend in friends)
                        if (friend.first == profile && friend.second != profile.name)
                            it.displayName = it.displayName.copy().append(Formatting.RESET.toString() + Formatting.GRAY.toString() + " (" + Formatting.WHITE.toString() + friend.second + Formatting.GRAY + ")" + Formatting.RESET /* maybe other mods are too incompetent to put this here */)
                }
            }
            add(EventPlayerListName::class.java) {
                if (moduleNameProtect.enabled.value) // Name protect will replace the names, so this is redundant
                    return@add

                for (friend in friends)
                    if (friend.first == it.playerListEntry.profile && friend.second != it.playerListEntry.profile.name) {
                        it.displayName = it.displayName.copy().append(Formatting.RESET.toString() + Formatting.GRAY.toString() + " (" + Formatting.WHITE.toString() + friend.second + Formatting.GRAY + ")" + Formatting.RESET /* maybe other mods are too incompetent to put this here */)
                    }
            }
        }

        ManagerModule.add(ModuleNoFriends())
        ManagerPanel.add(PanelElementsFriends(this))
    }

    private fun addFriend(gameProfile: GameProfile, alias: String = gameProfile.name) {
        if (friends.any { it.first == gameProfile }) return
        friends.add(Pair(gameProfile, alias))
    }

    private fun remFriend(gameProfile: GameProfile) {
        friends.removeIf { it.first == gameProfile }
    }

    fun clear() = friends.clearAndGC()

    fun isFriend(gameProfile: GameProfile) = friends.any { it.first == gameProfile }

    fun changeFriendState(gameProfile: GameProfile, alias: String = gameProfile.name) {
        if (isFriend(gameProfile)) remFriend(gameProfile)
        else addFriend(gameProfile, alias)
    }

    fun setAlias(gameProfile: GameProfile, newAlias: String = gameProfile.name) {
        if (isFriend(gameProfile)) {
            remFriend(gameProfile)
            addFriend(gameProfile, newAlias)
        }
    }

    fun names() = friends.map { it.first.name to it.second }

    fun amount() = friends.size

}