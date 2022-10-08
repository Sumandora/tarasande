package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Protocol1_13To1_12_2.class)
public class MixinProtocol1_13To1_12_2 extends AbstractProtocol<ClientboundPackets1_12_1, ClientboundPackets1_13, ServerboundPackets1_12_1, ServerboundPackets1_13> {

    @Inject(method = "registerPackets", at = @At("RETURN"), remap = false)
    public void fixParticles(CallbackInfo ci) {
        this.registerClientbound(ClientboundPackets1_12_1.ENTITY_EFFECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // Entity id
                map(Type.BYTE); // Effect id
                map(Type.BYTE); // Amplifier
                map(Type.VAR_INT); // Duration

                handler(packetWrapper -> {
                    byte flags = packetWrapper.read(Type.BYTE); // Input Flags

                    if (Via.getConfig().isNewEffectIndicator())
                        flags |= 0x04; // Since Minecraft 1.14.4, Minecraft has 2 Flags for rendering the particles, one for the Player Inventory, and one for the InGame HUD

                    packetWrapper.write(Type.BYTE, flags);
                });
            }
        });
    }
}
