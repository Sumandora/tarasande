package net.tarasandedevelopment.tarasande.mixin.mixins.event;

import net.minecraft.client.font.TextVisitFactory;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventTextVisit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextVisitFactory.class)
public class MixinTextVisitFactory {

    @ModifyVariable(method = "visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", at = @At("LOAD"), argsOnly = true, ordinal = 0)
    private static String hookEventTextVisit(String value) {
        EventTextVisit eventTextVisit = new EventTextVisit(value);
        TarasandeMain.Companion.get().getEventDispatcher().call(eventTextVisit);
        return eventTextVisit.getString();
    }


}