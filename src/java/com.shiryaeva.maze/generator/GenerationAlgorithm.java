package com.shiryaeva.maze.generator;

public enum GenerationAlgorithm {

    PRIM("Алгоритм Прима"),
    KRUSKAL("Алгоритм Краскала"),
    SIDEWINDER("Sidewinder");

    private final String alg;

    GenerationAlgorithm(String alg) {
        this.alg = alg;
    }

    @Override
    public String toString() {
        return alg;
    }
}
