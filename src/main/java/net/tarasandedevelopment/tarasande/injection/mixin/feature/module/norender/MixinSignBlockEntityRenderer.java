package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SignBlockEntityRenderer.class)
public class MixinSignBlockEntityRenderer {

    // TODO Port; WTF
//    @Redirect(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/block/entity/SignBlockEntity;updateSign(ZLjava/util/function/Function;)[Lnet/minecraft/text/OrderedText;"))
//    public OrderedText[] noRender_render(SignBlockEntity instance, boolean filterText, Function<Text, OrderedText> textOrderingFunction) {
//        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getWorld().getSignText().should()) {
//            return null;
//        }
//        return instance.updateSign(filterText, textOrderingFunction);
//    }

    @ModifyConstant(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            constant = @Constant(intValue = 4))
    public int noRender_render_signLength(int constant) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getWorld().getSignText().should()) {
            return 0;
        }
        return constant;
    }
}
