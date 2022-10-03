/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 6/24/22, 8:47 PM
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
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandBlockScreen.class)
public abstract class MixinCommandBlockScreen {

    @Shadow private CyclingButtonWidget<CommandBlockBlockEntity.Type> modeButton;

    @Shadow private CyclingButtonWidget<Boolean> conditionalModeButton;

    @Shadow public abstract void updateCommandBlock();

    @Shadow private CyclingButtonWidget<Boolean> redstoneTriggerButton;

    @Inject(method = "init", at = @At("TAIL"))
    private void injectInit(CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_8)) {
            modeButton.visible = false;
            conditionalModeButton.visible = false;
            redstoneTriggerButton.visible = false;

            updateCommandBlock();
        }
    }
}
