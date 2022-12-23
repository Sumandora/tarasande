/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.sound;

import de.florianmichael.vialegacy.api.sound.SoundRewriter;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.Protocol1_7_0_1_preto1_6_4;

public class SoundRewriter1_7_0_5to1_6_4 extends SoundRewriter<Protocol1_7_0_1_preto1_6_4> {

    public SoundRewriter1_7_0_5to1_6_4(Protocol1_7_0_1_preto1_6_4 protocol) {
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
