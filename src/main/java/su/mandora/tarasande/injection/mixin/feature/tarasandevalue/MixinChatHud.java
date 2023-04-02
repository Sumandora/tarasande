package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.Chat;

@Mixin(ChatHud.class)
public class MixinChatHud {

    @ModifyConstant(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", constant = @Constant(intValue = 100), expect = 2)
    public int changeHistoryMaximum(int original) {
        if (Chat.INSTANCE.getRemoveMaximum().isSelected(0)) {
            return Integer.MAX_VALUE;
        }
        return original;
    }
}
