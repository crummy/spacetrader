package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 9/2/2015.
 */
public enum Experiment {
    NotStarted,
    ElevenDaysLeft,
    TenDaysLeft,
    NineDaysLeft,
    EightDaysLeft,
    SevenDaysLeft,
    SixDaysLeft,
    FiveDaysLeft,
    FourDaysLeft,
    ThreeDaysLeft,
    TwoDaysLeft,
    OneDayLeft,
    Performed,
    Cancelled;

    public Experiment next() {
        return values()[ordinal() + 1];
    }
}
