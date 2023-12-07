package su.mandora.tarasande_protocol_spoofer.injection.mixin.resourcepackspoofer;

import net.minecraft.client.resource.server.ServerResourcePackManager;
import net.minecraft.util.Downloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande_protocol_spoofer.injection.accessor.IServerResourcePackManager;

import java.util.Collection;
import java.util.function.Consumer;

@Mixin(ServerResourcePackManager.class)
public abstract class MixinServerResourcePackManager implements IServerResourcePackManager {

    @Unique
    private Consumer<ServerResourcePackManager.PackEntry> tarasande_resourcePackConsumer = null;

    @Inject(method = "onDownload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/server/ServerResourcePackManager;onPackChanged()V"), cancellable = true)
    public void hookServerContainerAssignment(Collection<ServerResourcePackManager.PackEntry> packs, Downloader.DownloadResult result, CallbackInfo ci) {
        if (tarasande_resourcePackConsumer != null) {
            for(ServerResourcePackManager.PackEntry entry : packs)
                tarasande_resourcePackConsumer.accept(entry);
            tarasande_resourcePackConsumer = null;
            ci.cancel();
        }
    }

    @Override
    public void tarasande_setResourcePackConsumer(Consumer<ServerResourcePackManager.PackEntry> resourcePackConsumer) {
        tarasande_resourcePackConsumer = resourcePackConsumer;
    }
}
