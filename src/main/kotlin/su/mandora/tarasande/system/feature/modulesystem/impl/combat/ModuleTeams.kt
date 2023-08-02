package su.mandora.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.DyeableArmorItem
import su.mandora.tarasande.event.impl.EventIsEntityAttackable
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.string.StringUtil

class ModuleTeams : Module("Teams", "Prevents targeting of teammates", ModuleCategory.COMBAT) {

    private val mode = ValueMode(this, "Mode", true, "Vanilla team", "Display name", "Armor")
    private val displayNameMode = ValueMode(this, "Display name mode", true, "Sibling styles", "Paragraph symbols", isEnabled = { mode.isSelected(1) })
    private val armorMode = ValueMode(this, "Armor mode", false, "Any", "All", isEnabled = { mode.isSelected(2) })

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

        if (mode.isSelected(1)) if (run {
                val selfProfile = mc.networkHandler?.playerList?.firstOrNull { it.profile == mc.player?.gameProfile }
                val otherProfile = mc.networkHandler?.playerList?.firstOrNull { it.profile == entity.gameProfile }

                if (selfProfile == null || otherProfile == null) return@run false

                val selfName = mc.inGameHud.playerListHud.getPlayerName(selfProfile)
                val otherName = mc.inGameHud.playerListHud.getPlayerName(otherProfile)

                if (selfName == null || otherName == null) return@run false

                fun <T> nullSafeComp(a: T, b: T): Boolean {
                    if (a == null || b == null) return false
                    return a == b
                }

                if (displayNameMode.isSelected(0)) {
                    if (nullSafeComp(selfName.style, otherName.style)) {
                        return@run true
                    } else {
                        val selfStyle = selfName.siblings.firstOrNull { it.style.color != null }?.style
                        val otherStyle = otherName.siblings.firstOrNull { it.style.color != null }?.style
                        if (nullSafeComp(selfStyle, otherStyle)) return@run true
                    }
                }

                if (displayNameMode.isSelected(1)) {
                    val selfTeam = selfName.string
                    val otherTeam = otherName.string

                    val selfMatcher = StringUtil.colorCodePattern.matcher(selfTeam)
                    val otherMatcher = StringUtil.colorCodePattern.matcher(otherTeam)

                    if (selfMatcher.find() && otherMatcher.find()) {
                        val selfColor = selfMatcher.group(1)
                        val otherColor = otherMatcher.group(1)

                        if (selfColor == otherColor) return@run true
                    }
                }

                return@run false
            }) return true

        if (mode.isSelected(2)) if (run {
                val selfArmor = mc.player?.armorItems ?: return@run false
                val otherArmor = entity.armorItems

                val selfColors = selfArmor.mapNotNull { val item = it.item; if (item !is DyeableArmorItem) null else item.getColor(it) }
                val otherColors = otherArmor.mapNotNull { val item = it.item; if (item !is DyeableArmorItem) null else item.getColor(it) }

                if (selfColors.isEmpty() || otherColors.isEmpty()) return@run false

                when {
                    armorMode.isSelected(0) -> {
                        if (selfColors.any { otherColors.contains(it) }) return@run true
                    }

                    armorMode.isSelected(1) -> {
                        if (selfColors.all { otherColors.contains(it) }) return@run true
                    }
                }

                return@run false
            }) return true

        return false
    }

    init {
        registerEvent(EventIsEntityAttackable::class.java) { event ->
            if (!event.attackable) return@registerEvent
            if (event.entity !is PlayerEntity) return@registerEvent

            if (isTeammate(event.entity)) event.attackable = false
        }
    }
}