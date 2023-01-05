package de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.data;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.fastutil.ints.*;
import de.florianmichael.viabeta.api.data.BlockList1_6;
import de.florianmichael.viabeta.api.model.IdAndData;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AlphaItems {

    private static final IntList ITEM_IDS = new IntArrayList();
    private static final Int2IntMap MAX_STACK_SIZE = new Int2IntOpenHashMap(70, 0.99F);
    private static final Int2ObjectMap<Consumer<Item>> INTERACT_ACTION = new Int2ObjectOpenHashMap<>(14, 0.99F);
    private static final Int2ObjectMap<BiConsumer<Item, IntObjectPair<IdAndData>>> PLACE_ACTION = new Int2ObjectOpenHashMap<>(15, 0.99F);

    static {
        ITEM_IDS.add(1);
        ITEM_IDS.add(2);
        ITEM_IDS.add(3);
        ITEM_IDS.add(4);
        ITEM_IDS.add(5);
        ITEM_IDS.add(6);
        ITEM_IDS.add(7);
        ITEM_IDS.add(8);
        ITEM_IDS.add(9);
        ITEM_IDS.add(10);
        ITEM_IDS.add(11);
        ITEM_IDS.add(12);
        ITEM_IDS.add(13);
        ITEM_IDS.add(14);
        ITEM_IDS.add(15);
        ITEM_IDS.add(16);
        ITEM_IDS.add(17);
        ITEM_IDS.add(18);
        ITEM_IDS.add(19);
        ITEM_IDS.add(20);
        ITEM_IDS.add(35);
        ITEM_IDS.add(37);
        ITEM_IDS.add(38);
        ITEM_IDS.add(39);
        ITEM_IDS.add(40);
        ITEM_IDS.add(41);
        ITEM_IDS.add(42);
        ITEM_IDS.add(43);
        ITEM_IDS.add(44);
        ITEM_IDS.add(45);
        ITEM_IDS.add(46);
        ITEM_IDS.add(47);
        ITEM_IDS.add(48);
        ITEM_IDS.add(49);
        ITEM_IDS.add(50);
        ITEM_IDS.add(51);
        ITEM_IDS.add(52);
        ITEM_IDS.add(53);
        ITEM_IDS.add(54);
        ITEM_IDS.add(55);
        ITEM_IDS.add(56);
        ITEM_IDS.add(57);
        ITEM_IDS.add(58);
        ITEM_IDS.add(59);
        ITEM_IDS.add(60);
        ITEM_IDS.add(61);
        ITEM_IDS.add(62);
        ITEM_IDS.add(63);
        ITEM_IDS.add(64);
        ITEM_IDS.add(65);
        ITEM_IDS.add(66);
        ITEM_IDS.add(67);
        ITEM_IDS.add(68);
        ITEM_IDS.add(69);
        ITEM_IDS.add(70);
        ITEM_IDS.add(71);
        ITEM_IDS.add(72);
        ITEM_IDS.add(73);
        ITEM_IDS.add(74);
        ITEM_IDS.add(75);
        ITEM_IDS.add(76);
        ITEM_IDS.add(77);
        ITEM_IDS.add(78);
        ITEM_IDS.add(79);
        ITEM_IDS.add(80);
        ITEM_IDS.add(81);
        ITEM_IDS.add(82);
        ITEM_IDS.add(83);
        ITEM_IDS.add(84);
        ITEM_IDS.add(85);
        ITEM_IDS.add(86);
        ITEM_IDS.add(87);
        ITEM_IDS.add(88);
        ITEM_IDS.add(89);
        ITEM_IDS.add(90);
        ITEM_IDS.add(91);
        ITEM_IDS.add(256);
        ITEM_IDS.add(257);
        ITEM_IDS.add(258);
        ITEM_IDS.add(259);
        ITEM_IDS.add(260);
        ITEM_IDS.add(261);
        ITEM_IDS.add(262);
        ITEM_IDS.add(263);
        ITEM_IDS.add(264);
        ITEM_IDS.add(265);
        ITEM_IDS.add(266);
        ITEM_IDS.add(267);
        ITEM_IDS.add(268);
        ITEM_IDS.add(269);
        ITEM_IDS.add(270);
        ITEM_IDS.add(271);
        ITEM_IDS.add(272);
        ITEM_IDS.add(273);
        ITEM_IDS.add(274);
        ITEM_IDS.add(275);
        ITEM_IDS.add(276);
        ITEM_IDS.add(277);
        ITEM_IDS.add(278);
        ITEM_IDS.add(279);
        ITEM_IDS.add(280);
        ITEM_IDS.add(281);
        ITEM_IDS.add(282);
        ITEM_IDS.add(283);
        ITEM_IDS.add(284);
        ITEM_IDS.add(285);
        ITEM_IDS.add(286);
        ITEM_IDS.add(287);
        ITEM_IDS.add(288);
        ITEM_IDS.add(289);
        ITEM_IDS.add(290);
        ITEM_IDS.add(291);
        ITEM_IDS.add(292);
        ITEM_IDS.add(293);
        ITEM_IDS.add(294);
        ITEM_IDS.add(295);
        ITEM_IDS.add(296);
        ITEM_IDS.add(297);
        ITEM_IDS.add(298);
        ITEM_IDS.add(299);
        ITEM_IDS.add(300);
        ITEM_IDS.add(301);
        ITEM_IDS.add(302);
        ITEM_IDS.add(303);
        ITEM_IDS.add(304);
        ITEM_IDS.add(305);
        ITEM_IDS.add(306);
        ITEM_IDS.add(307);
        ITEM_IDS.add(308);
        ITEM_IDS.add(309);
        ITEM_IDS.add(310);
        ITEM_IDS.add(311);
        ITEM_IDS.add(312);
        ITEM_IDS.add(313);
        ITEM_IDS.add(314);
        ITEM_IDS.add(315);
        ITEM_IDS.add(316);
        ITEM_IDS.add(317);
        ITEM_IDS.add(318);
        ITEM_IDS.add(319);
        ITEM_IDS.add(320);
        ITEM_IDS.add(321);
        ITEM_IDS.add(322);
        ITEM_IDS.add(323);
        ITEM_IDS.add(324);
        ITEM_IDS.add(325);
        ITEM_IDS.add(326);
        ITEM_IDS.add(327);
        ITEM_IDS.add(328);
        ITEM_IDS.add(329);
        ITEM_IDS.add(330);
        ITEM_IDS.add(331);
        ITEM_IDS.add(332);
        ITEM_IDS.add(333);
        ITEM_IDS.add(334);
        ITEM_IDS.add(335);
        ITEM_IDS.add(336);
        ITEM_IDS.add(337);
        ITEM_IDS.add(338);
        ITEM_IDS.add(339);
        ITEM_IDS.add(340);
        ITEM_IDS.add(341);
        ITEM_IDS.add(342);
        ITEM_IDS.add(343);
        ITEM_IDS.add(344);
        ITEM_IDS.add(345);
        ITEM_IDS.add(346);
        ITEM_IDS.add(347);
        ITEM_IDS.add(348);
        ITEM_IDS.add(349);
        ITEM_IDS.add(350);
        ITEM_IDS.add(2256);
        ITEM_IDS.add(2257);

        MAX_STACK_SIZE.defaultReturnValue(64);
        MAX_STACK_SIZE.put(256, 1);
        MAX_STACK_SIZE.put(257, 1);
        MAX_STACK_SIZE.put(258, 1);
        MAX_STACK_SIZE.put(259, 1);
        MAX_STACK_SIZE.put(260, 1);
        MAX_STACK_SIZE.put(261, 1);
        MAX_STACK_SIZE.put(267, 1);
        MAX_STACK_SIZE.put(268, 1);
        MAX_STACK_SIZE.put(269, 1);
        MAX_STACK_SIZE.put(270, 1);
        MAX_STACK_SIZE.put(271, 1);
        MAX_STACK_SIZE.put(272, 1);
        MAX_STACK_SIZE.put(273, 1);
        MAX_STACK_SIZE.put(274, 1);
        MAX_STACK_SIZE.put(275, 1);
        MAX_STACK_SIZE.put(276, 1);
        MAX_STACK_SIZE.put(277, 1);
        MAX_STACK_SIZE.put(278, 1);
        MAX_STACK_SIZE.put(279, 1);
        MAX_STACK_SIZE.put(282, 1);
        MAX_STACK_SIZE.put(283, 1);
        MAX_STACK_SIZE.put(284, 1);
        MAX_STACK_SIZE.put(285, 1);
        MAX_STACK_SIZE.put(286, 1);
        MAX_STACK_SIZE.put(290, 1);
        MAX_STACK_SIZE.put(291, 1);
        MAX_STACK_SIZE.put(292, 1);
        MAX_STACK_SIZE.put(293, 1);
        MAX_STACK_SIZE.put(294, 1);
        MAX_STACK_SIZE.put(297, 1);
        MAX_STACK_SIZE.put(298, 1);
        MAX_STACK_SIZE.put(299, 1);
        MAX_STACK_SIZE.put(300, 1);
        MAX_STACK_SIZE.put(301, 1);
        MAX_STACK_SIZE.put(302, 1);
        MAX_STACK_SIZE.put(303, 1);
        MAX_STACK_SIZE.put(304, 1);
        MAX_STACK_SIZE.put(305, 1);
        MAX_STACK_SIZE.put(306, 1);
        MAX_STACK_SIZE.put(307, 1);
        MAX_STACK_SIZE.put(308, 1);
        MAX_STACK_SIZE.put(309, 1);
        MAX_STACK_SIZE.put(310, 1);
        MAX_STACK_SIZE.put(311, 1);
        MAX_STACK_SIZE.put(312, 1);
        MAX_STACK_SIZE.put(313, 1);
        MAX_STACK_SIZE.put(314, 1);
        MAX_STACK_SIZE.put(315, 1);
        MAX_STACK_SIZE.put(316, 1);
        MAX_STACK_SIZE.put(317, 1);
        MAX_STACK_SIZE.put(319, 1);
        MAX_STACK_SIZE.put(320, 1);
        MAX_STACK_SIZE.put(322, 1);
        MAX_STACK_SIZE.put(323, 1);
        MAX_STACK_SIZE.put(324, 1);
        MAX_STACK_SIZE.put(325, 1);
        MAX_STACK_SIZE.put(326, 1);
        MAX_STACK_SIZE.put(327, 1);
        MAX_STACK_SIZE.put(328, 1);
        MAX_STACK_SIZE.put(329, 1);
        MAX_STACK_SIZE.put(330, 1);
        MAX_STACK_SIZE.put(332, 16);
        MAX_STACK_SIZE.put(333, 1);
        MAX_STACK_SIZE.put(335, 1);
        MAX_STACK_SIZE.put(342, 1);
        MAX_STACK_SIZE.put(343, 1);
        MAX_STACK_SIZE.put(349, 1);
        MAX_STACK_SIZE.put(350, 1);
        MAX_STACK_SIZE.put(2256, 1);
        MAX_STACK_SIZE.put(2257, 1);

        INTERACT_ACTION.defaultReturnValue(Item::identifier); // no op
        INTERACT_ACTION.put(260, i -> i.setAmount(i.amount() - 1));
        INTERACT_ACTION.put(297, i -> i.setAmount(i.amount() - 1));
        INTERACT_ACTION.put(319, i -> i.setAmount(i.amount() - 1));
        INTERACT_ACTION.put(320, i -> i.setAmount(i.amount() - 1));
        INTERACT_ACTION.put(322, i -> i.setAmount(i.amount() - 1));
        INTERACT_ACTION.put(326, i -> i.setIdentifier(325));
        INTERACT_ACTION.put(327, i -> i.setIdentifier(325));
        INTERACT_ACTION.put(332, i -> i.setAmount(i.amount() - 1));
        INTERACT_ACTION.put(333, i -> i.setAmount(i.amount() - 1));
        INTERACT_ACTION.put(335, i -> i.setIdentifier(325));
        INTERACT_ACTION.put(346, i -> {
            i.setData((short) (i.data() + 1));
            if (i.data() > 64) {
                i.setAmount(i.amount() - 1);
            }
        });
        INTERACT_ACTION.put(349, i -> i.setAmount(i.amount() - 1));
        INTERACT_ACTION.put(350, i -> i.setAmount(i.amount() - 1));
        INTERACT_ACTION.put(282, i -> {
            i.setIdentifier(281);
            i.setAmount(1);
            i.setData((short) 0);
        });

        PLACE_ACTION.defaultReturnValue((i, d) -> i.identifier()); // no op
        PLACE_ACTION.put(259, (i, d) -> {
            i.setData((short) (i.data() + 1));
            if (i.data() > 64) {
                i.setAmount(i.amount() - 1);
            }
        });
        PLACE_ACTION.put(290, (i, d) -> {
            if (d.value().id == BlockList1_6.grass.blockID || d.value().id == BlockList1_6.dirt.blockID) {
                i.setData((short) (i.data() + 1));
                if (i.data() > 32) {
                    i.setAmount(i.amount() - 1);
                }
            }
        });
        PLACE_ACTION.put(291, (i, d) -> {
            if (d.value().id == BlockList1_6.grass.blockID || d.value().id == BlockList1_6.dirt.blockID) {
                i.setData((short) (i.data() + 1));
                if (i.data() > 64) {
                    i.setAmount(i.amount() - 1);
                }
            }
        });
        PLACE_ACTION.put(292, (i, d) -> {
            if (d.value().id == BlockList1_6.grass.blockID || d.value().id == BlockList1_6.dirt.blockID) {
                i.setData((short) (i.data() + 1));
                if (i.data() > 128) {
                    i.setAmount(i.amount() - 1);
                }
            }
        });
        PLACE_ACTION.put(293, (i, d) -> {
            if (d.value().id == BlockList1_6.grass.blockID || d.value().id == BlockList1_6.dirt.blockID) {
                i.setData((short) (i.data() + 1));
                if (i.data() > 256) {
                    i.setAmount(i.amount() - 1);
                }
            }
        });
        PLACE_ACTION.put(294, (i, d) -> {
            if (d.value().id == BlockList1_6.grass.blockID || d.value().id == BlockList1_6.dirt.blockID) {
                i.setData((short) (i.data() + 1));
                if (i.data() > 64) {
                    i.setAmount(i.amount() - 1);
                }
            }
        });
        PLACE_ACTION.put(295, (i, d) -> {
            if (d.keyInt() == 1 && d.value().id == BlockList1_6.tilledField.blockID) {
                i.setAmount(i.amount() - 1);
            }
        });
        PLACE_ACTION.put(321, (i, d) -> {
            if (d.keyInt() != 0 && d.keyInt() != 1) i.setAmount(i.amount() - 1);
        });
        PLACE_ACTION.put(324, (i, d) -> {
            if (d.keyInt() == 1) i.setAmount(i.amount() - 1);
        });
        PLACE_ACTION.put(328, (i, d) -> {
            if (d.value().id == BlockList1_6.rail.blockID) {
                i.setAmount(i.amount() - 1);
            }
        });
        PLACE_ACTION.put(330, (i, d) -> {
            if (d.keyInt() == 1) i.setAmount(i.amount() - 1);
        });
        PLACE_ACTION.put(342, (i, d) -> {
            if (d.value().id == BlockList1_6.rail.blockID) {
                i.setAmount(i.amount() - 1);
            }
        });
        PLACE_ACTION.put(343, (i, d) -> {
            if (d.value().id == BlockList1_6.rail.blockID) {
                i.setAmount(i.amount() - 1);
            }
        });
        PLACE_ACTION.put(2256, (i, d) -> {
            if (d.value().id == BlockList1_6.jukebox.blockID && d.value().data == 0) {
                i.setAmount(i.amount() - 1);
            }
        });
        PLACE_ACTION.put(2257, (i, d) -> {
            if (d.value().id == BlockList1_6.jukebox.blockID && d.value().data == 0) {
                i.setAmount(i.amount() - 1);
            }
        });
    }

    public static boolean isValid(final int itemId) {
        return ITEM_IDS.contains(itemId);
    }

    public static int getMaxStackSize(final int itemId) {
        return MAX_STACK_SIZE.get(itemId);
    }

    public static void doInteract(final Item item) {
        INTERACT_ACTION.get(item.identifier()).accept(item);
    }

    public static void doPlace(final Item item, final short direction, final IdAndData placedAgainst) {
        PLACE_ACTION.get(item.identifier()).accept(item, new IntObjectImmutablePair<>(direction, placedAgainst));
    }
}
