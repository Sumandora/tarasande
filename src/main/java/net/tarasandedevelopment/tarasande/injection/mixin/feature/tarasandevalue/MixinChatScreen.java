package net.tarasandedevelopment.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.client.gui.screen.ChatScreen;
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.impl.debug.Chat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @ModifyConstant(method = "init", constant = @Constant(intValue = 256))
    public int modifyMaxChatLength(int constant) {
        if (Chat.INSTANCE.getRemoveMaximum().isSelected(1)) {
            return Integer.MAX_VALUE;
        }
        return constant;
    }
}
