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

import de.florianmichael.clampclient.injection.mixininterface.IMinecraftClient_Protocol;
import net.minecraft.client.MinecraftClient;
import net.tarasandedevelopment.tarasande.event.EventScreenInput;
import de.florianmichael.tarasande_protocol_hack.injection.accessor.IEventScreenInput;
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.event.EventDispatcher;

import java.util.concurrent.ConcurrentLinkedDeque;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient implements IMinecraftClient_Protocol {

    @Unique
    private final ConcurrentLinkedDeque<Runnable> protocolhack_keyboardInteractions = new ConcurrentLinkedDeque<>();

    @Unique
    private final ConcurrentLinkedDeque<Runnable> protocolhack_mouseInteractions = new ConcurrentLinkedDeque<>();

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
            ordinal = 4, shift = At.Shift.BEFORE))
    public void injectTick(CallbackInfo ci) {
        if (!ProtocolHackValues.INSTANCE.getExecuteInputsInSync().getValue()) return;

        while (!protocolhack_mouseInteractions.isEmpty()) {
            protocolhack_mouseInteractions.poll().run();
        }

        EventScreenInput eventScreenInput = new EventScreenInput(false);
        ((IEventScreenInput) (Object) eventScreenInput).setOriginal(false);
        EventDispatcher.INSTANCE.call(eventScreenInput);

        while (!protocolhack_keyboardInteractions.isEmpty()) {
            protocolhack_keyboardInteractions.poll().run();
        }
    }

    @Override
    public void protocolhack_trackKeyboardInteraction(Runnable interaction) {
        this.protocolhack_keyboardInteractions.add(interaction);
    }

    @Override
    public void protocolhack_trackMouseInteraction(Runnable interaction) {
        this.protocolhack_mouseInteractions.add(interaction);
    }
}
