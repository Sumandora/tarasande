package de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.model;

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;

public class CraftingRecipe {

    private final int width;
    private final int height;
    private final int[] ingredientMap;
    private final Item resultItem;

    public CraftingRecipe(final int width, final int height, final int[] ingredientMap, final Item resultItem) {
        this.width = width;
        this.height = height;
        this.ingredientMap = ingredientMap;
        this.resultItem = resultItem;
    }

    public boolean matches(final int[] ingredientMap) {
        for (int x = 0; x <= 3 - this.width; x++) {
            for (int y = 0; y <= 3 - this.height; y++) {
                if (this.matches(ingredientMap, x, y, true)) {
                    return true;
                } else if (this.matches(ingredientMap, x, y, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(final int[] ingredientMap, final int x, final int y, final boolean b) {
        for (int xx = 0; xx < 3; xx++) {
            for (int yy = 0; yy < 3; yy++) {
                final int rx = xx - x;
                final int ry = yy - y;
                int var9 = -1;
                if (rx >= 0 && ry >= 0 && rx < this.width && ry < this.height) {
                    if (b) {
                        var9 = this.ingredientMap[this.width - rx - 1 + ry * this.width];
                    } else {
                        var9 = this.ingredientMap[rx + ry * this.width];
                    }
                }

                if (ingredientMap[xx + yy * 3] != var9) {
                    return false;
                }
            }
        }

        return true;
    }

    public Item createResult() {
        return new DataItem(this.resultItem);
    }

    public int getRecipeSize() {
        return this.width * this.height;
    }
}
