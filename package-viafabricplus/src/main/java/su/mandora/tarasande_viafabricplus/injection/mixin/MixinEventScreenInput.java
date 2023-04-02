package su.mandora.tarasande_viafabricplus.injection.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import su.mandora.tarasande.event.impl.EventScreenInput;
import su.mandora.tarasande_viafabricplus.injection.accessor.IEventScreenInput;

@Mixin(value = EventScreenInput.class, remap = false)
public class MixinEventScreenInput implements IEventScreenInput {

    @Unique
    boolean tarasande_original = true;

    @Override
    public boolean tarasande_getOriginal() {
        return tarasande_original;
    }

    @Override
    public void tarasande_setOriginal(boolean original) {
        tarasande_original = original;
    }
}