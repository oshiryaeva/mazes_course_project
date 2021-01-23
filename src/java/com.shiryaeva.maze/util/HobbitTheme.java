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
                return "shire.jpg";
            case "Rivendell":
                return "rivendell.jpg";
            case "Mirkwood":
                return "mirkwood.jpg";
            case "Erebor":
                return "erebor.jpg";
            default:
                return "shire.jpg";
        }
    }
}
