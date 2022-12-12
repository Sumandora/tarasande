package de.evilcodez.mazes.generator;

import java.util.function.Supplier;

public enum MazeGeneratorType {

    BACKTRACKING("Backtracking", BacktrackingMazeGenerator::new),
    ALDOUS_BRODER("Aldous-Broder", AldousBroderMazeGenerator::new),
    HILBERT_CURVE("Hilbert-Curve", HilbertCurveMazeGenerator::new),
    WILSON("Wilson", WilsonMazeGenerator::new),
    KRUSKAL("Kruskal", KruskalsMazeGenerator::new),
    PRIM("Prim", PrimsMazeGenerator::new),
    HUNT_AND_KILL("Hunt-and-Kill", HuntAndKillMazeGenerator::new),
    BINARY_TREE("Binary-Tree", BinaryTreeMazeGenerator::new);

    private final String name;
    private final Supplier<MazeGenerator> factory;

    MazeGeneratorType(String name, Supplier<MazeGenerator> factory) {
        this.name = name;
        this.factory = factory;
    }

    public String getName() {
        return name;
    }

    public Supplier<MazeGenerator> getFactory() {
        return factory;
    }

    @Override
    public String toString() {
        return name;
    }

    public static MazeGeneratorType byName(String name) {
        for (MazeGeneratorType type : values()) {
            if(type.getName().equalsIgnoreCase(name)) return type;
        }
        return null;
    }
}
