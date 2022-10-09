package net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack;

import net.minecraft.client.font.FontStorage;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface IFontManager_Protocol {

    Map<Identifier, FontStorage> getFontStorages();
}
