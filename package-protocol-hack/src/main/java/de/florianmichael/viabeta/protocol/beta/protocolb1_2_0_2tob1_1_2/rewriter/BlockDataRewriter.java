package de.florianmichael.viabeta.protocol.beta.protocolb1_2_0_2tob1_1_2.rewriter;

import de.florianmichael.viabeta.api.rewriter.AbstractBlockRemapper;
import de.florianmichael.viabeta.api.data.BlockList1_6;
import de.florianmichael.viabeta.api.model.IdAndData;

public class BlockDataRewriter extends AbstractBlockRemapper {

    public BlockDataRewriter() {
        for (int i = 1; i < 16; i++) { // cobblestone
            this.registerReplacement(new IdAndData(BlockList1_6.cobblestone.blockID, i), new IdAndData(BlockList1_6.cobblestone.blockID, 0));
        }
        for (int i = 1; i < 16; i++) { // sand
            this.registerReplacement(new IdAndData(BlockList1_6.sand.blockID, i), new IdAndData(BlockList1_6.sand.blockID, 0));
        }
        for (int i = 1; i < 16; i++) { // gravel
            this.registerReplacement(new IdAndData(BlockList1_6.gravel.blockID, i), new IdAndData(BlockList1_6.gravel.blockID, 0));
        }
        for (int i = 1; i < 16; i++) { // obsidian
            this.registerReplacement(new IdAndData(BlockList1_6.obsidian.blockID, i), new IdAndData(BlockList1_6.obsidian.blockID, 0));
        }
    }
}
