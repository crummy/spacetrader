package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum TechLevel {

    Preagricultural(0, "Pre-agricultural"),
    Agricultural(1, "Agricultural"),
    Medieval(2, "Medieval"),
    Renaissance(3, "Renaissance"),
    EarlyIndustrial(4, "Early Industrial"),
    Industrial(5, "Industrial"),
    PostIndustrial(6, "Post Industrial"),
    HiTech(7, "Hi-tech"),
    Unattainable(8, "UNATTAINABLE");

    private String name;
    private int era;

    TechLevel(int era, String name) {
        this.era = era;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isBeyond(TechLevel otherLevel) {
        return era > otherLevel.era;
    }

    public boolean isBefore(TechLevel otherLevel) {
        return era < otherLevel.era;
    }

    public int erasBetween(TechLevel otherLevel) {
        return Math.abs(era - otherLevel.era);
    }

    public int getEra() {
        return era;
    }
}
