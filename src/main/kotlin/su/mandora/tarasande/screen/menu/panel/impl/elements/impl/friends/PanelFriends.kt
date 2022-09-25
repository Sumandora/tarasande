package su.mandora.tarasande.screen.menu.panel.impl.elements.impl.friends

import net.minecraft.client.MinecraftClient
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.screen.menu.panel.impl.elements.PanelElements

class PanelFriends(x: Double, y: Double) : PanelElements<ElementPlayer>("Friends", x, y, 150.0, 100.0) {

    override fun tick() {
        elementList.removeIf {
            if (MinecraftClient.getInstance().networkHandler?.playerList?.none { p -> p.profile == it.gameProfile }!!) {
                it.onClose()
                return@removeIf true
            }
            false
        }
        for (player in MinecraftClient.getInstance().networkHandler?.playerList!!) {
            if (player != null && player.profile != MinecraftClient.getInstance().player?.gameProfile && elementList.none { it.gameProfile == player.profile } && player.profile.name.isNotEmpty()) {
                val elementPlayer = ElementPlayer(player.profile, 0.0)
                elementPlayer.init()
                elementList.add(elementPlayer)
            }
        }
        elementList.sortBy { !TarasandeMain.get().friends.isFriend(it.gameProfile) } // friends to top
        super.tick()
    }
}