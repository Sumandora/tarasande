package de.florianmichael.tarasande_protocol_hack.tarasande.module

import de.florianmichael.vialoadingbase.util.VersionListEnum
import net.minecraft.world.GameMode
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleEveryItemOnArmor : Module("Every item on armor", "Allows you to wear every item (" + VersionListEnum.r1_8.getName() + "/" + GameMode.CREATIVE.getName() + ")", ModuleCategory.EXPLOIT)
