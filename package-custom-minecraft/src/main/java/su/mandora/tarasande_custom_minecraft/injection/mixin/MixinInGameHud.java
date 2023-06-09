package su.mandora.tarasande_custom_minecraft.injection.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande_custom_minecraft.optimizations.OptimizedScoreboard;
import su.mandora.tarasande_custom_minecraft.tarasandevalues.optimization.OptimizationValues;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    public void optimizeScoreboard(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        if(OptimizationValues.INSTANCE.getOptimizeScoreboard().getValue()) {
            //noinspection DataFlowIssue
            OptimizedScoreboard.INSTANCE.renderScoreboardSidebar((InGameHud) (Object) this, matrices, objective);
            ci.cancel();
        }
    }

}
