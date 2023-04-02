package su.mandora.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;

@Mixin(SignBlockEntityRenderer.class)
public class MixinSignBlockEntityRenderer {

    @Inject(method = "renderText", at = @At("HEAD"), cancellable = true)
    public void noRender_RenderText(SignBlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider verticesProvider, int light, float scale, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getWorld().getSignText().should()) {
            ci.cancel();
        }
    }
}
