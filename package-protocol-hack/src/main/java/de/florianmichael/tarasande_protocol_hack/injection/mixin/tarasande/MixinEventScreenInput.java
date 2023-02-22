package de.florianmichael.tarasande_protocol_hack.injection.mixin.tarasande;

import de.florianmichael.tarasande_protocol_hack.injection.accessor.IEventScreenInput;
import net.tarasandedevelopment.tarasande.event.impl.EventScreenInput;
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
