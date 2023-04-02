package su.mandora.tarasande_viafabricplus.tarasande

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.minecraft.world.GameMode
import su.mandora.tarasande.feature.tarasandevalue.impl.DebugValues
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean

object EveryItemOnArmor {

    val allowEveryItemOnArmor = ValueBoolean(DebugValues, "Allow every item on armor (" + ProtocolVersion.v1_8.name + "/" + GameMode.CREATIVE.getName() + ")", false)
}
