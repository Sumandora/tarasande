package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.entity;

import com.mojang.authlib.GameProfile;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OtherClientPlayerEntity.class)
public class MixinOtherClientPlayerEntity extends AbstractClientPlayerEntity {

    public MixinOtherClientPlayerEntity(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Inject(method = "updatePose", at = @At("HEAD"))
    public void injectUpdatePose(CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_13_2)) {
            super.updatePose();
        }
    }
}
