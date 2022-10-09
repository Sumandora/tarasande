/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 7/10/22, 1:13 AM
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

package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.font;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.tarasandedevelopment.tarasande.TarasandeEntrypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/client/font/BuiltinEmptyGlyph$1")
public class MixinBuiltinEmptyGlyphSub1 {

    @Inject(method = "getWidth", at = @At("HEAD"), cancellable = true)
    public void injectGetWidth(CallbackInfoReturnable<Integer> cir) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_12_2) && !TarasandeEntrypoint.INSTANCE.getDashLoader())
            cir.setReturnValue(0);
    }

    @Inject(method = "getHeight", at = @At("RETURN"), cancellable = true)
    public void bert(CallbackInfoReturnable<Integer> cir) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_12_2) && !TarasandeEntrypoint.INSTANCE.getDashLoader())
            cir.setReturnValue(0);
    }
}
