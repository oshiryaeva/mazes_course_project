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
        return switch (getTheme()) {
            case "Shire" -> "src/resources/shire.jpg";
            case "Rivendell" -> "src/resources/rivendell.jpg";
            case "Mirkwood" -> "src/resources/mirkwood.jpg";
            case "Erebor" -> "src/resources/erebor.jpg";
            default -> "src/resources/shire.jpg";
        };
    }
}
