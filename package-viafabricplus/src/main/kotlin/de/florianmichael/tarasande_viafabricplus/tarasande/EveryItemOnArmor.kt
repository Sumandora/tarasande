package de.florianmichael.tarasande_viafabricplus.tarasande

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.minecraft.world.GameMode
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.impl.DebugValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean

object EveryItemOnArmor {

    val allowEveryItemOnArmor = ValueBoolean(DebugValues, "Allow every item on armor (" + ProtocolVersion.v1_8.name + "/" + GameMode.CREATIVE.getName() + ")", false)
}
