package de.florianmichael.clampclient.injection.instrumentation_1_8.wrapper;

import net.minecraft.util.math.Direction;

public class DirectionWrapper {

    public static Direction rotateY(final Direction input) {
        return switch (input) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.NORTH;
            default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + input);
        };
    }

    public static Direction rotateX(final Direction input) {
        return switch (input) {
            case NORTH -> Direction.DOWN;
            case EAST, WEST -> throw new IllegalStateException("Unable to get X-rotated facing of " + input);
            default -> throw new IllegalStateException("Unable to get X-rotated facing of " + input);
            case SOUTH -> Direction.UP;
            case UP -> Direction.NORTH;
            case DOWN -> Direction.SOUTH;
        };
    }

    public static Direction rotateZ(final Direction input) {
        return switch (input) {
            case EAST -> Direction.DOWN;
            case SOUTH -> throw new IllegalStateException("Unable to get Z-rotated facing of " + input);
            default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + input);
            case WEST -> Direction.UP;
            case UP -> Direction.EAST;
            case DOWN -> Direction.WEST;
        };
    }
}
