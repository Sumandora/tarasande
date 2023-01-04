package net.tarasandedevelopment.tarasande_chat_features.viaversion

import de.florianmichael.vialoadingbase.ViaLoadingBase
import de.florianmichael.vialoadingbase.util.VersionListEnum

object ViaVersionUtil {

    fun isSimpleSignatures() = ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_19_1tor1_19_2)
}
