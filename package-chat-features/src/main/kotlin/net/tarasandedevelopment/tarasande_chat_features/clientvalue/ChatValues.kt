package net.tarasandedevelopment.tarasande_chat_features.clientvalue

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean

object ChatValues {

    val allowAllCharacters = ValueBoolean(this, "Allow all characters", true)
    val dontResetHistoryOnDisconnect = ValueBoolean(this, "Don't reset history on disconnect", true)
    val removeChatMaximum = ValueBoolean(this, "Remove chat maximum", true)
}
