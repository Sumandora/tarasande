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

package de.florianmichael.vialegacy.protocols.protocol1_4_0_2to1_3_1_2.sound;

import de.florianmichael.vialegacy.api.sound.SoundRewriter;
import de.florianmichael.vialegacy.protocols.protocol1_4_0_2to1_3_1_2.Protocol1_4_0_2to1_3_1_2;

public class SoundRewriter1_4_0_2to1_3_1_2 extends SoundRewriter<Protocol1_4_0_2to1_3_1_2> {

    public SoundRewriter1_4_0_2to1_3_1_2(Protocol1_4_0_2to1_3_1_2 protocol) {
        super(protocol);
    }

    @Override
    public String rewrite(String tag) {
        if (tag.equals("mob.ghast.affectionate scream")) return "mob.ghast.affectionate_scream";
        if (tag.equals("mob.sheep")) return "mob.sheep.say";
        if (tag.equals("mob.spider")) return "mob.spider.yay";
        if (tag.equals("mob.chickenplop")) return "mob.chicken.plop";
        if (tag.equals("mob.pig")) return "mob.pig.say";
        if (tag.equals("mob.pigdeath")) return "mob.pig.death";
        if (tag.equals("random.hurt")) return "damage.hit";
        if (tag.equals("mob.chicken")) return "mob.chicken.say";
        if (tag.equals("damage.hurtflesh")) return "damage.hit";
        if (tag.equals("mob.zombie")) return "mob.zombie.say";
        if (tag.equals("mob.zombiehurt")) return "mob.zombie.hurt";
        if (tag.equals("mob.spiderdeath")) return "mob.spider.death";
        if (tag.equals("random.old_explode")) return "random.explode";
        if (tag.equals("mob.skeletondeath")) return "mob.skeleton.death";
        if (tag.equals("mob.skeleton")) return "mob.skeleton.say";
        if (tag.equals("mob.slimeattack")) return "mob.slime.attack";
        if (tag.equals("random.drr")) return ""; // not implemented anymore
        if (tag.equals("mob.zombiedeath")) return "mob.zombie.death";
        if (tag.equals("mob.creeper")) return "mob.creeper.say";
        if (tag.equals("mob.cow")) return "mob.cow.say";
        if (tag.equals("mob.creeperdeath")) return "mob.creeper.death";
        if (tag.equals("mob.slime")) return "mob.slime.small";
        if (tag.equals("mob.skeletonhurt")) return "mob.skeleton.hurt";
        if (tag.equals("mob.chickenhurt")) return "mob.chicken.hurt";
        if (tag.equals("mob.cowhurt")) return "mob.cow.hurt";

        return tag;
    }
}
