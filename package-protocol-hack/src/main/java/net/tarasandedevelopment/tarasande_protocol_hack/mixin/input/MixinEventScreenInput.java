package net.tarasandedevelopment.tarasande_protocol_hack.mixin.input;

import net.tarasandedevelopment.tarasande.event.EventScreenInput;
import net.tarasandedevelopment.tarasande_protocol_hack.accessor.IEventScreenInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = EventScreenInput.class, remap = false)
public class MixinEventScreenInput implements IEventScreenInput {

    @Unique
    boolean protocolhack_original = true;

    @Override
    public boolean getOriginal() {
        return protocolhack_original;
    }

    @Override
    public void setOriginal(boolean original) {
        protocolhack_original = original;
    }
}
