package net.tarasandedevelopment.tarasande.mixin.mixins.core.forgefaker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventRenderMultiplayerEntry;
import net.tarasandedevelopment.tarasande.mixin.accessor.forgefaker.IServerInfo;
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.IForgePayload;
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.modern.ModernForgePayload;
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.ui.ScreenBetterForgeModList;
import net.tarasandedevelopment.tarasande.util.render.RenderUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class MixinMultiplayerServerListWidgetSubServerEntry {

    @Shadow @Final private ServerInfo server;

    @Inject(method = "render", at = @At("RETURN"))
    public void renderForgeInformation(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        matrices.push();
        matrices.translate(x, y, 0);
        TarasandeMain.Companion.get().getEventDispatcher().call(new EventRenderMultiplayerEntry(matrices, x, y, entryWidth, entryHeight, mouseX, mouseY, this.server));
        matrices.pop();
    }
}
