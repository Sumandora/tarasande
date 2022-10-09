package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontManager;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IMinecraftClient_Protocol;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient implements IMinecraftClient_Protocol {
    @Shadow @Final private FontManager fontManager;

    @Override
    public FontManager getFontManager() {
        return this.fontManager;
    }
}
