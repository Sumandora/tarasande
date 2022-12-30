package net.tarasandedevelopment.tarasande.feature.clientvalue.impl

import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug.BlockChangeTracker
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues

object DebugValues {

    val blockChangeTracker = ValueButtonOwnerValues(this, "Block change tracker", BlockChangeTracker())

}