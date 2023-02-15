package net.tarasandedevelopment.tarasande.injection.mixin.core.skin;

import net.tarasandedevelopment.tarasande.injection.accessor.IPlayerSkinProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(targets = "net.minecraft.client.texture.PlayerSkinProvider")
public class MixinPlayerSkinProvider implements IPlayerSkinProvider {

    @Unique
    boolean sessionCheck = true;

    @Redirect(method = "method_4653", at = @At(value = "INVOKE", target = "Ljava/util/UUID;equals(Ljava/lang/Object;)Z"))
    public boolean hookedEquals(UUID instance, Object obj) {
        if (!sessionCheck) {
            sessionCheck = true;
            return false;
        }

        return instance.equals(obj);
    }

    @Override
    public void disableSessionCheckOnce() {
        sessionCheck = false;
    }
}
