package de.florianmichael.tarasande_custom_minecraft.tarasandevalues.optimization

import de.florianmichael.tarasande_custom_minecraft.tarasandevalues.optimization.scoreboard.ScoreboardValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues

object OptimizationValues {

    val optimizeScoreboard = ValueBoolean(this, "Optimize scoreboard", true)
    init {
        ValueButtonOwnerValues(this, "Scoreboard values", ScoreboardValues)
    }

}