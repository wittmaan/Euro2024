package de.awi.tournamentsimulator.euro2024;

public enum KnockoutStageRoundName {
    RoundOf16("RoundOf16"),
    QuarterFinals("QuarterFinals"),
    SemiFinals("SemiFinals"),
    Final("Final");

    private final String name;

    KnockoutStageRoundName(String name) {
        this.name = name;
    }
}
