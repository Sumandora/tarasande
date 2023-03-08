package de.florianmichael.tarasande_viafabricplus.viafabricplus

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.viafabricplus.settings.SettingGroup
import de.florianmichael.viafabricplus.settings.impl.ProtocolSyncBooleanSetting
import de.florianmichael.vialoadingbase.platform.ProtocolRange
import net.minecraft.world.GameMode

object CheatingSettings : SettingGroup("Cheating") {

    val allowEveryItemOnArmor = ProtocolSyncBooleanSetting(this, "Allow every item on armor (" + GameMode.CREATIVE.getName() + ")", ProtocolRange.andOlder(ProtocolVersion.v1_8))
}
