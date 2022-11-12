package net.tarasandedevelopment.tarasande.mixin.mixins.features.module.norender;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(SignBlockEntityRenderer.class)
public class MixinSignBlockEntityRenderer {

    @Redirect(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/entity/SignBlockEntity;updateSign(ZLjava/util/function/Function;)[Lnet/minecraft/text/OrderedText;"))
    public OrderedText[] noRender_render(SignBlockEntity instance, boolean filterText, Function<Text, OrderedText> textOrderingFunction) {
        if (TarasandeMain.Companion.get().getModuleSystem().get(ModuleNoRender.class).getWorld().getSignText().should()) {
            return null;
        }
        return instance.updateSign(filterText, textOrderingFunction);
    }

    @ModifyConstant(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            constant = @Constant(intValue = 4))
    public int noRender_render_signLength(int constant) {
        if (TarasandeMain.Companion.get().getModuleSystem().get(ModuleNoRender.class).getWorld().getSignText().should()) {
            return 0;
        }
        return constant;
    }
}
