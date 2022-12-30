package net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean

class MinecraftDebugger {

    val collision = ValueBoolean(this, "Collision", false)
    val chunkLoading = ValueBoolean(this, "Chunk loading", false)
    val blockOutline = ValueBoolean(this, "Block outline", false)
    val skyLight = ValueBoolean(this, "Skylight", false)
    val water = ValueBoolean(this, "Water", false)
}
