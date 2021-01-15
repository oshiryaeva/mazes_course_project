package com.shiryaeva.maze.solver;

public enum SolvingAlgorithm {

    ONE_HAND("Одной руки"),
    LEE("Волновой");

    private final String alg;

    SolvingAlgorithm(String alg) {
        this.alg = alg;
    }

    public String getAlg() {
        return alg;
    }

    @Override
    public String toString() {
        return alg;
    }
}
