package net.tarasandedevelopment.tarasande.feature.friend.panel

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.feature.friend.Friends
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements
import net.tarasandedevelopment.tarasande.util.extension.mc

class PanelElementsFriends(private val friends: Friends) : PanelElements<ElementWidthPlayer>("Friends", 150.0, 100.0) {

    override fun tick() {
        elementList.removeIf {
            if (mc.networkHandler?.playerList?.none { p -> p.profile == it.gameProfile }!!) {
                it.onClose()
                TarasandeMain.friends().apply {
                    if (isFriend(it.gameProfile))
                        changeFriendState(it.gameProfile)
                }
                return@removeIf true
            }
            false
        }
        for (player in mc.networkHandler?.playerList!!) {
            if (player != null && player.profile != mc.player?.gameProfile && elementList.none { it.gameProfile == player.profile } && player.profile.name.isNotEmpty()) {
                val elementWidthPlayer = ElementWidthPlayer(player.profile, 0.0)
                elementWidthPlayer.init()
                elementList.add(elementWidthPlayer)
            }
        }
        elementList.sortBy { !friends.isFriend(it.gameProfile) } // friends to top
        super.tick()
    }
}