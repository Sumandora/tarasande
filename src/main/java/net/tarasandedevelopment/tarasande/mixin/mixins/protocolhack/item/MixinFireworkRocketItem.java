/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 6/24/22, 8:17 PM
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.item;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireworkRocketItem.class)
public class MixinFireworkRocketItem {

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isFallFlying()Z", ordinal = 0))
    private boolean disableFireworkElytraBoost(PlayerEntity player) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_11))
            return false;
        return player.isFallFlying();
    }
}
