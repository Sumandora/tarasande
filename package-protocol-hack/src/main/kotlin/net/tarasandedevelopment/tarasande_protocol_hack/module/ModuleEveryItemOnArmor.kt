package net.tarasandedevelopment.tarasande_protocol_hack.module

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.minecraft.world.GameMode
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleEveryItemOnArmor : Module("Every item on armor", "Allows you to wear every item (" + ProtocolVersion.v1_8.name + "/" + GameMode.CREATIVE.getName() + ")", ModuleCategory.EXPLOIT)
