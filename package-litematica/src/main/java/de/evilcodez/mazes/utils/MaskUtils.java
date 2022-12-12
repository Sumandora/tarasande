package de.evilcodez.mazes.utils;

import java.awt.image.BufferedImage;
import java.util.function.IntPredicate;

public class MaskUtils {

    public static boolean[] createMask2D(BufferedImage image, IntPredicate predicate) {
        final boolean[] mask = new boolean[image.getWidth() * image.getHeight()];
        final int[] size = new int[]{image.getWidth(), image.getHeight()};
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                mask[DimensionUtils.multiDimensionalToLinear(size, x, y)] = predicate.test(image.getRGB(x, y));
            }
        }
        return mask;
    }

    public static boolean[] createMask2D(BufferedImage image, int minAlpha) {
        return createMask2D(image, rgb -> (rgb >>> 24 & 0xFF) >= minAlpha);
    }
}
