package de.florianmichael.viacursed.protocol.protocol1_16to20w14infinite;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16Types;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.data.RecipeRewriter1_14;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;
import de.florianmichael.viacursed.protocol.protocol1_16to20w14infinite.metadata.MetadataRewriter1_16to20w14infinite;
import de.florianmichael.viacursed.protocol.protocol1_16to20w14infinite.packets.BlockItemPackets20w14infinite;
import de.florianmichael.viacursed.protocol.protocol1_16to20w14infinite.packets.EntityPackets20w14infinite;
import de.florianmichael.viacursed.protocol.protocol1_16to20w14infinite.provider.PlayerAbilitiesProvider;
import de.florianmichael.viacursed.util.ItemBackwardsMappings;

import java.util.UUID;

public class Protocol1_16to20w14infinite extends BackwardsProtocol<ClientboundPackets20w14infinite, ClientboundPackets1_16, ServerboundPackets20w14infinite, ServerboundPackets1_16> {

    public static final BackwardsMappings MAPPINGS = new ItemBackwardsMappings("20w14infinite", "1.16");
    private static final UUID ZERO_UUID = new UUID(0, 0);

    private BlockItemPackets20w14infinite blockItemPackets;
    private TagRewriter tagRewriter;
    private MetadataRewriter1_16to20w14infinite metadataRewriter;

    public Protocol1_16to20w14infinite() {
        super(ClientboundPackets20w14infinite.class, ClientboundPackets1_16.class, ServerboundPackets20w14infinite.class, ServerboundPackets1_16.class);
    }

    @Override
    protected void registerPackets() {
        this.metadataRewriter = new MetadataRewriter1_16to20w14infinite(this);
        metadataRewriter.register();
        tagRewriter = new TagRewriter(this);

        this.executeAsyncAfterLoaded(Protocol1_16To1_15_2.class, () -> {
            MAPPINGS.load();
            this.onMappingDataLoaded();
        });

        tagRewriter.register(ClientboundPackets20w14infinite.TAGS, RegistryType.ENTITY);
        new StatisticsRewriter(this).register(ClientboundPackets20w14infinite.STATISTICS);
        this.blockItemPackets = new BlockItemPackets20w14infinite(this);
        this.blockItemPackets.register();
        EntityPackets20w14infinite.register(this);
        final SoundRewriter soundRewriter = new SoundRewriter(this);
        soundRewriter.registerSound(ClientboundPackets20w14infinite.SOUND);
        soundRewriter.registerSound(ClientboundPackets20w14infinite.ENTITY_SOUND);
        soundRewriter.registerNamedSound(ClientboundPackets20w14infinite.NAMED_SOUND);
        soundRewriter.registerStopSound(ClientboundPackets20w14infinite.STOP_SOUND);
        new RecipeRewriter1_14(this).registerDefaultHandler(ClientboundPackets20w14infinite.DECLARE_RECIPES);

        this.registerClientbound(ClientboundPackets20w14infinite.CHAT_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.COMPONENT);
                map(Type.BYTE);
                handler(wrapper -> {
                    wrapper.write(Type.UUID, ZERO_UUID); // Sender uuid - always send as 'system'
                });
            }
        });

        this.cancelServerbound(ServerboundPackets1_16.GENERATE_JIGSAW);
        this.registerServerbound(ServerboundPackets1_16.INTERACT_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.passthrough(Type.VAR_INT); // Entity Id
                    int action = wrapper.passthrough(Type.VAR_INT);
                    if (action == 0 || action == 2) {
                        if (action == 2) {
                            // Location
                            wrapper.passthrough(Type.FLOAT);
                            wrapper.passthrough(Type.FLOAT);
                            wrapper.passthrough(Type.FLOAT);
                        }

                        wrapper.passthrough(Type.VAR_INT); // Hand
                    }

                    // New boolean: Whether the client is sneaking/pressing shift
                    wrapper.read(Type.BOOLEAN);
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_16.PLAYER_ABILITIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.passthrough(Type.BYTE);
                    final PlayerAbilitiesProvider playerAbilitiesProvider = Via.getManager().getProviders().get(PlayerAbilitiesProvider.class);
                    if (playerAbilitiesProvider == null) {
                        throw new IllegalStateException("ViaCursed doesn't have PlayerAbilitiesProvider?");
                    }
                    wrapper.write(Type.FLOAT, playerAbilitiesProvider.getFlySpeed());
                    wrapper.write(Type.FLOAT, playerAbilitiesProvider.getWalkSpeed());
                });
            }
        });
    }

    @Override
    protected void onMappingDataLoaded() {
        int[] wallPostOverrideTag = new int[47];
        int arrayIndex = 0;
        wallPostOverrideTag[arrayIndex++] = 140;
        wallPostOverrideTag[arrayIndex++] = 179;
        wallPostOverrideTag[arrayIndex++] = 264;
        for (int i = 153; i <= 158; i++) {
            wallPostOverrideTag[arrayIndex++] = i;
        }
        for (int i = 163; i <= 168; i++) {
            wallPostOverrideTag[arrayIndex++] = i;
        }
        for (int i = 408; i <= 439; i++) {
            wallPostOverrideTag[arrayIndex++] = i;
        }

        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:wall_post_override", wallPostOverrideTag);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:beacon_base_blocks", 133, 134, 148, 265);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:climbable", 160, 241, 658);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:fire", 142);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:campfires", 679);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:fence_gates", 242, 467, 468, 469, 470, 471);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:unstable_bottom_center", 242, 467, 468, 469, 470, 471);
        tagRewriter.addTag(RegistryType.BLOCK, "minecraft:wooden_trapdoors", 193, 194, 195, 196, 197, 198);
        tagRewriter.addTag(RegistryType.ITEM, "minecraft:wooden_trapdoors", 215, 216, 217, 218, 219, 220);
        tagRewriter.addTag(RegistryType.ITEM, "minecraft:beacon_payment_items", 529, 530, 531, 760);
        tagRewriter.addTag(RegistryType.ENTITY, "minecraft:impact_projectiles", 2, 72, 71, 37, 69, 79, 83, 15, 93);

        // The client crashes if we don't send all tags it may use
        tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:guarded_by_piglins");
        tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:soul_speed_blocks");
        tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:soul_fire_base_blocks");
        tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:non_flammable_wood");
        tagRewriter.addEmptyTag(RegistryType.ITEM, "minecraft:non_flammable_wood");

        // The rest of not accessed tags added in older versions; #1830
        tagRewriter.addEmptyTags(RegistryType.BLOCK, "minecraft:bamboo_plantable_on", "minecraft:beds", "minecraft:bee_growables",
                "minecraft:beehives", "minecraft:coral_plants", "minecraft:crops", "minecraft:dragon_immune", "minecraft:flowers",
                "minecraft:portals", "minecraft:shulker_boxes", "minecraft:small_flowers", "minecraft:tall_flowers", "minecraft:trapdoors",
                "minecraft:underwater_bonemeals", "minecraft:wither_immune", "minecraft:wooden_fences", "minecraft:wooden_trapdoors");
        tagRewriter.addEmptyTags(RegistryType.ENTITY, "minecraft:arrows", "minecraft:beehive_inhabitors", "minecraft:raiders", "minecraft:skeletons");
        tagRewriter.addEmptyTags(RegistryType.ITEM, "minecraft:beds", "minecraft:coals", "minecraft:fences", "minecraft:flowers",
                "minecraft:lectern_books", "minecraft:music_discs", "minecraft:small_flowers", "minecraft:tall_flowers", "minecraft:trapdoors", "minecraft:walls", "minecraft:wooden_fences");
    }

    @Override
    public void register(ViaProviders providers) {
        super.register(providers);

        providers.register(PlayerAbilitiesProvider.class, new PlayerAbilitiesProvider());
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.addEntityTracker(this.getClass(), new EntityTrackerBase(userConnection, Entity1_16Types.PLAYER));
    }

    @Override
    public BlockItemPackets20w14infinite getItemRewriter() {
        return this.blockItemPackets;
    }

    @Override
    public MetadataRewriter1_16to20w14infinite getEntityRewriter() {
        return this.metadataRewriter;
    }

    @Override
    public BackwardsMappings getMappingData() {
        return MAPPINGS;
    }
}
