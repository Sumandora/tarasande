package su.mandora.tarasande.injection.mixin.event.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventRenderMultiplayerEntry;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class MixinMultiplayerServerListWidget_ServerEntry {

    @Shadow
    @Final
    private ServerInfo server;

    @Shadow
    @Final
    private MultiplayerScreen screen;

    @Inject(method = "render", at = @At("RETURN"))
    public void renderForgeInformation(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        EventDispatcher.INSTANCE.call(new EventRenderMultiplayerEntry(context, x, y, entryWidth, entryHeight, mouseX, mouseY, this.server, this.screen));
        context.getMatrices().pop();
    }
}
