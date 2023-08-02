package su.mandora.tarasande.feature.friend.panel

import com.mojang.authlib.GameProfile
import su.mandora.tarasande.feature.friend.Friends
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.panelsystem.api.PanelElements

class PanelElementsFriends(private val friends: Friends) : PanelElements<ElementWidthPlayer>("Friends", 150.0, 100.0) {

    override fun tick() {
        val hashMap = HashMap<GameProfile, Boolean>()
        for (element in elementList) {
            hashMap[element.gameProfile] = false
        }

        for (player in mc.networkHandler?.playerList!!) {
            if (player != null && player.profile != mc.player?.gameProfile && player.profile.name.isNotEmpty()) {
                // JVM sucks! There should be references, so you can get the element and update it later
                hashMap.computeIfAbsent(player.profile) {
                    val elementWidthPlayer = ElementWidthPlayer(it, 0.0)
                    elementWidthPlayer.init()
                    elementList.add(elementWidthPlayer)
                    true
                }
                hashMap[player.profile] = true
            }
        }

        elementList.removeIf { !hashMap.getOrDefault(it.gameProfile, true) }
        elementList.sortBy { !friends.isFriend(it.gameProfile) } // friends to top
        super.tick()
    }
}