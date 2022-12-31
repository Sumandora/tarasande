package net.tarasandedevelopment.tarasande.feature.clientvalue.impl

import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug.BlockChangeTracker
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug.MinecraftDebugger
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug.PlayerMovementPrediction
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues

object DebugValues {

    val blockChangeTracker = ValueButtonOwnerValues(this, "Block change tracker", BlockChangeTracker())
    val minecraftDebugger = ValueButtonOwnerValues(this, "Minecraft debugger", MinecraftDebugger())

    init {
        ValueButtonOwnerValues(this, "Player movement prediction", PlayerMovementPrediction())
    }

    val openGLErrorDebugger = ValueBoolean(this, "OpenGL error debugger", true)
}
