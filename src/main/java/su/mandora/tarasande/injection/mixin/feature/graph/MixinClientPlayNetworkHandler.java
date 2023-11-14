package su.mandora.tarasande.injection.mixin.feature.graph;

import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.system.screen.graphsystem.ManagerGraph;
import su.mandora.tarasande.system.screen.graphsystem.impl.GraphPing;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowPacketSizeAndPingCharts()Z"))
    public boolean spoofState(DebugHud instance) {
        GraphPing graphPing = ManagerGraph.INSTANCE.get(GraphPing.class);
        if(graphPing.shouldSpoof())
            return true;
        return instance.shouldShowPacketSizeAndPingCharts();
    }

}
