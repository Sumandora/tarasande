/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 6/24/22, 8:50 PM
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

package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.screen;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.screen.ingame.StructureBlockScreen$1")
public class MixinStructureBlockScreen_1 extends TextFieldWidget {

    public MixinStructureBlockScreen_1(TextRenderer textRenderer, int x, int y, int width, int height, TextFieldWidget copyFrom, Text text) {
        super(textRenderer, x, y, width, height, copyFrom, text);
    }

    @Inject(method = "charTyped(CI)Z", at = @At("HEAD"), cancellable = true)
    private void onCharTyped(char chr, int keyCode, CallbackInfoReturnable<Boolean> ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_12_2))
            ci.setReturnValue(super.charTyped(chr, keyCode));
    }
}
