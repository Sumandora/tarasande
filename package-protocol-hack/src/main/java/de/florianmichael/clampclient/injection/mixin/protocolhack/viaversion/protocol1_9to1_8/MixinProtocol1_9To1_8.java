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

package de.florianmichael.clampclient.injection.mixin.protocolhack.viaversion.protocol1_9to1_8;

import com.viaversion.viaversion.api.connection.UserConnection;
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
import de.florianmichael.clampclient.injection.instrumentation_1_8.SignStorage;
import net.minecraft.client.MinecraftClient;
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DataFlowIssue")
@Mixin(value = Protocol1_9To1_8.class, remap = false)
public class MixinProtocol1_9To1_8 extends AbstractProtocol<ClientboundPackets1_8, ClientboundPackets1_9, ServerboundPackets1_8, ServerboundPackets1_9> {

    @Inject(method = "init", at = @At("RETURN"))
    public void addSignStorage(UserConnection userConnection, CallbackInfo ci) {
        if (ProtocolHackValues.INSTANCE.getEmulateSignGUIModification().getValue()) {
            userConnection.put(new SignStorage(userConnection));
        }
    }

    @Inject(method = "registerPackets", at = @At("RETURN"))
    public void emulateSignData(CallbackInfo ci) {
        this.registerServerbound(ServerboundPackets1_9.UPDATE_SIGN, new PacketRemapper() {
            public void registerMap() {
                this.map(Type.POSITION);

                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);

                this.handler(wrapper -> {
                    final SignStorage signStorage = wrapper.user().get(SignStorage.class);
                    if (signStorage != null) {
                        final SignStorage.SignModel_1_8 currentSign = signStorage.getSigns().get(signStorage.getSigns().size() - 1);

                        final int chunkX = currentSign.position().x() / 16;
                        final int chunkZ = currentSign.position().y() / 16;

                        if (!MinecraftClient.getInstance().world.isChunkLoaded(chunkX, chunkZ)) {
                            wrapper.cancel();
                        }

                        wrapper.set(Type.COMPONENT, 0, currentSign.line1());
                        wrapper.set(Type.COMPONENT, 1, currentSign.line2());
                        wrapper.set(Type.COMPONENT, 2, currentSign.line3());
                        wrapper.set(Type.COMPONENT, 3, currentSign.line4());
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
                this.handler(wrapper -> {
                    final SignStorage signStorage = wrapper.user().get(SignStorage.class);
                    if (signStorage != null) {
                        final Position position = wrapper.get(Type.POSITION, 0);

                        final JsonElement line1 = wrapper.get(Type.COMPONENT, 0);
                        final JsonElement line2 = wrapper.get(Type.COMPONENT, 1);
                        final JsonElement line3 = wrapper.get(Type.COMPONENT, 2);
                        final JsonElement line4 = wrapper.get(Type.COMPONENT, 3);

                        signStorage.getSigns().add(new SignStorage.SignModel_1_8(line1, line2, line3, line4, position));
                    }
                });
            }
        });
    }
}
