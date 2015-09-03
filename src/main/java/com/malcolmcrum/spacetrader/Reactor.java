package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 9/2/2015.
 */
public enum Reactor {
    Unavailable(0),
    TwentyDaysLeft(1),
    NineteenDaysLeft(2),
    EighteenDaysLeft(3),
    SeventeenDaysLeft(4),
    SixteenDaysLeft(5),
    FifteenDaysLeft(6),
    FourteenDaysLeft(7),
    ThirteenDaysLeft(8),
    TwelveDaysLeft(9),
    ElevenDaysLeft(10),
    TenDaysLeft(11),
    NineDaysLeft(12),
    EightDaysLeft(13),
    SevenDaysLeft(14),
    SixDaysLeft(15),
    FiveDaysLeft(16),
    FourDaysLeft(17),
    ThreeDaysLeft(18),
    TwoDaysLeft(19),
    OneDayLeft(20),
    Delivered(21);

    private int value;

    Reactor(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
