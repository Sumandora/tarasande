package su.mandora.tarasande.feature.tarasandevalue.impl.debug

import net.minecraft.world.GameRules
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.util.string.StringUtil

object MinecraftDebugger {

    val collision = ValueBoolean(this, "Collision", false)
    val chunkLoading = ValueBoolean(this, "Chunk loading", false)
    val blockOutline = ValueBoolean(this, "Block outline", false)
    val skyLight = ValueBoolean(this, "Skylight", false)
    val water = ValueBoolean(this, "Water", false)

    val ignoreRDI = ValueBoolean(this, "Ignore \"" + StringUtil.camelCaseToTitleCase(GameRules.REDUCED_DEBUG_INFO.name) + "\"", false)

}
