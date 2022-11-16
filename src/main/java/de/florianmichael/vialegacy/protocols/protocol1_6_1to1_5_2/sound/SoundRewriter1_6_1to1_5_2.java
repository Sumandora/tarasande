package de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.sound;

import de.florianmichael.vialegacy.api.sound.SoundRewriter;

public class SoundRewriter1_6_1to1_5_2 extends SoundRewriter {

    @Override
    public String rewrite(String tag) {
        if (tag.equals("mob.ghast.affectionate scream")) {
            return "mob.ghast.affectionate_scream";
        }
        return tag;
    }
}
