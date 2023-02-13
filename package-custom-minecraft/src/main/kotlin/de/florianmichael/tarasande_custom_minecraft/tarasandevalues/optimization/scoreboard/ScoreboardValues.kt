package de.florianmichael.tarasande_custom_minecraft.tarasandevalues.optimization.scoreboard

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber

object ScoreboardValues {

    val showScoreNumber = ValueBoolean(this, "Show score number", true)
    val limitEntries = ValueBoolean(this, "Limit entries", true)
    val maxEntries = ValueNumber(this, "Max entries", 0.0, 15.0, 30.0, 1.0)
    val blur = ValueBoolean(this, "Blur", false)

}