package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.font;

import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IFontManager_Protocol;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(FontManager.class)
public abstract class MixinFontManager implements IFontManager_Protocol {
    @Shadow @Final private Map<Identifier, FontStorage> fontStorages;

    @Override
    public Map<Identifier, FontStorage> getFontStorages() {
        return this.fontStorages;
    }
}
