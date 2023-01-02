package net.tarasandedevelopment.tarasande_chat_features.injection.mixin.clientvalue;

import net.minecraft.client.gui.screen.ChatScreen;
import net.tarasandedevelopment.tarasande_chat_features.clientvalue.ChatValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @ModifyConstant(method = "init", constant = @Constant(intValue = 256))
    public int modifyMaxChatLength(int constant) {
        if (ChatValues.INSTANCE.getRemoveMaximum().isSelected(1)) {
            return Integer.MAX_VALUE;
        }
        return constant;
    }
}
