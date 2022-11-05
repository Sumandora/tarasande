package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput extends Input {

    @ModifyVariable(method = "tick", at = @At(value = "LOAD", ordinal = 0), argsOnly = true)
    private boolean injectTick(boolean slowDown) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_13_2)) {
            return this.sneaking;
        } else if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_14_4)) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            assert player != null;
            return !player.isSpectator() && (this.sneaking || slowDown);
        }
        return slowDown;
    }
}
