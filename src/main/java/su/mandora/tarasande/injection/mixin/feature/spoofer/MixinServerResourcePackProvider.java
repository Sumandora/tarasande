package su.mandora.tarasande.injection.mixin.feature.spoofer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ServerResourcePackProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import su.mandora.tarasande.injection.accessor.IServerResourcePackProvider;

import java.io.File;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

@Mixin(ServerResourcePackProvider.class)
public abstract class MixinServerResourcePackProvider implements IServerResourcePackProvider {

    @Shadow private @Nullable CompletableFuture<?> downloadTask;
    @Shadow @Final private ReentrantLock lock;

    @Shadow protected abstract boolean verifyFile(String expectedSha1, File file);

    @Unique
    private boolean tarasande_spoofDownloading = false;

    @Redirect(method = "download", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;submitAndJoin(Ljava/lang/Runnable;)V"))
    public void closeScreen(MinecraftClient instance, Runnable runnable) {
        if (tarasande_spoofDownloading) return;
        instance.submitAndJoin(runnable);
    }

    @Inject(method = "download", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenCompose(Ljava/util/function/Function;)Ljava/util/concurrent/CompletableFuture;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    public void spoofResourcePackDownloading(URL url, String packSha1, boolean closeAfterDownload, CallbackInfoReturnable<CompletableFuture<?>> cir, String string, String string2, MinecraftClient minecraftClient, File file, CompletableFuture<String> completableFuture) {
        if (tarasande_spoofDownloading) {
            this.downloadTask = completableFuture.thenCompose(o -> {
                if (!this.verifyFile(string2, file)) {
                    return CompletableFuture.failedFuture(new RuntimeException("Hash check failure for file " + file + ", see log"));
                } else {
                    return CompletableFuture.completedFuture("");
                }
            });
            this.lock.unlock();
            cir.setReturnValue(this.downloadTask);
        }
    }

    @Override
    public void tarasande_setSpoofLoading(boolean shouldLoad) {
        tarasande_spoofDownloading = shouldLoad;
    }
}
