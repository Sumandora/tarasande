package de.florianmichael.tarasande_litematica.injection.mixin;

import fi.dy.masa.malilib.gui.GuiBase;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(GuiBase.class)
public class MixinGuiBase extends Screen {

    protected MixinGuiBase(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void renderMinecraftWidgets(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
