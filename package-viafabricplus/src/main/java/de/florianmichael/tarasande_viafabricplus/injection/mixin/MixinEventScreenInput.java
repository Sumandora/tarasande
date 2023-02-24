package de.florianmichael.tarasande_viafabricplus.injection.mixin;

import de.florianmichael.tarasande_viafabricplus.injection.accessor.IEventScreenInput;
import net.tarasandedevelopment.tarasande.event.impl.EventScreenInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = EventScreenInput.class, remap = false)
public class MixinEventScreenInput implements IEventScreenInput {

    @Unique
    boolean tarasande_original = true;

    @Override
    public boolean getOriginal() {
        return tarasande_original;
    }

    @Override
    public void setOriginal(boolean original) {
        tarasande_original = original;
    }
}