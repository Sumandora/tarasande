package de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.sound;

import de.florianmichael.vialegacy.api.sound.SoundRewriter;
import de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.Protocol1_6_1to1_5_2;

public class SoundRewriter1_6_1to1_5_2 extends SoundRewriter<Protocol1_6_1to1_5_2> {

    public SoundRewriter1_6_1to1_5_2(Protocol1_6_1to1_5_2 protocol) {
        super(protocol);
    }

    @Override
    public String rewrite(String tag) {
        if (tag.equals("mob.ghast.affectionate scream")) {
            return "mob.ghast.affectionate_scream";
        }
        return tag;
    }
}
