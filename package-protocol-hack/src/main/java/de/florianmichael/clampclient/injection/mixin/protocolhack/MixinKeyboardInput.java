/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.clampclient.injection.mixin.protocolhack;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleSneak;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput extends Input {

    @ModifyVariable(method = "tick", at = @At(value = "LOAD", ordinal = 0), argsOnly = true)
    private boolean injectTick(boolean slowDown) {
        ModuleSneak moduleSneak = TarasandeMain.Companion.managerModule().get(ModuleSneak.class);
        if (moduleSneak.getEnabled() && moduleSneak.getDontSlowdown().getValue()) {
            return slowDown;
        }

        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_13_2)) {
            return this.sneaking;
        } else if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_14_4)) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            assert player != null;
            return !player.isSpectator() && (this.sneaking || slowDown);
        }
        return slowDown;
    }
}
