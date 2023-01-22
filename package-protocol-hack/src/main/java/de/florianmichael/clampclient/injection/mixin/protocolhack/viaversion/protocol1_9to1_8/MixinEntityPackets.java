package de.florianmichael.clampclient.injection.mixin.protocolhack.viaversion.protocol1_9to1_8;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.metadata.MetadataRewriter1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("DuplicatedCode")
@Mixin(value = EntityPackets.class, remap = false)
public class MixinEntityPackets {

    @Inject(method = "register", at = @At("RETURN"))
    private static void passPositions(Protocol1_9To1_8 protocol, CallbackInfo ci) {
        protocol.registerClientbound(ClientboundPackets1_8.ENTITY_TELEPORT, ClientboundPackets1_9.ENTITY_TELEPORT, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                handler(wrapper -> {
                    final long x = ((long) wrapper.read(Type.INT)) - (long) Integer.MIN_VALUE;
                    final long y = ((long) wrapper.read(Type.INT)) - (long) Integer.MIN_VALUE;
                    final long z = ((long) wrapper.read(Type.INT)) - (long) Integer.MIN_VALUE;
                    wrapper.write(Type.DOUBLE, Double.longBitsToDouble(x));
                    wrapper.write(Type.DOUBLE, Double.longBitsToDouble(y));
                    wrapper.write(Type.DOUBLE, Double.longBitsToDouble(z));
                });

                map(Type.BYTE); // 4 - Pitch
                map(Type.BYTE); // 5 - Yaw

                map(Type.BOOLEAN); // 6 - On Ground

                handler(wrapper -> {
                    int entityID = wrapper.get(Type.VAR_INT, 0);
                    if (Via.getConfig().isHologramPatch()) {
                        EntityTracker1_9 tracker = wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                        if (tracker.getKnownHolograms().contains(entityID)) {
                            Double newValue = wrapper.get(Type.DOUBLE, 1);
                            newValue += (Via.getConfig().getHologramYOffset());
                            wrapper.set(Type.DOUBLE, 1, newValue);
                        }
                    }
                });
            }
        }, true);
        protocol.registerClientbound(ClientboundPackets1_8.SPAWN_PLAYER, ClientboundPackets1_9.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.UUID); // 1 - Player UUID

                // Parse this info
                handler(wrapper -> {
                    final int entityID = wrapper.get(Type.VAR_INT, 0);

                    EntityTracker1_9 tracker = wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.addEntity(entityID, Entity1_10Types.EntityType.PLAYER);
                    tracker.sendMetadataBuffer(entityID);
                });

                handler(wrapper -> {
                    final long x = ((long) wrapper.read(Type.INT)) - (long) Integer.MIN_VALUE;
                    final long y = ((long) wrapper.read(Type.INT)) - (long) Integer.MIN_VALUE;
                    final long z = ((long) wrapper.read(Type.INT)) - (long) Integer.MIN_VALUE;
                    wrapper.write(Type.DOUBLE, Double.longBitsToDouble(x));
                    wrapper.write(Type.DOUBLE, Double.longBitsToDouble(y));
                    wrapper.write(Type.DOUBLE, Double.longBitsToDouble(z));
                });

                map(Type.BYTE); // 5 - Yaw
                map(Type.BYTE); // 6 - Pitch

                //Handle discontinued player hand item
                handler(wrapper -> {
                    short item = wrapper.read(Type.SHORT);
                    if (item != 0) {
                        PacketWrapper packet = PacketWrapper.create(ClientboundPackets1_9.ENTITY_EQUIPMENT, null, wrapper.user());
                        packet.write(Type.VAR_INT, wrapper.get(Type.VAR_INT, 0));
                        packet.write(Type.VAR_INT, 0);
                        packet.write(Type.ITEM, new DataItem(item, (byte) 1, (short) 0, null));
                        try {
                            packet.send(Protocol1_9To1_8.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                map(Types1_8.METADATA_LIST, Types1_9.METADATA_LIST);

                handler(wrapper -> {
                    final List<Metadata> metadataList = wrapper.get(Types1_9.METADATA_LIST, 0);
                    int entityId = wrapper.get(Type.VAR_INT, 0);
                    EntityTracker1_9 tracker = wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    if (tracker.hasEntity(entityId)) {
                        protocol.get(MetadataRewriter1_9To1_8.class).handleMetadata(entityId, metadataList, wrapper.user());
                    } else {
                        Via.getPlatform().getLogger().warning("Unable to find entity for metadata, entity ID: " + entityId);
                        metadataList.clear();
                    }
                });

                // Handler for meta data
                handler(wrapper -> {
                    final List<Metadata> metadataList = wrapper.get(Types1_9.METADATA_LIST, 0);
                    final int entityID = wrapper.get(Type.VAR_INT, 0);

                    EntityTracker1_9 tracker = wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.handleMetadata(entityID, metadataList);
                });
            }
        }, true);
    }
}
