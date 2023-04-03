package su.mandora.tarasande.injection.mixin.event;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventRender2D;
import su.mandora.tarasande.event.impl.EventScreenRender;
import su.mandora.tarasande.event.impl.EventUpdateTargetedEntity;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;applyModelViewMatrix()V", ordinal = 0, shift = At.Shift.AFTER, remap = false))
    public void hookEventRender2D(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null && !(MinecraftClient.getInstance().currentScreen instanceof DownloadingTerrainScreen)) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            EventDispatcher.INSTANCE.call(new EventRender2D(new MatrixStack()));
        }
    }

    @Inject(method = "updateTargetedEntity", at = @At("HEAD"))
    public void hookEventUpdateTargetedEntityPre(float tickDelta, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventUpdateTargetedEntity(EventUpdateTargetedEntity.State.PRE));
    }

    @Inject(method = "updateTargetedEntity", at = @At("TAIL"))
    public void hookEventUpdateTargetedEntityPost(float tickDelta, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventUpdateTargetedEntity(EventUpdateTargetedEntity.State.POST));
    }
}
