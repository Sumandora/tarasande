package su.mandora.tarasande_protocol_spoofer.injection.mixin.resourcepackspoofer;

import net.minecraft.client.resource.ServerResourcePackProvider;
import net.minecraft.resource.ResourcePackProfile;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande_protocol_spoofer.injection.accessor.IServerResourcePackProvider;

import java.util.function.Consumer;

@Mixin(ServerResourcePackProvider.class)
public abstract class MixinServerResourcePackProvider implements IServerResourcePackProvider {

    @Unique
    private Consumer<ResourcePackProfile> tarasande_resourcePackConsumer = null;

    @Shadow
    private @Nullable ResourcePackProfile serverContainer;

    @Redirect(method = "loadServerPack(Ljava/io/File;Lnet/minecraft/resource/ResourcePackSource;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "FIELD", target = "Lnet/minecraft/client/resource/ServerResourcePackProvider;serverContainer:Lnet/minecraft/resource/ResourcePackProfile;"))
    public void hookServerContainerAssignment(ServerResourcePackProvider instance, ResourcePackProfile value) {
        if (tarasande_resourcePackConsumer != null) {
            tarasande_resourcePackConsumer.accept(value);
            tarasande_resourcePackConsumer = null;
            return;
        }
        serverContainer = value;
    }

    @Override
    public void tarasande_setResourcePackConsumer(Consumer<ResourcePackProfile> resourcePackConsumer) {
        tarasande_resourcePackConsumer = resourcePackConsumer;
    }
}
