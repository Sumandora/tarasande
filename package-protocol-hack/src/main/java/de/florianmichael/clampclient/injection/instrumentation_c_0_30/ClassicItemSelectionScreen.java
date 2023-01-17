package de.florianmichael.clampclient.injection.instrumentation_c_0_30;

import com.viaversion.viaversion.api.connection.UserConnection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
public class ClassicItemSelectionScreen extends Screen {

    public static ClassicItemSelectionScreen INSTANCE = new ClassicItemSelectionScreen();

    private final static int MAX_ROW_DIVIDER = 9;
    private final static int ITEM_XY_BOX_DIMENSION_CLASSIC = 25;
    private final static int SIDE_OFFSET = 15;
    private final static int ITEM_XY_BOX_DIMENSION_MODERN = 16;

    public Item[][] itemGrid = null;
    public ItemStack selectedItem = null;

    public ClassicItemSelectionScreen() {
        super(Text.literal("Classic item selection"));
    }

    public void reload(final UserConnection userConnection) {
        final List<Item> allowedItems = ClassicItemSplitter.get(userConnection);

        itemGrid = new Item[MathHelper.ceil(allowedItems.size() / (double) MAX_ROW_DIVIDER)][MAX_ROW_DIVIDER];
        int x = 0;
        int y = 0;
        for (Item allowedItem : allowedItems) {
            itemGrid[y][x] = allowedItem;
            x++;
            if (x == MAX_ROW_DIVIDER) {
                x = 0;
                y++;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedItem != null) {
            this.client.interactionManager.clickCreativeStack(selectedItem, MinecraftClient.getInstance().player.getInventory().selectedSlot + 36); // Beta Inventory Tracker

            this.client.player.getInventory().main.set(MinecraftClient.getInstance().player.getInventory().selectedSlot, selectedItem);
            this.client.player.playerScreenHandler.sendContentUpdates();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        final int halfWidth = this.width / 2;
        final int halfHeight = this.height / 2;

        final int boxWidth = ITEM_XY_BOX_DIMENSION_CLASSIC * MAX_ROW_DIVIDER + SIDE_OFFSET * 2;
        final int boxHeight = ITEM_XY_BOX_DIMENSION_CLASSIC * itemGrid.length + SIDE_OFFSET * 2 + SIDE_OFFSET;

        final int renderX = halfWidth - boxWidth / 2;
        final int renderY = halfHeight - boxHeight / 2;

        fill(matrices, renderX, renderY, renderX + boxWidth, renderY + boxHeight, Integer.MIN_VALUE);
        drawCenteredText(matrices, textRenderer, "Select block", renderX + boxWidth / 2, renderY + SIDE_OFFSET, -1);
        selectedItem = null;

        int y = SIDE_OFFSET + SIDE_OFFSET;
        for (Item[] items : itemGrid) {
            int x = SIDE_OFFSET;
            for (Item item : items) {
                if (item == null) continue;

                if (mouseX > renderX + x && mouseY > renderY + y && mouseX < renderX + x + ITEM_XY_BOX_DIMENSION_CLASSIC && mouseY < renderY + y + ITEM_XY_BOX_DIMENSION_CLASSIC) {
                    fill(matrices, renderX + x, renderY + y, renderX + x + ITEM_XY_BOX_DIMENSION_CLASSIC, renderY + y + ITEM_XY_BOX_DIMENSION_CLASSIC, Integer.MAX_VALUE);
                    selectedItem = item.getDefaultStack();
                }
                MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(item.getDefaultStack(), renderX + x + ITEM_XY_BOX_DIMENSION_MODERN / 4, renderY + y + ITEM_XY_BOX_DIMENSION_MODERN / 4);
                x += ITEM_XY_BOX_DIMENSION_CLASSIC;
            }
            y += ITEM_XY_BOX_DIMENSION_CLASSIC;
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}
