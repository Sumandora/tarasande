package net.tarasandedevelopment.tarasande.mixin.mixins.feature.module.norender;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.MatrixStack;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantingTableBlockEntityRenderer.class)
public class MixinEnchantingTableBlockEntityRenderer {

    @Redirect(method = "render(Lnet/minecraft/block/entity/EnchantingTableBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BookModel;renderBook(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    public void noRender_render(BookModel instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (!TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getWorld().getEnchantmentTableBook().should()) {
            instance.renderBook(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }
}
