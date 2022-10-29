package net.tarasandedevelopment.tarasande.mixin.mixins.core.skin;

import net.minecraft.client.texture.PlayerSkinProvider;
import net.tarasandedevelopment.tarasande.mixin.accessor.IPlayerSkinProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(PlayerSkinProvider.class)
public class MixinPlayerSkinProvider implements IPlayerSkinProvider {

    @Unique
    private boolean tarasande_sessionCheck = true;

    @Redirect(method = "method_4653", at = @At(value = "INVOKE", target = "Ljava/util/UUID;equals(Ljava/lang/Object;)Z"))
    public boolean ignoreSession(UUID instance, Object obj) {
        if (!tarasande_sessionCheck) {
            tarasande_sessionCheck = true;
            return false;
        }

        return instance.equals(obj);
    }

    @Override
    public void tarasande_disableSessionCheckOnce() {
        tarasande_sessionCheck = false;
    }
}
