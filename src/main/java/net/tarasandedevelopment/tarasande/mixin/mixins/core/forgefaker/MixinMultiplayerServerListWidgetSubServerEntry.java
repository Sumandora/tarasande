package net.tarasandedevelopment.tarasande.mixin.mixins.core.forgefaker;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import su.mandora.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.events.EventRenderMultiplayerEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class MixinMultiplayerServerListWidgetSubServerEntry {

    @Shadow
    @Final
    private ServerInfo server;

    @Inject(method = "render", at = @At("RETURN"))
    public void renderForgeInformation(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        matrices.push();
        matrices.translate(x, y, 0);
        EventDispatcher.INSTANCE.call(new EventRenderMultiplayerEntry(matrices, x, y, entryWidth, entryHeight, mouseX, mouseY, this.server));
        matrices.pop();
    }
}
