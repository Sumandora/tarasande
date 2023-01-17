/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.clampclient.injection.mixin.protocolhack.viaversion;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import net.minecraft.client.MinecraftClient;
import de.florianmichael.tarasande_protocol_hack.fix.Sign_1_8;
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DataFlowIssue")
@Mixin(value = Protocol1_9To1_8.class, remap = false)
public class MixinProtocol1_9To1_8 extends AbstractProtocol<ClientboundPackets1_8, ClientboundPackets1_9, ServerboundPackets1_8, ServerboundPackets1_9> {

    @Inject(method = "registerPackets", at = @At("RETURN"))
    public void emulateSignData(CallbackInfo ci) {
        this.registerServerbound(ServerboundPackets1_9.UPDATE_SIGN, new PacketRemapper() {
            public void registerMap() {
                this.map(Type.POSITION);

                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);

                this.handler((pw) -> {
                    if (ProtocolHackValues.INSTANCE.getEmulateSignGUIModification().getValue()) {
                        final Sign_1_8 currentSign = Sign_1_8.Companion.getSigns().get(Sign_1_8.Companion.getSigns().size() - 1);

                        final int chunkX = currentSign.getPosition().x() / 16;
                        final int chunkZ = currentSign.getPosition().y() / 16;

                        if (!MinecraftClient.getInstance().world.isChunkLoaded(chunkX, chunkZ)) {
                            pw.cancel();
                        }

                        pw.set(Type.COMPONENT, 0, currentSign.getLine1());
                        pw.set(Type.COMPONENT, 1, currentSign.getLine2());
                        pw.set(Type.COMPONENT, 2, currentSign.getLine3());
                        pw.set(Type.COMPONENT, 3, currentSign.getLine4());
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_8.UPDATE_SIGN, new PacketRemapper() {
            public void registerMap() {
                this.map(Type.POSITION);
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.handler((pw) -> {
                    final Position position = pw.get(Type.POSITION, 0);

                    final JsonElement line1 = pw.get(Type.COMPONENT, 0);
                    final JsonElement line2 = pw.get(Type.COMPONENT, 1);
                    final JsonElement line3 = pw.get(Type.COMPONENT, 2);
                    final JsonElement line4 = pw.get(Type.COMPONENT, 3);

                    Sign_1_8.Companion.getSigns().add(new Sign_1_8(line1, line2, line3, line4, position));
                });
            }
        });
    }
}
