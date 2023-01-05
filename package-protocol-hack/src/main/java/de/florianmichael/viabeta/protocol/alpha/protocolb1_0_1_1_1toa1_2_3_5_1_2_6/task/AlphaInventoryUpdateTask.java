package de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.task;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.Protocolb1_0_1_1_1toa1_2_3_5_1_2_6;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.ServerboundPacketsa1_2_6;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.provider.AlphaInventoryProvider;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.storage.InventoryStorage;
import de.florianmichael.viabeta.protocol.beta.protocolb1_2_0_2tob1_1_2.ServerboundPacketsb1_1;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.Type1_4_2;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

public class AlphaInventoryUpdateTask implements Runnable {

    @Override
    public void run() {
        for (UserConnection info : Via.getManager().getConnectionManager().getConnections()) {
            final InventoryStorage inventoryStorage = info.get(InventoryStorage.class);
            if (inventoryStorage == null) continue;

            try {
                final Item[] mainInventory = Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.fixItems(Via.getManager().getProviders().get(AlphaInventoryProvider.class).getMainInventoryItems(info));
                final Item[] craftingInventory = Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.fixItems(Via.getManager().getProviders().get(AlphaInventoryProvider.class).getCraftingInventoryItems(info));
                final Item[] armorInventory = Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.fixItems(Via.getManager().getProviders().get(AlphaInventoryProvider.class).getArmorInventoryItems(info));
                final Item handItem = Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.fixItem(Via.getManager().getProviders().get(AlphaInventoryProvider.class).getHandItem(info));

                if (!Objects.equals(handItem, inventoryStorage.handItem)) {
                    final PacketWrapper heldItemChange = PacketWrapper.create(ServerboundPacketsb1_1.HELD_ITEM_CHANGE, info);
                    heldItemChange.write(Type.SHORT, inventoryStorage.selectedHotbarSlot); // slot
                    heldItemChange.sendToServer(Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.class, false);
                }

                final Item[] mergedMainInventory = Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.copyItems(inventoryStorage.mainInventory);
                final Item[] mergedCraftingInventory = Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.copyItems(inventoryStorage.craftingInventory);
                final Item[] mergedArmorInventory = Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.copyItems(inventoryStorage.armorInventory);
                System.arraycopy(mainInventory, 0, mergedMainInventory, 0, mainInventory.length);
                System.arraycopy(craftingInventory, 0, mergedCraftingInventory, 0, craftingInventory.length);
                System.arraycopy(armorInventory, 0, mergedArmorInventory, 0, armorInventory.length);

                boolean hasChanged = !Arrays.equals(mergedMainInventory, inventoryStorage.mainInventory) || !Arrays.equals(mergedCraftingInventory, inventoryStorage.craftingInventory) || !Arrays.equals(mergedArmorInventory, inventoryStorage.armorInventory);
                if (!hasChanged) continue;

                inventoryStorage.mainInventory = Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.copyItems(mergedMainInventory);
                inventoryStorage.craftingInventory = Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.copyItems(mergedCraftingInventory);
                inventoryStorage.armorInventory = Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.copyItems(mergedArmorInventory);

                final PacketWrapper mainContent = PacketWrapper.create(ServerboundPacketsa1_2_6.PLAYER_INVENTORY, info);
                mainContent.write(Type.INT, -1); // type
                mainContent.write(Type1_4_2.NBTLESS_ITEM_ARRAY, mergedMainInventory); // items

                final PacketWrapper craftingContent = PacketWrapper.create(ServerboundPacketsa1_2_6.PLAYER_INVENTORY, info);
                craftingContent.write(Type.INT, -2); // type
                craftingContent.write(Type1_4_2.NBTLESS_ITEM_ARRAY, mergedCraftingInventory); // items

                final PacketWrapper armorContent = PacketWrapper.create(ServerboundPacketsa1_2_6.PLAYER_INVENTORY, info);
                armorContent.write(Type.INT, -3); // type
                armorContent.write(Type1_4_2.NBTLESS_ITEM_ARRAY, mergedArmorInventory); // items

                mainContent.sendToServer(Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.class);
                craftingContent.sendToServer(Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.class);
                armorContent.sendToServer(Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.class);
            } catch (Throwable e) {
                ViaBeta.getPlatform().getLogger().log(Level.WARNING, "Error sending inventory update packets", e);
            }
        }
    }

}
