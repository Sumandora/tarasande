package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.screen.merchant;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.protocol.platform.ViaLegacyValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MixinMerchantScreen extends HandledScreen<MerchantScreenHandler> {

    @Shadow
    private int selectedIndex;

    @Unique
    private int previousRecipeIndex;

    public MixinMerchantScreen(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void reset(CallbackInfo ci) {
        previousRecipeIndex = 0;
    }

    @Inject(method = "syncRecipeIndex", at = @At("HEAD"))
    public void smoothOutRecipeIndex(CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_13_2) && ViaLegacyValues.INSTANCE.getSmoothOutMerchantScreens().getValue()) {
            if (previousRecipeIndex != selectedIndex) {
                int direction = previousRecipeIndex < selectedIndex ? 1 : -1;
                for (int smooth = previousRecipeIndex + direction /* don't send the page we already are on */; smooth != selectedIndex; smooth += direction) {
                    System.out.println(previousRecipeIndex + " -> " + smooth + " -> " + selectedIndex);
                    client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(smooth));
                }
                previousRecipeIndex = selectedIndex;
            }
        }
    }

}
