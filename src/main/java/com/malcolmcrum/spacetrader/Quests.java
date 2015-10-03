package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 10/3/2015.
 */
public class Quests {
    private Scarab scarabState = Scarab.Unavailable;
    private Jarek jarekState = Jarek.Unavailable;
    private Japori japoriState = Japori.NoDisease;
    private Reactor reactorState = Reactor.Unavailable;
    private Wild wildState = Wild.Unavailable;
    private int monsterHullStrength = ShipType.SpaceMonster.getHullStrength();
    private int fabricRipProbability; // = ??
    private Experiment experimentState = Experiment.NotStarted;
    private Monster monsterState = Monster.Unavailable;
    private SolarSystem.Name dragonflySystem = null;
    private Invasion invasionState = Invasion.Unavailable;
    private boolean isArtifactOnBoard = false;
    private Dragonfly dragonflyStatus = Dragonfly.Unavailable;

    public boolean scarabUpgradePerformed() {
        return scarabState == Scarab.DestroyedUpgradePerformed;
    }

    public boolean isJarekDelivered() {
        return jarekState == Jarek.Delivered;
    }

    public boolean isAntidoteOnBoard() {
        return japoriState == Japori.GoToJapori;
    }

    public boolean isReactorOnBoard() {
        return reactorState != Reactor.Delivered && reactorState != Reactor.Unavailable;
    }

    public int getReactorDays() {
        return reactorState.getValue();
    }

    public boolean isWildOnBoard() {
        return wildState == Wild.OnBoard;
    }

    public void cancelJapori() {
        japoriState = Japori.FinishedOrCancelled;
    }

    public boolean isJarekOnBoard() {
        return jarekState == Jarek.OnBoard;
    }

    public void jarekLeft() {
        jarekState = Jarek.Unavailable;
    }

    public void wildArrested() {
        wildState = Wild.Unavailable;
    } // TODO: merge with lostWild

    public void lostReactor() {
        reactorState = Reactor.Unavailable;
    }

    public void healMonster() {
        monsterHullStrength = (monsterHullStrength * 105) / 100;
        if (monsterHullStrength > ShipType.SpaceMonster.getHullStrength()) {
            monsterHullStrength = ShipType.SpaceMonster.getHullStrength();
        }
    }

    public int getFabricRipProbability() {
        return fabricRipProbability;
    }

    public boolean experimentPerformed() {
        return experimentState == Experiment.Performed;
    }

    public boolean monsterInAcamar() {
        return monsterState == Monster.InAcamar;
    }

    public boolean isScarabAlive() {
        return scarabState == Scarab.Alive;
    }

    public boolean isDragonflyAt(SolarSystem.Name system) {
        return dragonflySystem == system;
    }

    public boolean isInvasionTooLate() {
        return invasionState == Invasion.TooLate;
    }

    public int reactorDaysLeft() {
        return 21 - reactorState.getValue();
    }

    public void reactorDestroyed() {
        reactorState = Reactor.Unavailable;
    }

    public void setScarabUnavailable() {
        scarabState = Scarab.Unavailable;
    }

    public boolean isExperimentComing() {
        return experimentState != Experiment.Performed && experimentState != Experiment.Cancelled && experimentState != Experiment.NotStarted;
    }

    public boolean isInvasionComing() {
        return invasionState != Invasion.Unavailable && invasionState != Invasion.TooLate;
    }

    public boolean isArtifactOnBoard() {
        return isArtifactOnBoard;
    }

    public boolean isDragonflyDestroyed() {
        return dragonflyStatus == Dragonfly.Destroyed;
    }

    public boolean isMonsterKilled() {
        return monsterState == Monster.Destroyed;
    }

    public boolean scarabUpgradeAvailable() {
        return scarabState == Scarab.DestroyedUpgradeAvailable;
    }

    public void destroyedDragonfly() {
        dragonflyStatus = Dragonfly.Destroyed;
    }

    public int getMonsterHullStrength() {
        return monsterHullStrength;
    }

    public void lostWild() {
        wildState = Wild.Unavailable;
    }

    public void setMonsterHullStrength(int monsterHullStrength) {
        this.monsterHullStrength = monsterHullStrength;
    }

    public void destroyedMonster() {
        this.monsterState = Monster.Destroyed;
    }

    public void lostAntidote() {
        japoriState = Japori.NoDisease;
    }

    public void lostArtifact() {
        isArtifactOnBoard = false;
    }

    public void lostJarek() {
        jarekState = Jarek.Unavailable;
    }

    public void destroyedScarab() {
        scarabState = Scarab.DestroyedUpgradeAvailable;
    }

    public void gotWild() {
        wildState = Wild.OnBoard;
    }

    public void gotReactor() {
        reactorState = Reactor.TwentyDaysLeft;
    }


    private enum Dragonfly {
        Unavailable,
        GoToBaratas,
        GoToMelina,
        GoToRegulas,
        GoToZalkon,
        Destroyed
    }


    private enum Experiment {
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


    private enum Monster {
        Unavailable,
        InAcamar,
        Destroyed
    }


    private enum Japori {
        NoDisease,
        GoToJapori,
        FinishedOrCancelled
    }

    private enum Invasion {
        Unavailable,
        SevenDaysLeft,
        SixDaysLeft,
        FiveDaysLeft,
        FourDaysLeft,
        ThreeDaysLeft,
        TwoDaysLeft,
        OneDayLeft,
        TooLate
    }

    private enum Jarek {
        Unavailable,
        OnBoard,
        Delivered
    }

    private enum Scarab {
        Unavailable,
        Alive,
        DestroyedUpgradeAvailable,
        DestroyedUpgradePerformed
    }

    private enum Wild {
        Unavailable,
        OnBoard,
        Delivered
    }

    private enum Reactor {
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

}
