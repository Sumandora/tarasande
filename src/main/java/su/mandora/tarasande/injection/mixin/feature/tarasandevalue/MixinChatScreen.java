package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.Chat;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @Shadow protected TextFieldWidget chatField;

    @Inject(method = "init", at = @At("TAIL"))
    public void modifyMaxChatLength(CallbackInfo ci) {
        if (Chat.INSTANCE.getRemoveMaximum().isSelected(1)) {
            chatField.setMaxLength(Integer.MAX_VALUE);
        }
    }
}
