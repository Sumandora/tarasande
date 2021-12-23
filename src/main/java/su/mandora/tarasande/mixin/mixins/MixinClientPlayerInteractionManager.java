package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.mixin.accessor.IClientPlayerInteractionManager;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager implements IClientPlayerInteractionManager {

    boolean onlyPackets = false;

    @Redirect(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;isCoolingDown(Lnet/minecraft/item/Item;)Z"))
    public boolean hookedIsCoolingDown(ItemCooldownManager itemCooldownManager, Item item) {
        if(onlyPackets)
            return true;
        return itemCooldownManager.isCoolingDown(item);
    }

    @Override
    public void setOnlyPackets(boolean onlyPackets) {
        this.onlyPackets = onlyPackets;
    }
}
