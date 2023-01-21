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

package de.florianmichael.clampclient.injection.mixin.protocolhack.input;

import de.florianmichael.clampclient.injection.instrumentation_1_12_2.MouseEmulation_1_12_2;
import de.florianmichael.clampclient.injection.mixininterface.IMinecraftClient_Protocol;
import de.florianmichael.clampclient.injection.mixininterface.IMouse_Protocol;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Mouse.class, priority = 1001)
public class MixinMouse implements IMouse_Protocol {

    @Shadow @Final private MinecraftClient client;

    @Redirect(method = {"method_29615", "method_22685", "method_22684"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;execute(Ljava/lang/Runnable;)V"))
    public void redirectSync(MinecraftClient instance, Runnable runnable) {
        if (ProtocolHackValues.INSTANCE.getExecuteInputsInSync().getValue()) {
            ((IMinecraftClient_Protocol) client).protocolhack_trackMouseInteraction(runnable);
            return;
        }

        instance.execute(runnable);
    }

    @Unique
    private final MouseEmulation_1_12_2 protocolhack_mouseEmulation = new MouseEmulation_1_12_2((Mouse)(Object)this);

    @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
    public void emulateMouse(CallbackInfo ci) {
        if (ProtocolHackValues.INSTANCE.getEmulateMouseInputs().getValue()) {
            protocolhack_mouseEmulation.updateMouse();
            ci.cancel();
        }
    }

    @Override
    public MouseEmulation_1_12_2 protocolhack_getMouseEmulation() {
        return this.protocolhack_mouseEmulation;
    }
}
