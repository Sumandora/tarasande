package net.tarasandedevelopment.tarasande.mixin.mixins.features.module.brigadier;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.misc.ModuleBrigadierIgnoreCase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(value = CommandNode.class, remap = false)
public class MixinCommandNode<S> {

    @Shadow @Final private Map<String, LiteralCommandNode<S>> literals;

    @Redirect(method = "getRelevantNodes", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), remap = false)
    public Object ignoreCase(Map<?, ?> instance, Object o) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleBrigadierIgnoreCase.class).isValidCommand()) {
            for (Map.Entry<String, LiteralCommandNode<S>> entry : literals.entrySet()) {
                if (((String) o).equalsIgnoreCase(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }

        return instance.get(o);
    }
}
