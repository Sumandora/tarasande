package de.florianmichael.viacursed.protocol.protocol1_16to20w14infinite.data;

import com.viaversion.viaversion.libs.fastutil.ints.IntArrayList;
import com.viaversion.viaversion.libs.fastutil.ints.IntList;

public class BiomeData20w14infinite {

    private static final IntList VALID_BIOMES = new IntArrayList();

    static {
        VALID_BIOMES.add(0);
        VALID_BIOMES.add(33);
        VALID_BIOMES.add(132);
        VALID_BIOMES.add(165);
        VALID_BIOMES.add(134);
        VALID_BIOMES.add(35);
        VALID_BIOMES.add(130);
        VALID_BIOMES.add(133);
        VALID_BIOMES.add(31);
        VALID_BIOMES.add(32);
        VALID_BIOMES.add(129);
        VALID_BIOMES.add(34);
        VALID_BIOMES.add(36);
        VALID_BIOMES.add(131);
        VALID_BIOMES.add(169);
        VALID_BIOMES.add(29);
        VALID_BIOMES.add(161);
        VALID_BIOMES.add(37);
        VALID_BIOMES.add(167);
        VALID_BIOMES.add(171);
        VALID_BIOMES.add(163);
        VALID_BIOMES.add(1);
        VALID_BIOMES.add(3);
        VALID_BIOMES.add(7);
        VALID_BIOMES.add(5);
        VALID_BIOMES.add(140);
        VALID_BIOMES.add(173);
        VALID_BIOMES.add(156);
        VALID_BIOMES.add(26);
        VALID_BIOMES.add(23);
        VALID_BIOMES.add(39);
        VALID_BIOMES.add(27);
        VALID_BIOMES.add(38);
        VALID_BIOMES.add(127);
        VALID_BIOMES.add(42);
        VALID_BIOMES.add(43);
        VALID_BIOMES.add(30);
        VALID_BIOMES.add(22);
        VALID_BIOMES.add(2);
        VALID_BIOMES.add(6);
        VALID_BIOMES.add(14);
        VALID_BIOMES.add(10);
        VALID_BIOMES.add(168);
        VALID_BIOMES.add(164);
        VALID_BIOMES.add(160);
        VALID_BIOMES.add(172);
        VALID_BIOMES.add(50);
        VALID_BIOMES.add(49);
        VALID_BIOMES.add(16);
        VALID_BIOMES.add(17);
        VALID_BIOMES.add(149);
        VALID_BIOMES.add(18);
        VALID_BIOMES.add(47);
        VALID_BIOMES.add(48);
        VALID_BIOMES.add(46);
        VALID_BIOMES.add(13);
        VALID_BIOMES.add(151);
        VALID_BIOMES.add(19);
        VALID_BIOMES.add(21);
        VALID_BIOMES.add(175);
        VALID_BIOMES.add(15);
        VALID_BIOMES.add(155);
        VALID_BIOMES.add(11);
        VALID_BIOMES.add(25);
        VALID_BIOMES.add(8);
        VALID_BIOMES.add(9);
        VALID_BIOMES.add(157);
        VALID_BIOMES.add(24);
        VALID_BIOMES.add(45);
        VALID_BIOMES.add(44);
        VALID_BIOMES.add(41);
        VALID_BIOMES.add(40);
        VALID_BIOMES.add(4);
        VALID_BIOMES.add(12);
        VALID_BIOMES.add(28);
        VALID_BIOMES.add(20);
        VALID_BIOMES.add(158);
        VALID_BIOMES.add(166);
        VALID_BIOMES.add(170);
        VALID_BIOMES.add(174);
        VALID_BIOMES.add(162);
    }

    public static boolean isValid(final int biomeId) {
        return VALID_BIOMES.contains(biomeId);
    }

}
