package de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.sound;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteBlockPitch {

    private final static List<String> instrumentList = Arrays.asList("harp", "bd", "snare", "hat", "bassattack");
    private final static Map<Short, Short> pitchList = new HashMap<>();

    static {
        pitchList.put((short) 0, (short) 31);
        pitchList.put((short) 1, (short) 33);
        pitchList.put((short) 2, (short) 35);
        pitchList.put((short) 3, (short) 37);
        pitchList.put((short) 4, (short) 39);
        pitchList.put((short) 5, (short) 42);
        pitchList.put((short) 6, (short) 44);
        pitchList.put((short) 7, (short) 47);
        pitchList.put((short) 8, (short) 50);
        pitchList.put((short) 9, (short) 52);
        pitchList.put((short) 10, (short) 56);
        pitchList.put((short) 11, (short) 59);
        pitchList.put((short) 12, (short) 63);
        pitchList.put((short) 13, (short) 66);
        pitchList.put((short) 14, (short) 70);
        pitchList.put((short) 15, (short) 74);
        pitchList.put((short) 16, (short) 79);
        pitchList.put((short) 17, (short) 84);
        pitchList.put((short) 18, (short) 89);
        pitchList.put((short) 19, (short) 94);
        pitchList.put((short) 20, (short) 100);
        pitchList.put((short) 21, (short) 105);
        pitchList.put((short) 22, (short) 112);
        pitchList.put((short) 23, (short) 118);
        pitchList.put((short) 24, (short) 126);
    }

    public static short getPitch(final short value) {
        if (pitchList.containsKey(value)) {
            return pitchList.get(value);
        }

        return value;
    }

    public static int limitInstrument(final int instrument) {
        if (instrument < 0 || instrument >= instrumentList.size()) {
            return 0;
        }

        return instrument;
    }

    public static String getInstrument(final int instrument) {
        final String name = instrumentList.get(instrument);
        if (name == null) {
            return null;
        }

        return "note." + name;
    }
}
