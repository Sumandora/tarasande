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

package de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.chunk;

import com.viaversion.viaversion.api.minecraft.chunks.NibbleArray;

public class ExtendedBlockStorage {

    private final byte[] blockLSBArray;
    private NibbleArray blockMSBArray;
    private final NibbleArray blockMetadataArray;
    private final NibbleArray blocklightArray;
    private NibbleArray skylightArray;

    public ExtendedBlockStorage(boolean paramBoolean) {
        this.blockLSBArray = new byte[4096];
        this.blockMetadataArray = new NibbleArray(this.blockLSBArray.length);
        this.blocklightArray = new NibbleArray(this.blockLSBArray.length);

        if (paramBoolean)
            this.skylightArray = new NibbleArray(this.blockLSBArray.length);

    }

    public byte[] getBlockLSBArray() {
        return this.blockLSBArray;
    }

    public boolean isEmpty() {
        return this.blockMSBArray==null;
    }

    public void clearMSBArray() {
        this.blockMSBArray = null;
    }

    public NibbleArray getBlockMSBArray() {
        return this.blockMSBArray;
    }

    public NibbleArray getMetadataArray() {
        return this.blockMetadataArray;
    }

    public NibbleArray getBlocklightArray() {
        return this.blocklightArray;
    }

    public NibbleArray getSkylightArray() {
        return this.skylightArray;
    }

    public NibbleArray createBlockMSBArray() {
        this.blockMSBArray = new NibbleArray(this.blockLSBArray.length);
        return this.blockMSBArray;
    }
}
