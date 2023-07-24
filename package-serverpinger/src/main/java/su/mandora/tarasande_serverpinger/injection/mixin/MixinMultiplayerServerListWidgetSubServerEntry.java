package su.mandora.tarasande_serverpinger.injection.mixin;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande_serverpinger.injection.accessor.IMultiplayerServerListWidgetSubServerEntry;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class MixinMultiplayerServerListWidgetSubServerEntry implements IMultiplayerServerListWidgetSubServerEntry {

    @Shadow
    @Final
    private ServerInfo server;

    @Unique
    private Function1<ServerInfo, Unit> tarasande_completionConsumer;

    @Unique
    private boolean tarasande_isPinged;

    @Inject(method = "render", at = @At("RETURN"))
    public void trackCompletion(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (tarasande_isPinged && this.server.ping != 0L && this.server.ping != -1L && this.server.ping != -2L) {
            if (tarasande_completionConsumer != null) {
                tarasande_completionConsumer.invoke(server);
            }
            tarasande_isPinged = false;
        }
    }

    @Override
    public void tarasande_setCompletionConsumer(Function1<ServerInfo, Unit> consumer) {
        this.tarasande_isPinged = true;
        this.tarasande_completionConsumer = consumer;
    }
}
