package de.florianmichael.tarasande_viafabricplus.viafabricplus

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.viafabricplus.settings.base.SettingGroup
import de.florianmichael.viafabricplus.settings.type_impl.ProtocolSyncBooleanSetting
import de.florianmichael.vialoadingbase.platform.ProtocolRange
import net.minecraft.world.GameMode

object TarasandeSettings : SettingGroup("Tarasande") {

    val allowEveryItemOnArmor = ProtocolSyncBooleanSetting(this, "Allow every item on armor (" + GameMode.CREATIVE.getName() + ")", ProtocolRange.andOlder(ProtocolVersion.v1_8))
}
