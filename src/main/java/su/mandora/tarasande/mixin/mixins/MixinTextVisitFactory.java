package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.font.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.base.module.ManagerModule;
import su.mandora.tarasande.module.render.ModuleNameProtect;

@Mixin(TextVisitFactory.class)
public class MixinTextVisitFactory {

    @ModifyVariable(method = "visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", at = @At("LOAD"), argsOnly = true, ordinal = 0)
    private static String modifyText(String value) {
        ManagerModule managerModule = TarasandeMain.Companion.get().getManagerModule();
        if (managerModule != null) {
            ModuleNameProtect moduleNameProtect = managerModule.get(ModuleNameProtect.class);
            if (moduleNameProtect.getEnabled())
                value = moduleNameProtect.replaceNames(value);
        }
        return value;
    }


}
