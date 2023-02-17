package de.florianmichael.tarasande_custom_minecraft.viaversion

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.vialoadingbase.ViaLoadingBase

object ViaVersionUtil {

    fun isLegacyLogin() = ViaLoadingBase.getTargetVersion().isOlderThan(ProtocolVersion.v1_7_1)
}