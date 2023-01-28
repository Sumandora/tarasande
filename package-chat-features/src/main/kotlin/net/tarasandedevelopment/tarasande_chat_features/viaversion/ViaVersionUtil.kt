package net.tarasandedevelopment.tarasande_chat_features.viaversion

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.vialoadingbase.ViaLoadingBase

object ViaVersionUtil {

    fun isSimpleSignatures() = ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_1)
}
