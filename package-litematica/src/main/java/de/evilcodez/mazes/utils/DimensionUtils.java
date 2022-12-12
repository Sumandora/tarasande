package de.evilcodez.mazes.utils;

public class DimensionUtils {

    public static int[] toMinecraftDimension(final int width, final int height, final int scale) {
        return new int[] {
                (width / 2 - 1) / scale,
                (height / 2 - 1) / scale
        };
    }

    public static int[] linearToMultiDimensional(int[] sizes, int index) {
        if(sizes.length == 2) return new int[] {index % sizes[0], index / sizes[0]};
        else if(sizes.length == 3) return new int[] {index % sizes[0], (index / sizes[0]) % sizes[1], index / (sizes[0] * sizes[1])};
        final int[] result = new int[sizes.length];
        int current = index;
        for (int i = 0; i < sizes.length; i++) {
            result[i] = current % sizes[i];
            current /= sizes[i];
        }
        return result;
    }

    public static int multiDimensionalToLinear(int[] sizes, int... indices) {
        if(sizes.length == 2) return indices[0] + indices[1] * sizes[0];
        else if(sizes.length == 3) return indices[0] + indices[1] * sizes[0] + indices[2] * sizes[0] * sizes[1];
        int result = 0;
        int current = 1;
        for (int i = 0; i < sizes.length; i++) {
            result += current * indices[i];
            current *= sizes[i];
        }
        return result;
    }
}
