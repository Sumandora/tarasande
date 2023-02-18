package de.florianmichael.clampclient.injection.mixin.protocolhack.screen;

import de.florianmichael.clampclient.injection.instrumentation_c_0_30.ClassicItemSelectionScreen;
import de.florianmichael.tarasande_protocol_hack.tarasande.values.ProtocolHackValues;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public class MixinCreativeInventoryScreen {

    @Inject(method = "init", at = @At("RETURN"))
    public void replaceCreativeMenu(CallbackInfo ci) {
        if (ProtocolHackValues.INSTANCE.getReplaceCreativeInventory().getValue()) {
            MinecraftClient.getInstance().setScreen(ClassicItemSelectionScreen.INSTANCE);
        }
    }
}
