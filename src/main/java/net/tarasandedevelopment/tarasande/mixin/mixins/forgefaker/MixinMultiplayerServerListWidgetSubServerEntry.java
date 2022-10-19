package net.tarasandedevelopment.tarasande.mixin.mixins.forgefaker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.mixin.accessor.IServerInfo;
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

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class MixinMultiplayerServerListWidgetSubServerEntry {

    @Shadow @Final private ServerInfo server;

    @Inject(method = "render", at = @At("RETURN"))
    public void renderForgeInformation(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        final int fontHeight = MinecraftClient.getInstance().textRenderer.fontHeight;
        final IForgePayload payload = ((IServerInfo) server).getForgePayload();

        if (payload == null) return;

        final float yPos = y + (entryHeight / 2F) - fontHeight / 2F;
        final String text = MinecraftClient.getInstance().textRenderer.trimToWidth("Forge/FML Server", x);
        final int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(text) + 2;

        RenderUtil.INSTANCE.text(matrices, text, x - textWidth, yPos, Color.RED.getRGB());

        if (RenderUtil.INSTANCE.isHovered(mouseX, mouseY, x - textWidth, yPos, x - 2, yPos + fontHeight)) {
            assert MinecraftClient.getInstance().currentScreen != null;
            final List<Text> tooltip = Arrays.asList(Text.of("Left mouse for Mods: " + payload.installedMods().size()));

            if (payload instanceof ModernForgePayload modernForgePayload) {
                tooltip.add(Text.of("FML Network Version: " + modernForgePayload.getFmlNetworkVersion()));
                tooltip.add(Text.of("Right mouse for Channels: " + modernForgePayload.getChannels().size()));

                if (modernForgePayload.getChannels().size() > 0 && GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS) {
                    MinecraftClient.getInstance().setScreen(new ScreenBetterForgeModList(MinecraftClient.getInstance().currentScreen, server.address + " (Channels: " + modernForgePayload.getChannels().size() + ")", ScreenBetterForgeModList.Type.CHANNEL_LIST, ((IServerInfo) server).getForgePayload()));
                }
            }

            MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, tooltip, mouseX, mouseY);

            if (payload.installedMods().size() > 0 && GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
                MinecraftClient.getInstance().setScreen(new ScreenBetterForgeModList(MinecraftClient.getInstance().currentScreen, server.address + " (Mods: " + payload.installedMods().size() + ")", ScreenBetterForgeModList.Type.MOD_LIST, ((IServerInfo) server).getForgePayload()));
            }
        }
    }
}
