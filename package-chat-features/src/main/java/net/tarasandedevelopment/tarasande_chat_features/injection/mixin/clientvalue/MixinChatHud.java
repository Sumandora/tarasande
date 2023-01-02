package net.tarasandedevelopment.tarasande_chat_features.injection.mixin.clientvalue;

import net.minecraft.client.gui.hud.ChatHud;
import net.tarasandedevelopment.tarasande_chat_features.clientvalue.ChatValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatHud.class)
public class MixinChatHud {

    @ModifyConstant(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", constant = @Constant(intValue = 100), expect = 2)
    public int changeHistoryMaximum(int original) {
        if (ChatValues.INSTANCE.getRemoveMaximum().isSelected(0)) {
            return Integer.MAX_VALUE;
        }
        return original;
    }
}
