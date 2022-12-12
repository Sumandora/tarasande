// Ported to Java from Python source. Original repo: https://github.com/jakubcerveny/gilbert
/*
BSD 2-Clause License

Copyright (c) 2018, Jakub Červený
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package de.evilcodez.mazes.utils;

import java.util.ArrayList;
import java.util.List;

public class HilbertCurve {

    public static List<int[]> generate(int width, int height, int depth) {
        final List<int[]> ints = new ArrayList<>();
        if (width >= height && width >= depth) {
            generate(ints, 0, 0, 0,
                    width, 0, 0,
                    0, height, 0,
                    0, 0, depth);
        } else if (height >= width && height >= depth) {
            generate(ints, 0, 0, 0,
                    0, height, 0,
                    width, 0, 0,
                    0, 0, depth);
        } else {
            generate(ints, 0, 0, 0,
                    0, 0, depth,
                    width, 0, 0,
                    0, height, 0);
        }
        return ints;
    }

    private static int sign(final int x) {
        return Integer.compare(x, 0);
    }

    private static void generate(final List<int[]> ints, final int x, final int y, final int z,
                                 final int ax, final int ay, final int az,
                                 final int bx, final int by, final int bz,
                                 final int cx, final int cy, final int cz) {
        int w = Math.abs(ax + ay + az);
        int h = Math.abs(bx + by + bz);
        int d = Math.abs(cx + cy + cz);

        int[] da = new int[]{sign(ax), sign(ay), sign(az)};
        int[] db = new int[]{sign(bx), sign(by), sign(bz)};
        int[] dc = new int[]{sign(cx), sign(cy), sign(cz)};

        if (h == 1 && d == 1) {
            for (int i = 0; i < w; i++) ints.add(new int[]{x + da[0] * i, y + da[1] * i, z + da[2] * i});
            return;
        }
        if (w == 1 && d == 1) {
            for (int i = 0; i < h; i++) ints.add(new int[]{x + db[0] * i, y + db[1] * i, z + db[2] * i});
            return;
        }
        if (w == 1 && h == 1) {
            for (int i = 0; i < d; i++) ints.add(new int[]{x + dc[0] * i, y + dc[1] * i, z + dc[2] * i});
            return;
        }

        int[] a2 = new int[]{Math.floorDiv(ax, 2), Math.floorDiv(ay, 2), Math.floorDiv(az, 2)};
        int[] b2 = new int[]{Math.floorDiv(bx, 2), Math.floorDiv(by, 2), Math.floorDiv(bz, 2)};
        int[] c2 = new int[]{Math.floorDiv(cx, 2), Math.floorDiv(cy, 2), Math.floorDiv(cz, 2)};

        int w2 = Math.abs(a2[0] + a2[1] + a2[2]);
        int h2 = Math.abs(b2[0] + b2[1] + b2[2]);
        int d2 = Math.abs(c2[0] + c2[1] + c2[2]);

        if (w2 % 2 != 0 && w > 2) {
            a2[0] += da[0];
            a2[1] += da[1];
            a2[2] += da[2];
        }
        if (h2 % 2 != 0 && h > 2) {
            b2[0] += db[0];
            b2[1] += db[1];
            b2[2] += db[2];
        }
        if (d2 % 2 != 0 && d > 2) {
            c2[0] += dc[0];
            c2[1] += dc[1];
            c2[2] += dc[2];
        }

        if (2 * w > 3 * h && 2 * w > 3 * d) {
            generate(ints, x, y, z,
                    a2[0], a2[1], a2[2],
                    bx, by, bz,
                    cx, cy, cz);
            generate(ints, x + a2[0], y + a2[1], z + a2[2],
                    ax - a2[0], ay - a2[1], az - a2[2],
                    bx, by, bz,
                    cx, cy, cz);
        } else if (3 * h > 4 * d) {
            generate(ints, x, y, z,
                    b2[0], b2[1], b2[2],
                    cx, cy, cz,
                    a2[0], a2[1], a2[2]);
            generate(ints, x + b2[0], y + b2[1], z + b2[2],
                    ax, ay, az,
                    bx - b2[0], by - b2[1], bz - b2[2],
                    cx, cy, cz);
            generate(ints, x + (ax - da[0]) + (b2[0] - db[0]), y + (ay - da[1]) + (b2[1] - db[1]), z + (az - da[2]) + (b2[2] - db[2]),
                    -b2[0], -b2[1], -b2[2],
                    cx, cy, cz,
                    -(ax - a2[0]), -(ay - a2[1]), -(az - a2[2]));
        } else if (3 * d > 4 * h) {
            generate(ints, x, y, z,
                    c2[0], c2[1], c2[2],
                    a2[0], a2[1], a2[2],
                    bx, by, bz);
            generate(ints, x + c2[0], y + c2[1], z + c2[2],
                    ax, ay, az,
                    bx, by, bz,
                    cx - c2[0], cy - c2[1], cz - c2[2]);
            generate(ints, x + (ax - da[0]) + (c2[0] - dc[0]), y + (ay - da[1]) + (c2[1] - dc[1]), z + (az - da[2]) + (c2[2] - dc[2]),
                    -c2[0], -c2[1], -c2[2],
                    -(ax - a2[0]), -(ay - a2[1]), -(az - a2[2]),
                    bx, by, bz);
        } else {
            generate(ints, x, y, z,
                    b2[0], b2[1], b2[2],
                    c2[0], c2[1], c2[2],
                    a2[0], a2[1], a2[2]);
            generate(ints, x + b2[0], y + b2[1], z + b2[2],
                    cx, cy, cz,
                    a2[0], a2[1], a2[2],
                    bx - b2[0], by - b2[1], bz - b2[2]);
            generate(ints, x + (b2[0] - db[0]) + (cx - dc[0]), y + (b2[1] - db[1]) + (cy - dc[1]), z + (b2[2] - db[2]) + (cz - dc[2]),
                    ax, ay, az,
                    -b2[0], -b2[1], -b2[2],
                    -(cx - c2[0]), -(cy - c2[1]), -(cz - c2[2]));
            generate(ints, x + (ax - da[0]) + b2[0] + (cx - dc[0]), y + (ay - da[1]) + b2[1] + (cy - dc[1]), z + (az - da[2]) + b2[2] + (cz - dc[2]),
                    -cx, -cy, -cz,
                    -(ax - a2[0]), -(ay - a2[1]), -(az - a2[2]),
                    bx - b2[0], by - b2[1], bz - b2[2]);
            generate(ints, x + (ax - da[0]) + (b2[0] - db[0]), y + (ay - da[1]) + (b2[1] - db[1]), z + (az - da[2]) + (b2[2] - db[2]),
                    -b2[0], -b2[1], -b2[2],
                    c2[0], c2[1], c2[2],
                    -(ax - a2[0]), -(ay - a2[1]), -(az - a2[2]));
        }
    }
}
