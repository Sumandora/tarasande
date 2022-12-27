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

package de.florianmichael.vialegacy;

import de.florianmichael.vialegacy.api.ViaLegacyPlatform;

/**
 * ViaLegacy is a rewrite of PrivateViaForge (2020 - 2021)
 
 * BUG TRACKER:
 *  - 1.1 Chunks needs a rewrite
 *  - 1.6.4 Login is totally broken and unclear
 *  - 1.5.2 Minecart logic is missing
 *  - 1.0 Chat filtering is missing
 *  - Biome data Remapping is missing (needs st. like AbstractChunkTracker)
 *  - 1.7.5 (?): Bed flag
 *  - 1.6.4 (?): Ender pearl
 *  - 1.6.4 (?): Book writing
 */
public class ViaLegacy {
    private static ViaLegacyPlatform platform;

    public static void init(final ViaLegacyPlatform platform) {
        ViaLegacy.platform = platform;
    }

    public static ViaLegacyPlatform getPlatform() {
        return platform;
    }
}
