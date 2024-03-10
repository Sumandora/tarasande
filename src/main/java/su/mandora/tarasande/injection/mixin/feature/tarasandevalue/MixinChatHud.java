package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.Chat;

@Mixin(ChatHud.class)
public class MixinChatHud {

    @ModifyExpressionValue(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 2))
    public int removeHistoryMaximum_visibles(int original) {
        if(Chat.INSTANCE.getRemoveMaximum().isSelected(0))
            return 0;
        return original;
    }

    @ModifyExpressionValue(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 4))
    public int removeHistoryMaximum(int original) {
        if(Chat.INSTANCE.getRemoveMaximum().isSelected(0))
            return 0;
        return original;
    }
}
