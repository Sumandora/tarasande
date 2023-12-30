package su.mandora.tarasande.injection.mixin.event;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventPlayerListName;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    @Inject(method = "getPlayerName", at = @At(value = "RETURN"), cancellable = true)
    public void hookEventPlayerListName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        EventPlayerListName eventPlayerListName = new EventPlayerListName(entry, cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventPlayerListName);
        cir.setReturnValue(eventPlayerListName.getDisplayName());
    }

}
