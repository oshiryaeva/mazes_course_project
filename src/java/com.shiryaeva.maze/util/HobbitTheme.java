package com.shiryaeva.maze.util;

public enum HobbitTheme {

    SHIRE("Shire"),
    RIVENDELL("Rivendell"),
    MIRKWOOD("Mirkwood"),
    EREBOR("Erebor");

    private final String theme;

    HobbitTheme(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }

    public String getBackground() {
        switch (getTheme()) {
            case "Shire":
                return "src/resources/shire.jpg";
            case "Rivendell":
                return "src/resources/rivendell.jpg";
            case "Mirkwood":
                return "src/resources/mirkwood.jpg";
            case "Erebor":
                return "src/resources/erebor.jpg";
            default:
                return "src/resources/shire.jpg";
        }
    }
}
