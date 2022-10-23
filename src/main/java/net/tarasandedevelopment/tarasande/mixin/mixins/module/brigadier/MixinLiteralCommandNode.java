package net.tarasandedevelopment.tarasande.mixin.mixins.module.brigadier;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.misc.ModuleBrigadierIgnoreCase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LiteralCommandNode.class)
public class MixinLiteralCommandNode {

    @Redirect(method = "parse(Lcom/mojang/brigadier/StringReader;)I", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"), remap = false)
    public boolean ignoreCase(String input, Object nextInput) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleBrigadierIgnoreCase.class).isValidCommand()) {
            return input.equalsIgnoreCase((String) nextInput);
        }

        return input.equals(nextInput);
    }
}
