package de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.sound;

import de.florianmichael.vialegacy.api.sound.SoundRewriter;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.Protocol1_7_5to1_6_4;

public class SoundRewriter1_7_5to1_6_4 extends SoundRewriter<Protocol1_7_5to1_6_4> {

    public SoundRewriter1_7_5to1_6_4(Protocol1_7_5to1_6_4 protocol) {
        super(protocol);
    }

    @Override
    public String rewrite(String tag) {
        if (tag.equals("liquid.swim")) {
            return "game.neutral.swim";
        }
        if (tag.equals("random.breath")) {
            return "";
        }
        if (tag.equals("random.glass")) {
            return "dig.glass";
        }
        if (tag.equals("damage.hit")) {
            return "game.neutral.hurt";
        }
        if (tag.equals("random.fuse")) {
            return "creeper.primed";
        }
        if (tag.equals("random.classic_hurt")) {
            return "game.neutral.hurt";
        }
        if (tag.equals("damage.fallbig")) {
            return "game.neutral.hurt.fall.big";
        }
        if (tag.equals("liquid.splash")) {
            return "game.neutral.swim.splash";
        }
        if (tag.equals("damage.fallsmall")) {
            return "game.neutral.hurt.fall.small";
        }
        return tag;
    }
}
