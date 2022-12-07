package net.tarasandedevelopment.tarasande.injection.mixin.core.opengl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11.*;

@Mixin(value = GameRenderer.class, priority = 1001)
public class MixinGameRenderer {
    @Shadow
    @Final
    MinecraftClient client;

    // stolen from 1.8
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;applyModelViewMatrix()V", shift = At.Shift.AFTER, remap = false))
    public void setupMatrix(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        glViewport(0, 0, client.getWindow().getWidth(), client.getWindow().getHeight());
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glClear(GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight(), 0.0, 1000.0, 3000.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0.0F, 0.0F, -2000.0F);
    }
}
