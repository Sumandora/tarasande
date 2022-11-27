package net.tarasandedevelopment.tarasande.injection.mixin.core.screen;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.tarasandedevelopment.tarasande.injection.accessor.IMultiplayerServerListWidgetSubServerEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class MixinMultiplayerServerListWidgetSubServerEntry implements IMultiplayerServerListWidgetSubServerEntry {

    @Shadow @Final private ServerInfo server;

    @Unique
    private Consumer<ServerInfo> tarasande_completionConsumer;

    @Unique
    private boolean tarasande_isPinged;

    @Inject(method = "render", at = @At("RETURN"))
    public void trackCompletion(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (tarasande_isPinged && this.server.ping != -1L && this.server.ping != -2L) {
            if (tarasande_completionConsumer != null) {
                tarasande_completionConsumer.accept(server);
            }
            tarasande_isPinged = false;
        }
    }

    @Override
    public void tarasande_setCompletionConsumer(Consumer<ServerInfo> consumer) {
        this.tarasande_isPinged = true;
        this.tarasande_completionConsumer = consumer;
    }
}
