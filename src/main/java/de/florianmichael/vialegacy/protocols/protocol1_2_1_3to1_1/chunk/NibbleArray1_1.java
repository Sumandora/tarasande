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

package de.florianmichael.vialegacy.protocols.protocol1_2_1_3to1_1.chunk;

public class NibbleArray1_1 {

	public NibbleArray1_1(int i) {
		data = new byte[i >> 1];
	}

	public int get(int i, int j, int k) {
		int l = i << 11 | k << 7 | j;
		int i1 = l >> 1;
		int j1 = l & 1;
		if (j1 == 0) {
			return data[i1] & 0xf;
		} else {
			return data[i1] >> 4 & 0xf;
		}
	}

	public void set(int i, int j, int k, int l) {
		int i1 = i << 11 | k << 7 | j;
		int j1 = i1 >> 1;
		int k1 = i1 & 1;
		if (k1 == 0) {
			data[j1] = (byte) (data[j1] & 0xf0 | l & 0xf);
		} else {
			data[j1] = (byte) (data[j1] & 0xf | (l & 0xf) << 4);
		}
	}

	public final byte data[];
}
