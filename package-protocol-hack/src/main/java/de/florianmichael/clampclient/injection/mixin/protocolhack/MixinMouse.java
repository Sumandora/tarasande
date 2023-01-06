package de.florianmichael.clampclient.injection.mixin.protocolhack;

import de.florianmichael.clampclient.injection.instrumentation_1_12.MouseEmulation_1_12_2;
import de.florianmichael.clampclient.injection.mixininterface.IMouse_Protocol;
import net.minecraft.client.Mouse;
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.ProtocolHackValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DataFlowIssue")
@Mixin(Mouse.class)
public class MixinMouse implements IMouse_Protocol {

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
