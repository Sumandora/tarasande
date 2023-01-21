package de.florianmichael.viabeta.api.rewriter;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectArrayList;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectList;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.*;

import java.util.List;

@SuppressWarnings({"DataFlowIssue", "unchecked"})
public abstract class LegacyItemRewriter<P extends Protocol> extends RewriterBase<P> implements ItemRewriter<P> {

    private final ObjectList<RewriteEntry> rewriteEntries = new ObjectArrayList<>();
    private final ObjectList<NonExistentEntry> nonExistentItems = new ObjectArrayList<>();
    protected final String tagName;
    protected final String protocolName;

    public LegacyItemRewriter(final P protocol, final String protocolName) {
        super(protocol);
        this.tagName = protocolName.replace(".", "_") + "_ProtocolHack_" + System.currentTimeMillis();
        this.protocolName = protocolName;
    }

    protected void addRemappedItem(final int oldItemId, final int newItemId, final String newItemName) {
        this.addRemappedItem(oldItemId, newItemId, -1, newItemName);
    }

    protected void addRemappedItem(final int oldItemId, final int newItemId, final int newItemMeta, final String newItemName) {
        this.addRemappedItem(oldItemId, -1, newItemId, newItemMeta, newItemName);
    }

    protected void addRemappedItem(final int oldItemId, final int oldItemMeta, final int newItemId, final int newItemMeta, final String newItemName) {
        this.rewriteEntries.add(new RewriteEntry(oldItemId, (short) oldItemMeta, newItemId, (short) newItemMeta, newItemName));
    }

    protected void addNonExistentItem(final int itemId, final int itemMeta) {
        this.nonExistentItems.add(new NonExistentEntry(itemId, (short) itemMeta));
    }

    protected void addNonExistentItem(final int itemId, final int startItemMeta, final int endItemMeta) {
        for (int i = startItemMeta; i <= endItemMeta; i++) {
            this.nonExistentItems.add(new NonExistentEntry(itemId, (short) i));
        }
    }

    protected void addNonExistentItems(final int... itemIds) {
        for (int itemId : itemIds) {
            this.nonExistentItems.add(new NonExistentEntry(itemId, (short) -1));
        }
    }

    protected void addNonExistentItemRange(final int startItemId, final int endItemId) {
        for (int i = startItemId; i <= endItemId; i++) {
            this.nonExistentItems.add(new NonExistentEntry(i, (short) -1));
        }
    }

    public void registerCreativeInventoryAction(final ServerboundPacketType packetType, final Type<Item> type) {
        this.protocol.registerServerbound(packetType, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT); // slot
                map(type); // item
                handler(itemToServerHandler(type));
            }
        });
    }

    @Override
    public Item handleItemToClient(final Item item) {
        if (item == null) return null;

        for (RewriteEntry rewriteEntry : this.rewriteEntries) {
            if (rewriteEntry.rewrites(item)) {
                this.setRemappedNameRead(item, rewriteEntry.newItemName);
                if (rewriteEntry.newItemMeta != -1) {
                    item.setData(rewriteEntry.newItemMeta);
                }
                item.setIdentifier(rewriteEntry.newItemID);
            }
        }

        return item;
    }

    @Override
    public Item handleItemToServer(final Item item) {
        if (item == null) return null;

        for (NonExistentEntry nonExistentEntry : this.nonExistentItems) {
            if (nonExistentEntry.rewrites(item)) {
                item.setIdentifier(1);
                item.setData((short) 0);
                return item;
            }
        }

        this.setRemappedTagWrite(item);

        return item;
    }


    private PacketHandler itemToClientHandler(Type<Item> type) {
        return wrapper -> handleItemToClient(wrapper.get(type, 0));
    }

    private PacketHandler itemToServerHandler(Type<Item> type) {
        return wrapper -> handleItemToServer(wrapper.get(type, 0));
    }

    private void setRemappedNameRead(final Item item, final String name) {
        //Set protocol hack tag for later remapping
        final CompoundTag protocolHackTag = (item.tag() != null && item.tag().contains(tagName) ? item.tag().get(tagName) : new CompoundTag());
        if (item.tag() == null || !item.tag().contains(tagName)) {
            protocolHackTag.put("Id", new IntTag(item.identifier()));
            protocolHackTag.put("Meta", new ShortTag(item.data()));
        }

        //Get Item tag
        CompoundTag tag = item.tag();
        if (tag == null) {
            tag = new CompoundTag();
            item.setTag(tag);
            protocolHackTag.put("RemoveTag", new IntTag(0));
        }
        tag.put(tagName, protocolHackTag);

        //Set name/lore of item
        CompoundTag display = tag.get("display");
        if (display == null) {
            display = new CompoundTag();
            tag.put("display", display);
            protocolHackTag.put("RemoveDisplayTag", new IntTag(0));
        }
        if (display.contains("Name")) {
            ListTag lore = display.get("Lore");
            if (lore == null) {
                lore = new ListTag();
                display.put("Lore", lore);
                protocolHackTag.put("RemoveLore", new IntTag(0));
            }
            lore.add(new StringTag("§r " + this.protocolName + " Item ID: " + item.identifier() + " (" + name + ")"));
            protocolHackTag.put("RemoveLastLore", new IntTag(0));
        } else {
            display.put("Name", new StringTag("§r" + this.protocolName + " " + name));
            protocolHackTag.put("RemoveDisplayName", new IntTag(0));
        }
    }

    private void setRemappedTagWrite(final Item item) {
        if (item.tag() == null) return;
        if (!item.tag().contains(tagName)) return;

        final CompoundTag tag = item.tag();
        final CompoundTag protocolHackTag = tag.get(tagName);
        tag.remove(tagName);

        item.setIdentifier(((IntTag) protocolHackTag.get("Id")).asInt());
        item.setData(((ShortTag) protocolHackTag.get("Meta")).asShort());
        if (protocolHackTag.contains("RemoveLastLore")) {
            ListTag lore = ((CompoundTag) tag.get("display")).get("Lore");
            List<Tag> tags = lore.getValue();
            tags.remove(lore.size() - 1);
            lore.setValue(tags);
        }
        if (protocolHackTag.contains("RemoveLore")) {
            ((CompoundTag) tag.get("display")).remove("Lore");
        }
        if (protocolHackTag.contains("RemoveDisplayName")) {
            ((CompoundTag) tag.get("display")).remove("Name");
        }
        if (protocolHackTag.contains("RemoveDisplayTag")) {
            tag.remove("display");
        }
        if (protocolHackTag.contains("RemoveTag")) {
            item.setTag(null);
        }
    }


    private record RewriteEntry(int oldItemID, short oldItemMeta, int newItemID, short newItemMeta,
                                String newItemName) {

        public boolean rewrites(final Item item) {
            return item.identifier() == this.oldItemID && (this.oldItemMeta == -1 || this.oldItemMeta == item.data());
        }
    }


    private record NonExistentEntry(int itemId, short itemMeta) {

        public boolean rewrites(final Item item) {
            return item.identifier() == this.itemId && (this.itemMeta == -1 || this.itemMeta == item.data());
        }
    }
}
