package su.mandora.tarasande_custom_minecraft.tarasandevalues.optimization

import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande_custom_minecraft.tarasandevalues.optimization.scoreboard.ScoreboardValues

object OptimizationValues {

    val optimizeScoreboard = ValueBoolean(this, "Optimize scoreboard", true)
    init {
        ValueButtonOwnerValues(this, "Scoreboard values", ScoreboardValues, isEnabled = { optimizeScoreboard.value })
    }

}