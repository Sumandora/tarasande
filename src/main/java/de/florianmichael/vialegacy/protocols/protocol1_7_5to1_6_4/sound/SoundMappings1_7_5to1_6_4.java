package de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.sound;

import java.util.HashMap;
import java.util.Map;

public class SoundMappings1_7_5to1_6_4 {

    public static final Map<String, String> soundDiff = new HashMap<>();

    static {
        soundDiff.put("liquid.swim", "game.neutral.swim");
        soundDiff.put("random.breath", "");
        soundDiff.put("random.glass", "dig.glass");
        soundDiff.put("damage.hit", "game.neutral.hurt");
        soundDiff.put("random.fuse", "creeper.primed");
        soundDiff.put("random.classic_hurt", "game.neutral.hurt");
        soundDiff.put("damage.fallbig", "game.neutral.hurt.fall.big");
        soundDiff.put("liquid.splash", "game.neutral.swim.splash");
        soundDiff.put("damage.fallsmall", "game.neutral.hurt.fall.small");
    }
}
