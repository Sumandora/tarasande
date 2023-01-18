package net.tarasandedevelopment.tarasande_chat_features.clientvalue

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import net.tarasandedevelopment.tarasande_chat_features.feature.commandsystem.ManagerCommand

object ChatValues {

    init {
        ValueButtonOwnerValues(this, "Commands", ManagerCommand)
    }

    val allowAllCharacters = ValueBoolean(this, "Allow all characters", true)
    val dontResetHistoryOnDisconnect = ValueBoolean(this, "Don't reset history on disconnect", true)
    val removeMaximum = ValueMode(this, "Remove maximum", true, "History", "Message field")
}
