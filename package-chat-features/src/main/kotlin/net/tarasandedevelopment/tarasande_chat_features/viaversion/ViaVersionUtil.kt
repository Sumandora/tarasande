package net.tarasandedevelopment.tarasande_chat_features.viaversion

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.vialoadingbase.util.VersionList

object ViaVersionUtil {

    fun isSimpleSignatures() = VersionList.isOlderOrEqualTo(ProtocolVersion.v1_19_1)
}
