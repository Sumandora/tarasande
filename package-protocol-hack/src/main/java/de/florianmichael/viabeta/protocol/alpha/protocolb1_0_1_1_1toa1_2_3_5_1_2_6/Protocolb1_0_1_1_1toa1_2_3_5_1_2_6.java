package de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.*;
import de.florianmichael.viabeta.api.data.BlockList1_6;
import de.florianmichael.viabeta.api.model.IdAndData;
import de.florianmichael.viabeta.api.data.ItemList1_6;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.task.AlphaInventoryUpdateTask;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.Protocol1_2_1_3to1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_4_5to1_2_1_3.ClientboundPackets1_2_1;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.data.AlphaItems;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.provider.AlphaInventoryProvider;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.provider.TrackingAlphaInventoryProvider;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.storage.AlphaInventoryTracker;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.storage.InventoryStorage;
import de.florianmichael.viabeta.protocol.beta.protocolb1_2_0_2tob1_1_2.ClientboundPacketsb1_1;
import de.florianmichael.viabeta.protocol.beta.protocolb1_2_0_2tob1_1_2.ServerboundPacketsb1_1;
import de.florianmichael.viabeta.protocol.beta.protocolb1_2_0_2tob1_1_2.type.Typeb1_1;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.type.Typeb1_7_0_3;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.data.EntityList_1_2_5;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.Type1_3_1_2;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.Type1_4_2;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.ChunkTracker;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.PlayerInfoStorage;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings({"DataFlowIssue", "SameParameterValue"})
public class Protocolb1_0_1_1_1toa1_2_3_5_1_2_6 extends AbstractProtocol<ClientboundPacketsa1_2_6, ClientboundPacketsb1_1, ServerboundPacketsa1_2_6, ServerboundPacketsb1_1> {

    public Protocolb1_0_1_1_1toa1_2_3_5_1_2_6() {
        super(ClientboundPacketsa1_2_6.class, ClientboundPacketsb1_1.class, ServerboundPacketsa1_2_6.class, ServerboundPacketsb1_1.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundPacketsa1_2_6.PLAYER_INVENTORY, ClientboundPacketsb1_1.WINDOW_ITEMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final InventoryStorage inventoryStorage = wrapper.user().get(InventoryStorage.class);
                    final AlphaInventoryTracker inventoryTracker = wrapper.user().get(AlphaInventoryTracker.class);
                    final int type = wrapper.read(Type.INT); // type
                    Item[] items = wrapper.read(Type1_4_2.NBTLESS_ITEM_ARRAY); // items

                    final Item[] windowItems = new Item[45];
                    System.arraycopy(inventoryStorage.mainInventory, 0, windowItems, 36, 9);
                    System.arraycopy(inventoryStorage.mainInventory, 9, windowItems, 9, 36 - 9);
                    System.arraycopy(inventoryStorage.craftingInventory, 0, windowItems, 1, 4);
                    System.arraycopy(inventoryStorage.armorInventory, 0, windowItems, 5, 4);

                    switch (type) {
                        case -1: // main
                            inventoryStorage.mainInventory = items;
                            if (inventoryTracker != null) inventoryTracker.setMainInventory(copyItems(items));
                            System.arraycopy(items, 0, windowItems, 36, 9);
                            System.arraycopy(items, 9, windowItems, 9, 36 - 9);
                            break;
                        case -2: // crafting
                            inventoryStorage.craftingInventory = items;
                            if (inventoryTracker != null) inventoryTracker.setCraftingInventory(copyItems(items));
                            System.arraycopy(items, 0, windowItems, 1, 4);
                            break;
                        case -3: // armor
                            inventoryStorage.armorInventory = items;
                            if (inventoryTracker != null) inventoryTracker.setArmorInventory(copyItems(items));
                            System.arraycopy(reverseArray(items), 0, windowItems, 5, 4);
                    }

                    wrapper.write(Type.BYTE, (byte) 0); // window id
                    wrapper.write(Type1_4_2.NBTLESS_ITEM_ARRAY, copyItems(windowItems)); // items
                });
            }
        });
        this.registerClientbound(ClientboundPacketsa1_2_6.UPDATE_HEALTH, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE, Type.SHORT); // health
            }
        });
        this.registerClientbound(ClientboundPacketsa1_2_6.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.user().get(InventoryStorage.class).resetPlayerInventory();

                    final AlphaInventoryTracker inventoryTracker = wrapper.user().get(AlphaInventoryTracker.class);
                    if (inventoryTracker != null) inventoryTracker.onRespawn();
                });
            }
        });
        this.registerClientbound(ClientboundPacketsa1_2_6.HELD_ITEM_CHANGE, ClientboundPacketsb1_1.ENTITY_EQUIPMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                create(Type.SHORT, (short) 0); // slot (hand)
                map(Type.SHORT); // item id
                handler(wrapper -> {
                    if (wrapper.get(Type.SHORT, 1) == 0) {
                        wrapper.set(Type.SHORT, 1, (short) -1);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPacketsa1_2_6.ADD_TO_INVENTORY, null, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    final Item item = wrapper.read(Type1_3_1_2.NBTLESS_ITEM); // item
                    Via.getManager().getProviders().get(AlphaInventoryProvider.class).addToInventory(wrapper.user(), item);
                });
            }
        });
        this.registerClientbound(ClientboundPacketsa1_2_6.PRE_CHUNK, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // chunkX
                map(Type.INT); // chunkZ
                map(Type.UNSIGNED_BYTE); // mode

                handler(wrapper -> wrapper.user().get(InventoryStorage.class).unload(wrapper.get(Type.INT, 0), wrapper.get(Type.INT, 1)));
            }
        });
        this.registerClientbound(ClientboundPacketsa1_2_6.COMPLEX_ENTITY, null, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    final InventoryStorage tracker = wrapper.user().get(InventoryStorage.class);
                    final Position pos = wrapper.read(Type1_7_6_10.POSITION_SHORT); // position
                    final CompoundTag tag = wrapper.read(Type1_7_6_10.COMPRESSED_NBT); // data

                    if (tag.<IntTag>get("x").asInt() != pos.x() || tag.<IntTag>get("y").asInt() != pos.y() || tag.<IntTag>get("z").asInt() != pos.z()) {
                        return;
                    }

                    final IdAndData block = wrapper.user().get(ChunkTracker.class).getBlockNotNull(pos);
                    final String blockName = tag.get("id") != null ? tag.<StringTag>get("id").getValue() : "";

                    if (block.id == BlockList1_6.signPost.blockID || block.id == BlockList1_6.signWall.blockID || blockName.equals("Sign")) {
                        final PacketWrapper updateSign = PacketWrapper.create(ClientboundPacketsb1_1.UPDATE_SIGN, wrapper.user());
                        updateSign.write(Type1_7_6_10.POSITION_SHORT, pos); // position
                        updateSign.write(Typeb1_7_0_3.STRING, tag.<StringTag>get("Text1").getValue()); // line 1
                        updateSign.write(Typeb1_7_0_3.STRING, tag.<StringTag>get("Text2").getValue()); // line 2
                        updateSign.write(Typeb1_7_0_3.STRING, tag.<StringTag>get("Text3").getValue()); // line 3
                        updateSign.write(Typeb1_7_0_3.STRING, tag.<StringTag>get("Text4").getValue()); // line 4
                        updateSign.send(Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.class);
                    } else if (block.id == BlockList1_6.mobSpawner.blockID || blockName.equals("MobSpawner")) {
                        if (wrapper.user().getProtocolInfo().getPipeline().contains(Protocol1_2_1_3to1_1.class)) {
                            final PacketWrapper spawnerData = PacketWrapper.create(ClientboundPackets1_2_1.BLOCK_ENTITY_DATA, wrapper.user());
                            spawnerData.write(Type1_7_6_10.POSITION_SHORT, pos); // position
                            spawnerData.write(Type.BYTE, (byte) 1); // type
                            spawnerData.write(Type.INT, EntityList_1_2_5.getEntityId(tag.<StringTag>get("EntityId").getValue())); // entity id
                            spawnerData.write(Type.INT, 0); // unused
                            spawnerData.write(Type.INT, 0); // unused
                            spawnerData.send(Protocol1_2_1_3to1_1.class);
                        }
                    } else if (block.id == BlockList1_6.chest.blockID || blockName.equals("Chest")) {
                        final Item[] chestItems = new Item[3 * 9];
                        readItemsFromTag(tag, chestItems);
                        tracker.containers.put(pos, chestItems);
                        if (pos.equals(tracker.openContainerPos)) sendWindowItems(wrapper.user(), InventoryStorage.CHEST_WID, chestItems);
                    } else if (block.id == BlockList1_6.furnaceIdle.blockID || block.id == BlockList1_6.furnaceBurning.blockID || blockName.equals("Furnace")) {
                        final Item[] furnaceItems = new Item[3];
                        readItemsFromTag(tag, furnaceItems);
                        tracker.containers.put(pos, furnaceItems);
                        if (pos.equals(tracker.openContainerPos)) {
                            sendWindowItems(wrapper.user(), InventoryStorage.FURNACE_WID, furnaceItems);
                            sendProgressUpdate(wrapper.user(), InventoryStorage.FURNACE_WID, (short) 0, tag.<ShortTag>get("CookTime").asShort()); // cook time
                            sendProgressUpdate(wrapper.user(), InventoryStorage.FURNACE_WID, (short) 1, tag.<ShortTag>get("BurnTime").asShort()); // furnace burn time
                            sendProgressUpdate(wrapper.user(), InventoryStorage.FURNACE_WID, (short) 2, getBurningTime(furnaceItems[1])); // item burn time
                        }
                    } else {
                        ViaBeta.getPlatform().getLogger().warning("Unhandled Complex Entity data: " + block + "@" + pos + ": '" + tag + "'");
                    }
                });
            }
        });

        this.registerServerbound(ServerboundPacketsb1_1.PLAYER_DIGGING, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE); // status
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // direction
                handler(wrapper -> {
                    final short status = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    if (status == 4) {
                        wrapper.cancel();

                        final Item selectedItem = fixItem(Via.getManager().getProviders().get(AlphaInventoryProvider.class).getHandItem(wrapper.user()));
                        if (selectedItem == null) {
                            return;
                        }

                        final AlphaInventoryTracker inventoryTracker = wrapper.user().get(AlphaInventoryTracker.class);
                        if (inventoryTracker != null) inventoryTracker.onHandItemDrop();

                        selectedItem.setAmount(1);
                        dropItem(wrapper.user(), selectedItem, false);
                    }
                });
            }
        });
        this.registerServerbound(ServerboundPacketsb1_1.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final InventoryStorage tracker = wrapper.user().get(InventoryStorage.class);
                    final AlphaInventoryTracker inventoryTracker = wrapper.user().get(AlphaInventoryTracker.class);
                    final Position pos = wrapper.read(Type1_7_6_10.POSITION_UBYTE); // position
                    final short direction = wrapper.read(Type.UNSIGNED_BYTE); // direction
                    Item item = fixItem(wrapper.read(Typeb1_1.NBTLESS_ITEM)); // item

                    if (item == null && inventoryTracker != null) {
                        item = Via.getManager().getProviders().get(AlphaInventoryProvider.class).getHandItem(wrapper.user());
                    }

                    wrapper.write(Type.SHORT, item == null ? (short) -1 : (short) item.identifier()); // item id
                    wrapper.write(Type1_7_6_10.POSITION_UBYTE, pos);
                    wrapper.write(Type.UNSIGNED_BYTE, direction);

                    if (inventoryTracker != null) inventoryTracker.onBlockPlace(pos, direction);

                    if (direction == 255) return;

                    final IdAndData block = wrapper.user().get(ChunkTracker.class).getBlockNotNull(pos);
                    if (block.id != BlockList1_6.furnaceIdle.blockID && block.id != BlockList1_6.furnaceBurning.blockID && block.id != BlockList1_6.chest.blockID && block.id != BlockList1_6.workbench.blockID) {
                        return;
                    }

                    final Item[] containerItems = tracker.containers.get(tracker.openContainerPos = pos);
                    if (containerItems == null && block.id != BlockList1_6.workbench.blockID) {
                        tracker.openContainerPos = null;
                        final PacketWrapper chatMessage = PacketWrapper.create(ClientboundPacketsb1_1.CHAT_MESSAGE, wrapper.user());
                        chatMessage.write(Typeb1_7_0_3.STRING, "§cMissing Container"); // message
                        chatMessage.send(Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.class);
                        return;
                    }

                    final PacketWrapper openWindow = PacketWrapper.create(ClientboundPacketsb1_1.OPEN_WINDOW, wrapper.user());
                    if (block.id == BlockList1_6.chest.blockID) {
                        openWindow.write(Type.UNSIGNED_BYTE, (short) InventoryStorage.CHEST_WID); // window id
                        openWindow.write(Type.UNSIGNED_BYTE, (short) 0); // window type
                        openWindow.write(Typeb1_7_0_3.STRING, "Chest"); // title
                        openWindow.write(Type.UNSIGNED_BYTE, (short) (3 * 9)); // slots
                        if (inventoryTracker != null) inventoryTracker.onWindowOpen(0, 3 * 9);
                    } else if (block.id == BlockList1_6.workbench.blockID) {
                        openWindow.write(Type.UNSIGNED_BYTE, (short) InventoryStorage.WORKBENCH_WID); // window id
                        openWindow.write(Type.UNSIGNED_BYTE, (short) 1); // window type
                        openWindow.write(Typeb1_7_0_3.STRING, "Crafting Table"); // title
                        openWindow.write(Type.UNSIGNED_BYTE, (short) 9); // slots
                        if (inventoryTracker != null) inventoryTracker.onWindowOpen(1, 10);
                    } else { // furnace
                        openWindow.write(Type.UNSIGNED_BYTE, (short) InventoryStorage.FURNACE_WID); // window id
                        openWindow.write(Type.UNSIGNED_BYTE, (short) 2); // window type
                        openWindow.write(Typeb1_7_0_3.STRING, "Furnace"); // title
                        openWindow.write(Type.UNSIGNED_BYTE, (short) 3); // slots
                        if (inventoryTracker != null) inventoryTracker.onWindowOpen(2, 3);
                    }
                    openWindow.send(Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.class);

                    if (block.id != BlockList1_6.workbench.blockID) {
                        sendWindowItems(wrapper.user(), block.id == BlockList1_6.chest.blockID ? InventoryStorage.CHEST_WID : InventoryStorage.FURNACE_WID, containerItems);
                    }
                });
            }
        });
        this.registerServerbound(ServerboundPacketsb1_1.HELD_ITEM_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final InventoryStorage inventoryStorage = wrapper.user().get(InventoryStorage.class);
                    short slot = wrapper.read(Type.SHORT); // slot
                    if (slot < 0 || slot > 8) slot = 0;
                    inventoryStorage.selectedHotbarSlot = slot;
                    final Item selectedItem = fixItem(Via.getManager().getProviders().get(AlphaInventoryProvider.class).getHandItem(wrapper.user()));
                    if (Objects.equals(selectedItem, inventoryStorage.handItem)) {
                        wrapper.cancel();
                        return;
                    }
                    inventoryStorage.handItem = selectedItem;

                    wrapper.write(Type.INT, 0); // entity id (always 0)
                    wrapper.write(Type.SHORT, (short) (selectedItem == null ? 0 : selectedItem.identifier())); // item id
                });
            }
        });
        this.registerServerbound(ServerboundPacketsb1_1.CLOSE_WINDOW, null, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    wrapper.user().get(InventoryStorage.class).openContainerPos = null;

                    final AlphaInventoryTracker inventoryTracker = wrapper.user().get(AlphaInventoryTracker.class);
                    if (inventoryTracker != null) inventoryTracker.onWindowClose();
                });
            }
        });
        this.registerServerbound(ServerboundPacketsb1_1.CLICK_WINDOW, ServerboundPacketsa1_2_6.COMPLEX_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final InventoryStorage tracker = wrapper.user().get(InventoryStorage.class);
                    final AlphaInventoryTracker inventoryTracker = wrapper.user().get(AlphaInventoryTracker.class);
                    final byte windowId = wrapper.read(Type.BYTE); // window id
                    final short slot = wrapper.read(Type.SHORT); // slot
                    final byte button = wrapper.read(Type.BYTE); // button
                    final short action = wrapper.read(Type.SHORT); // action
                    final Item item = fixItem(wrapper.read(Typeb1_1.NBTLESS_ITEM)); // item

                    if (inventoryTracker != null) inventoryTracker.onWindowClick(windowId, slot, button, action, item);
                    if ((windowId != InventoryStorage.CHEST_WID && windowId != InventoryStorage.FURNACE_WID) || tracker.openContainerPos == null) {
                        wrapper.cancel();
                        return;
                    }

                    final Item[] containerItems = fixItems(Via.getManager().getProviders().get(AlphaInventoryProvider.class).getContainerItems(wrapper.user()));
                    if (Arrays.equals(tracker.containers.get(tracker.openContainerPos), containerItems)) {
                        wrapper.cancel();
                        return;
                    }
                    tracker.containers.put(tracker.openContainerPos, containerItems);

                    final CompoundTag tag = new CompoundTag();
                    tag.put("id", new StringTag(windowId == InventoryStorage.CHEST_WID ? "Chest" : "Furnace"));
                    tag.put("x", new IntTag(tracker.openContainerPos.x()));
                    tag.put("y", new IntTag(tracker.openContainerPos.y()));
                    tag.put("z", new IntTag(tracker.openContainerPos.z()));
                    writeItemsToTag(tag, containerItems);

                    wrapper.write(Type.INT, tracker.openContainerPos.x());
                    wrapper.write(Type.SHORT, (short) tracker.openContainerPos.y());
                    wrapper.write(Type.INT, tracker.openContainerPos.z());
                    wrapper.write(Type1_7_6_10.COMPRESSED_NBT, tag);
                });
            }
        });
        this.registerServerbound(ServerboundPacketsb1_1.UPDATE_SIGN, ServerboundPacketsa1_2_6.COMPLEX_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final Position pos = wrapper.passthrough(Type1_7_6_10.POSITION_SHORT); // position

                    final CompoundTag tag = new CompoundTag();
                    tag.put("id", new StringTag("Sign"));
                    tag.put("x", new IntTag(pos.x()));
                    tag.put("y", new IntTag(pos.y()));
                    tag.put("z", new IntTag(pos.z()));
                    tag.put("Text1", new StringTag(wrapper.read(Typeb1_7_0_3.STRING))); // line 1
                    tag.put("Text2", new StringTag(wrapper.read(Typeb1_7_0_3.STRING))); // line 2
                    tag.put("Text3", new StringTag(wrapper.read(Typeb1_7_0_3.STRING))); // line 3
                    tag.put("Text4", new StringTag(wrapper.read(Typeb1_7_0_3.STRING))); // line 4
                    wrapper.write(Type1_7_6_10.COMPRESSED_NBT, tag); // data
                });
            }
        });
        this.cancelServerbound(ServerboundPacketsb1_1.WINDOW_CONFIRMATION);
    }

    private void writeItemsToTag(final CompoundTag tag, final Item[] items) {
        final ListTag slotList = new ListTag();
        for (int i = 0; i < items.length; i++) {
            final Item item = items[i];
            if (item == null) continue;
            final CompoundTag slotTag = new CompoundTag();
            slotTag.put("Slot", new ByteTag((byte) i));
            slotTag.put("id", new ShortTag((short) item.identifier()));
            slotTag.put("Count", new ByteTag((byte) item.amount()));
            slotTag.put("Damage", new ShortTag(item.data()));
            slotList.add(slotTag);
        }
        tag.put("Items", slotList);
    }

    private void readItemsFromTag(final CompoundTag tag, final Item[] items) {
        final ListTag slotList = tag.get("Items");
        for (Tag itemTag : slotList) {
            final CompoundTag slotTag = (CompoundTag) itemTag;
            items[slotTag.<ByteTag>get("Slot").asByte() & 255] = new DataItem(slotTag.<ShortTag>get("id").asShort(), slotTag.<ByteTag>get("Count").asByte(), slotTag.<ShortTag>get("Damage").asShort(), null);
        }
    }

    private void sendWindowItems(final UserConnection user, final byte windowId, final Item[] items) throws Exception {
        final PacketWrapper windowItems = PacketWrapper.create(ClientboundPacketsb1_1.WINDOW_ITEMS, user);
        windowItems.write(Type.BYTE, windowId); // window id
        windowItems.write(Type1_4_2.NBTLESS_ITEM_ARRAY, copyItems(items)); // items
        windowItems.send(Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.class);

        final AlphaInventoryTracker inventoryTracker = user.get(AlphaInventoryTracker.class);
        if (inventoryTracker != null) inventoryTracker.setOpenContainerItems(copyItems(items));
    }

    private void sendProgressUpdate(final UserConnection user, final short windowId, final short id, final short value) throws Exception {
        final PacketWrapper windowProperty = PacketWrapper.create(ClientboundPacketsb1_1.WINDOW_PROPERTY, user);
        windowProperty.write(Type.UNSIGNED_BYTE, windowId); // window id
        windowProperty.write(Type.SHORT, id); // progress bar id
        windowProperty.write(Type.SHORT, value); // progress bar value
        windowProperty.send(Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.class);
    }

    private short getBurningTime(final Item item) {
        if (item == null) return 0;

        final int id = item.identifier();
        if (id == BlockList1_6.bookShelf.blockID || id == BlockList1_6.chest.blockID || id == BlockList1_6.fence.blockID || id == BlockList1_6.jukebox.blockID || id == BlockList1_6.wood.blockID || id == BlockList1_6.planks.blockID || id == BlockList1_6.doorWood.blockID || id == BlockList1_6.signWall.blockID || id == BlockList1_6.signPost.blockID || id == BlockList1_6.workbench.blockID) {
            return 300;
        } else if (id == ItemList1_6.stick.itemID) {
            return 100;
        } else if (id == ItemList1_6.coal.itemID) {
            return 1600;
        } else if (id == ItemList1_6.bucketLava.itemID) {
            return 20000;
        }
        return 0;
    }

    public static void dropItem(final UserConnection user, final Item item, final boolean flag) throws Exception {
        final PlayerInfoStorage playerInfoStorage = user.get(PlayerInfoStorage.class);
        final double itemX = playerInfoStorage.posX;
        final double itemY = playerInfoStorage.posY + 1.62F - 0.30000001192092896D + 0.12D;
        final double itemZ = playerInfoStorage.posZ;
        double motionX;
        double motionY;
        double motionZ;
        if (flag) {
            final float f2 = ThreadLocalRandom.current().nextFloat() * 0.5F;
            final float f1 = (float) (ThreadLocalRandom.current().nextFloat() * Math.PI * 2.0F);
            motionX = -Math.sin(f1) * f2;
            motionZ = Math.cos(f1) * f2;
            motionY = 0.20000000298023224D;
        } else {
            motionX = -Math.sin((playerInfoStorage.yaw / 180F) * Math.PI) * Math.cos((playerInfoStorage.pitch / 180F) * Math.PI) * 0.3F;
            motionZ = Math.cos((playerInfoStorage.yaw / 180F) * Math.PI) * Math.cos((playerInfoStorage.pitch / 180F) * Math.PI) * 0.3F;
            motionY = -Math.sin((playerInfoStorage.pitch / 180F) * Math.PI) * 0.3F + 0.1F;
            final float f1 = (float) (ThreadLocalRandom.current().nextFloat() * Math.PI * 2.0F);
            final float f2 = 0.02F * ThreadLocalRandom.current().nextFloat();
            motionX += Math.cos(f1) * (double) f2;
            motionY += (ThreadLocalRandom.current().nextFloat() - ThreadLocalRandom.current().nextFloat()) * 0.1F;
            motionZ += Math.sin(f1) * (double) f2;
        }

        final PacketWrapper spawnItem = PacketWrapper.create(ServerboundPacketsa1_2_6.SPAWN_ITEM, user);
        spawnItem.write(Type.INT, 0); // entity id
        spawnItem.write(Type.SHORT, (short) item.identifier()); // item id
        spawnItem.write(Type.BYTE, (byte) item.amount()); // item count
        spawnItem.write(Type.INT, (int) (itemX * 32)); // x
        spawnItem.write(Type.INT, (int) (itemY * 32)); // y
        spawnItem.write(Type.INT, (int) (itemZ * 32)); // z
        spawnItem.write(Type.BYTE, (byte) (motionX * 128)); // velocity x
        spawnItem.write(Type.BYTE, (byte) (motionY * 128)); // velocity y
        spawnItem.write(Type.BYTE, (byte) (motionZ * 128)); // velocity z
        spawnItem.sendToServer(Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.class);
    }

    public static Item[] reverseArray(final Item[] array) {
        if (array == null) return null;
        final Item[] reversed = new Item[array.length];

        for (int i = 0; i < array.length / 2; i++) {
            reversed[i] = array[array.length - i - 1];
            reversed[array.length - i - 1] = array[i];
        }

        return reversed;
    }

    public static Item copyItem(final Item item) {
        return item == null ? null : new DataItem(item);
    }

    public static Item[] copyItems(final Item[] items) {
        return Arrays.stream(items).map(Protocolb1_0_1_1_1toa1_2_3_5_1_2_6::copyItem).toArray(Item[]::new);
    }

    public static Item fixItem(final Item item) {
        if (item == null || !AlphaItems.isValid(item.identifier())) return null;
        item.setTag(null);
        return item;
    }

    public static Item[] fixItems(final Item[] items) {
        for (int i = 0; i < items.length; i++) {
            items[i] = fixItem(items[i]);
        }
        return items;
    }

    @Override
    public void register(ViaProviders providers) {
        super.register(providers);

        providers.register(AlphaInventoryProvider.class, new TrackingAlphaInventoryProvider());

        Via.getPlatform().runRepeatingSync(new AlphaInventoryUpdateTask(), 20L);
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.class, ClientboundPacketsa1_2_6::getPacket));

        userConnection.put(new InventoryStorage(userConnection));
        if (Via.getManager().getProviders().get(AlphaInventoryProvider.class).usesInventoryTracker()) {
            userConnection.put(new AlphaInventoryTracker(userConnection));
        }
    }
}
