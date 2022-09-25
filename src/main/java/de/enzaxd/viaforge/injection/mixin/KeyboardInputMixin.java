package de.enzaxd.viaforge.injection.mixin;

import de.enzaxd.viaforge.equals.ProtocolEquals;
import de.enzaxd.viaforge.equals.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {

    @ModifyVariable(method = "tick", at = @At(value = "LOAD", ordinal = 0), argsOnly = true)
    private boolean injectTick(boolean slowDown) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_13_2)) {
            return this.sneaking;
        } else if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_14_4)) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            assert player != null;
            return !player.isSpectator() && (this.sneaking || slowDown);
        }
        return slowDown;
    }
}
