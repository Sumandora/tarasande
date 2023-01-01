package net.tarasandedevelopment.tarasande_chat_features.injection.mixin.module;

import com.google.common.hash.Hashing;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ProfileKeysImpl;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande_chat_features.module.ModulePublicKeyKicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(ProfileKeysImpl.class)
public class MixinProfileKeysImpl {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Inject(method = "getKeyPair", at = @At("HEAD"), cancellable = true)
    public void revokeGateKeep(Optional<PlayerKeyPair> currentKey, CallbackInfoReturnable<CompletableFuture<Optional<PlayerKeyPair>>> cir) {
        if (TarasandeMain.Companion.managerModule().get(ModulePublicKeyKicker.class).getEnabled()) {
            cir.setReturnValue(CompletableFuture.supplyAsync(Optional::empty));
        }
    }

    @Inject(method = "saveKeyPairToFile", at = @At("HEAD"))
    public void trackGateKeepKeys(PlayerKeyPair keyPair, CallbackInfo ci) {
        if (TarasandeMain.Companion.managerModule().get(ModulePublicKeyKicker.class).getEnabled()) {
            PlayerKeyPair.CODEC.encodeStart(JsonOps.INSTANCE, keyPair).result().ifPresent(jsonElement -> {
                final String hash = Hashing.sha256().hashBytes(jsonElement.toString().getBytes(StandardCharsets.UTF_8)).toString();
                final Path folder = MinecraftClient.getInstance().runDirectory.toPath().resolve("gatekeep").resolve(MinecraftClient.getInstance().getSession().getUuid());
                //noinspection ResultOfMethodCallIgnored
                folder.toFile().mkdirs();

                try {
                    Files.writeString(folder.resolve(hash), jsonElement.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
