package de.florianmichael.viasnapshot.protocol.protocol1_16to20w14infinite.packets;

import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.types.Chunk1_15Type;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.types.Chunk1_16Type;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.util.CompactArrayUtil;
import de.florianmichael.viasnapshot.protocol.protocol1_16to20w14infinite.ClientboundPackets20w14infinite;
import de.florianmichael.viasnapshot.protocol.protocol1_16to20w14infinite.Protocol1_16to20w14infinite;
import de.florianmichael.viasnapshot.protocol.protocol1_16to20w14infinite.data.BiomeData20w14infinite;

import java.util.Map;

public class BlockItemPackets20w14infinite extends ItemRewriter<ClientboundPackets20w14infinite, ServerboundPackets1_16, Protocol1_16to20w14infinite> {

    public BlockItemPackets20w14infinite(Protocol1_16to20w14infinite protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        this.registerSetCooldown(ClientboundPackets20w14infinite.COOLDOWN);
        this.registerWindowItems(ClientboundPackets20w14infinite.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
        this.registerSetSlot(ClientboundPackets20w14infinite.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
        this.registerTradeList(ClientboundPackets20w14infinite.TRADE_LIST);
        this.registerAdvancements(ClientboundPackets20w14infinite.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
        this.registerSpawnParticle(ClientboundPackets20w14infinite.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, Type.DOUBLE);
        this.registerClickWindow(ServerboundPackets1_16.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
        this.registerCreativeInvAction(ServerboundPackets1_16.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
        final BlockRewriter<ClientboundPackets20w14infinite> blockRewriter = new BlockRewriter(this.protocol, Type.POSITION1_14);
        blockRewriter.registerBlockAction(ClientboundPackets20w14infinite.BLOCK_ACTION);
        blockRewriter.registerBlockChange(ClientboundPackets20w14infinite.BLOCK_CHANGE);
        blockRewriter.registerMultiBlockChange(ClientboundPackets20w14infinite.MULTI_BLOCK_CHANGE);
        blockRewriter.registerAcknowledgePlayerDigging(ClientboundPackets20w14infinite.ACKNOWLEDGE_PLAYER_DIGGING);
        blockRewriter.registerEffect(ClientboundPackets20w14infinite.EFFECT, 1010, 2001);

        protocol.registerClientbound(ClientboundPackets20w14infinite.UPDATE_LIGHT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // x
                map(Type.VAR_INT); // y
                handler(wrapper -> wrapper.write(Type.BOOLEAN, true)); // Take neighbour's light into account as well
            }
        });
        protocol.registerClientbound(ClientboundPackets20w14infinite.CHUNK_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    Chunk chunk = wrapper.read(new Chunk1_15Type());
                    wrapper.write(new Chunk1_16Type(), chunk);

                    chunk.setIgnoreOldLightData(chunk.isFullChunk());

                    for (int s = 0; s < chunk.getSections().length; s++) {
                        ChunkSection section = chunk.getSections()[s];
                        if (section == null) continue;
                        final DataPalette blockPalette = section.palette(PaletteType.BLOCKS);
                        for (int i = 0; i < blockPalette.size(); i++) {
                            int old = blockPalette.idByIndex(i);
                            blockPalette.setIdByIndex(i, protocol.getMappingData().getNewBlockStateId(old));
                        }
                    }

                    if (chunk.getBiomeData() != null) {
                        for (int i = 0; i < chunk.getBiomeData().length; i++) {
                            if (!BiomeData20w14infinite.isValid(chunk.getBiomeData()[i])) {
                                chunk.getBiomeData()[i] = 1; // plains
                            }
                        }
                    }

                    CompoundTag heightMaps = chunk.getHeightMap();
                    for (Map.Entry<String, Tag> heightMapTag : heightMaps) {
                        LongArrayTag heightMap = (LongArrayTag) heightMapTag.getValue();
                        int[] heightMapData = new int[256];
                        CompactArrayUtil.iterateCompactArray(9, heightMapData.length, heightMap.getValue(), (i, v) -> heightMapData[i] = v);
                        heightMap.setValue(CompactArrayUtil.createCompactArrayWithPadding(9, heightMapData.length, i -> heightMapData[i]));
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets20w14infinite.ENTITY_EQUIPMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID

                handler(wrapper -> {
                    int slot = wrapper.read(Type.VAR_INT);
                    wrapper.write(Type.BYTE, (byte) slot);
                    handleItemToClient(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                });
            }
        });

        protocol.registerServerbound(ServerboundPackets1_16.EDIT_BOOK, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> handleItemToServer(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
            }
        });
    }

}
