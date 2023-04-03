package su.mandora.tarasande_custom_minecraft.tarasandevalues.optimization.scoreboard

import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber

object ScoreboardValues {

    val disableScoreboard = ValueBoolean(this, "Disable scoreboard", false)
    val showScoreNumber = ValueBoolean(this, "Show score number", true, isEnabled = { !disableScoreboard.value })
    val limitEntries = ValueBoolean(this, "Limit entries", true, isEnabled = { !disableScoreboard.value })
    val maxEntries = ValueNumber(this, "Max entries", 0.0, 15.0, 30.0, 1.0, isEnabled = { !disableScoreboard.value && limitEntries.value })
    val blur = ValueBoolean(this, "Blur", false, isEnabled = { !disableScoreboard.value })

}