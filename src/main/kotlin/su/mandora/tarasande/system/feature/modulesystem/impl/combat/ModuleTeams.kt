package su.mandora.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.entity.player.PlayerEntity
import su.mandora.tarasande.event.impl.EventIsEntityAttackable
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleTeams : Module("Teams", "Prevents targeting of teammates", ModuleCategory.COMBAT) {

    private val mode = ValueMode(this, "Mode", true, "Vanilla team", "Display name")
    private val displayNameMode = ValueMode(this, "Display name mode", true, "Sibling styles", "Paragraph symbols", isEnabled = { mode.isSelected(1) })

    init {
        mode.select(0)
        mode.select(1)

        displayNameMode.select(0)
        displayNameMode.select(1)
    }

    private fun isTeammate(entity: PlayerEntity): Boolean {
        if (mode.isSelected(0)) {
            if (entity.isTeammate(mc.player)) {
                return true
            }
        }

        if (mode.isSelected(1)) return run {
            val selfName = mc.inGameHud.playerListHud.getPlayerName(mc.networkHandler?.playerList?.firstOrNull { it.profile == mc.player?.gameProfile })
            val otherName = mc.inGameHud.playerListHud.getPlayerName(mc.networkHandler?.playerList?.firstOrNull { it.profile == entity.gameProfile })

            if (displayNameMode.isSelected(0)) {
                if (selfName.style == otherName.style || selfName.siblings.firstOrNull { it.style.color != null }?.style == otherName.siblings.firstOrNull { it.style.color != null }?.style) {
                    return@run true
                }
            }

            if (displayNameMode.isSelected(1)) {
                val selfTeam = selfName.string
                val otherTeam = otherName.string

                val selfIdx = selfTeam.indexOf('§')
                val otherIdx = otherTeam.indexOf('§')

                if (selfIdx + 1 <= selfTeam.length && otherIdx + 1 <= otherTeam.length)
                    if (selfTeam[selfIdx + 1] == otherTeam[otherIdx + 1])
                        return@run true
            }

            return@run false
        }

        return false
    }

    init {
        registerEvent(EventIsEntityAttackable::class.java) { event ->
            if (!event.attackable) return@registerEvent
            if (event.entity !is PlayerEntity) return@registerEvent

            if (isTeammate(event.entity))
                event.attackable = false
        }
    }
}