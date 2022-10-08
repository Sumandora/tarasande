package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.chat;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IKeyBinding_Protocol;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBinding.class)
public class MixinKeyBinding implements IKeyBinding_Protocol {
    @Shadow private InputUtil.Key boundKey;

    @Override
    public InputUtil.Key tarasande_getBoundKey() {
        return this.boundKey;
    }
}
