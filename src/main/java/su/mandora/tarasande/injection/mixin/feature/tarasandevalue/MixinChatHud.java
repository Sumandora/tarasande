package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.Chat;

import java.util.List;

@Mixin(ChatHud.class)
public class MixinChatHud {

    @WrapWithCondition(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(I)Ljava/lang/Object;"))
    public boolean removeHistoryMaximum(List<?> instance, int i) {
        return !Chat.INSTANCE.getRemoveMaximum().isSelected(0);
    }
}
