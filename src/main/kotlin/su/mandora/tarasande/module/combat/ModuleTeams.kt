package su.mandora.tarasande.module.combat

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventIsEntityAttackable
import su.mandora.tarasande.value.ValueMode
import java.util.function.Consumer

class ModuleTeams : Module("Teams", "Prevents targeting teammates", ModuleCategory.COMBAT) {

    val mode = ValueMode(this, "Mode", true, "Vanilla team", "Display name")

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventIsEntityAttackable) {
            if (!event.attackable)
                return@Consumer
            if(event.entity !is PlayerEntity)
                return@Consumer

            if (mode.isSelected(0)) {
                if (event.entity.isTeammate(mc.player)) {
                    event.attackable = false
                }
            }

            if (mode.isSelected(1)) {
                val selfTeam = mc.inGameHud.playerListHud.getPlayerName(mc.networkHandler?.playerList?.firstOrNull { it.profile == mc.player?.gameProfile } ?: return@Consumer).siblings.firstOrNull { it.style.color != null }?.style?.color ?: return@Consumer
                val otherTeam = mc.inGameHud.playerListHud.getPlayerName(mc.networkHandler?.playerList?.firstOrNull { it.profile == event.entity.gameProfile } ?: return@Consumer).siblings.firstOrNull { it.style.color != null }?.style?.color ?: return@Consumer

                if (selfTeam == otherTeam) {
                    event.attackable = false
                }
            }
        }
    }
}