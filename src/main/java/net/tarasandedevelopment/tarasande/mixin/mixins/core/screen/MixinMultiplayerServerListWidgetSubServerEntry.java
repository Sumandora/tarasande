package net.tarasandedevelopment.tarasande.mixin.mixins.core.screen;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.tarasandedevelopment.tarasande.mixin.accessor.IMultiplayerServerListWidgetSubServerEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class MixinMultiplayerServerListWidgetSubServerEntry implements IMultiplayerServerListWidgetSubServerEntry {

    @Unique
    private boolean tarasande_bypassRendering;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setTooltip(Ljava/util/List;)V", shift = At.Shift.BEFORE), cancellable = true)
    public void cancelRendering(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (tarasande_bypassRendering) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    public void cancelRendering2(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (tarasande_bypassRendering) {
            ci.cancel();
        }
    }


    @Override
    public void tarasande_setBypassRendering(boolean bypassRendering) {
        this.tarasande_bypassRendering = bypassRendering;
    }
}
