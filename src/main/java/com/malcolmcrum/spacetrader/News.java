package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 8/31/2015.
 */
public class News {
    private static final Logger logger = LoggerFactory.getLogger(News.class);


    static private final int USEFUL_STORY_PROBABILITY = 50 / 8;

    final private List<NotableEvent> notableEvents;
    final private List<SolarSystem.SpecialEvent> specialEvents;
    final private Game game;
    private final Quests quests;

    public News(Game game) { // TODO: don't pass in Game
        this.game = game;
        this.quests = game.getQuests();
        notableEvents = new ArrayList<>();
        specialEvents = new ArrayList<>();
    }

    public void resetNewsEvents() {
        notableEvents.clear();
        specialEvents.clear();
    }

    public void addNotableEvent(NotableEvent event) {
        notableEvents.add(event);
    }

    public void addSpecialEvent(SolarSystem.SpecialEvent event) {
        specialEvents.add(event);
    }

    public List<String> getNewspaper() {
        SolarSystem currentSystem = game.getCurrentSystem();
        Captain captain = game.getCaptain();
        List<String> paper = new ArrayList<>();

        String masthead = generateTitle(currentSystem.getType().getTitle(), currentSystem.getPolitics());
        paper.add(masthead);

        for (NotableEvent notableEvent : notableEvents) {
            String headline = notableEvent.getHeadline();
            headline = headline.replace("{{CAPTAIN_NAME}}", captain.getName()); // haha wow what a hack. jesus
            paper.add(headline);
        }
        for (SolarSystem.SpecialEvent specialEvent : specialEvents) {
            String headline = getSpecialEventHeadline(specialEvent);
            if (headline != null) {
                paper.add(headline);
            }
        }
        if (currentSystem.getSpecialEvent() == SolarSystem.SpecialEvent.MonsterKilled && quests.monsterInAcamar()) {
            paper.add("Space Monster Threatens Homeworld!");
        }
        if (currentSystem.getSpecialEvent() == SolarSystem.SpecialEvent.ScarabDestroyed && quests.isScarabAlive()) {
            paper.add("Wormhole Travelers Harassed by Unusual Ship!");
        }
        if (currentSystem.getSpecialEvent() == SolarSystem.SpecialEvent.DragonflyDestroyed && quests.isDragonflyAt(SolarSystem.Name.Zalkon) && !specialEvents.contains(SolarSystem.SpecialEvent.DragonflyDestroyed)) {
            paper.add("Unidentified Ship: A Threat to Zalkon?");
        }
        if (currentSystem.getSpecialEvent() == SolarSystem.SpecialEvent.GemulonRescued && !specialEvents.contains(SolarSystem.SpecialEvent.GemulonRescued)) {
            paper.add("Alien Invasion Devastates Planet!");
        }

        String statusHeadline = getStatusHeadline(currentSystem.getStatus());
        if (statusHeadline != null) {
            paper.add(statusHeadline);
        }

        String captainHeadline = getCaptainHeadline(captain, currentSystem);
        if (captainHeadline != null) {
            paper.add(captainHeadline);
        }

        List<String> usefulHeadlines = getUsefulHeadlines(currentSystem);
        paper.addAll(usefulHeadlines.stream().collect(Collectors.toList()));

        if (usefulHeadlines.size() == 0) {
            // TODO: Show random headlines
            // SystemInfoEvent.c:778-794
        }

        return paper;
    }

    private List<String> getUsefulHeadlines(SolarSystem currentSystem) {
        List<String> headlines = new ArrayList<>();
        for (SolarSystem system : game.getSystems()) {
            if (system == currentSystem) {
                continue;
            }

            int systemDistance = (int)Vector2i.Distance(currentSystem.getLocation(), system.getLocation());
            boolean systemNear = (systemDistance <= game.getShip().type.getFuelTanks());
            boolean systemConnectedThroughWormhole = (currentSystem.getWormholeDestination() == system);
            if (systemNear || systemConnectedThroughWormhole) {

                // some stories are always shown
                if (system.getSpecialEvent() == SolarSystem.SpecialEvent.MoonForSale) {
                    headlines.add("Seller in " + system.getType() + " has Utopian Moon available.");
                }
                if (system.getSpecialEvent() == SolarSystem.SpecialEvent.TribbleBuyer) {
                    headlines.add("Collector in " + system.getType() + "seeks to purchase Tribbles.");
                }

                boolean somethingGoingOn = (system.getStatus() != SolarSystem.Status.Uneventful);
                // Slight logic change to the original here.
                // In the original, the above moon and tribble stories are only shown if
                // the system status is not none.
                // I don't think that makes sense, so I do the system status check here.
                // (See SystemInfoEvent.c:698 for the line I moved).
                Difficulty difficulty = game.getDifficulty();
                if (somethingGoingOn && GetRandom(100) <= USEFUL_STORY_PROBABILITY * system.getTechLevel().getEra() + 10 * (5 - difficulty.getValue())) {
                    int diceRoll = GetRandom(6);
                    switch (diceRoll) {
                        case 0:
                            headlines.add("Reports of " + system.getStatus().getTitle() + " in the " + system.getType() + " System");
                            break;
                        case 1:
                            headlines.add("News of " + system.getStatus().getTitle() + " in the " + system.getType() + " System");
                            break;
                        case 2:
                            headlines.add("News Rumors of " + system.getStatus().getTitle() + " in the " + system.getType() + " System");
                            break;
                        case 3:
                            headlines.add("Sources say " + system.getStatus().getTitle() + " in the " + system.getType() + " System");
                            break;
                        case 4:
                            headlines.add("Notice: " + system.getStatus().getTitle() + " in the " + system.getType() + " System");
                            break;
                        case 5:
                            headlines.add("Evidence Suggests " + system.getStatus().getTitle() + " in the " + system.getType() + " System");
                            break;
                    }
                }
            }
        }
        return headlines;
    }

    private String getCaptainHeadline(Captain captain, SolarSystem system) {
        String headline = null;
        if (captain.policeRecord.is(PoliceRecord.Status.Villain)) {
            int diceRoll = GetRandom(4);
            switch (diceRoll) {
                case 0:
                    headline = "Police Warning: " + captain.getName() + " Will Dock At " + system.getType() + "!";
                    break;
                case 1:
                    headline = "Notorious Criminal " + captain.getName() + " Sighted in " + system.getType() + "!";
                    break;
                case 2:
                    headline = "Locals Rally to Deny Spaceport Access to " + captain.getName() + "!";
                    break;
                case 3:
                    headline = "Terror Strikes Locals on Arrival of " + captain.getName() + "!";
                    break;
            }
        } else if (captain.policeRecord.is(PoliceRecord.Status.Hero)) {
            int diceRoll = GetRandom(3);
            switch (diceRoll) {
                case 0:
                    headline = "Locals Welcome Visiting Hero " + captain.getName() + "!";
                    break;
                case 1:
                    headline = "Famed Hero " + captain.getName() + " to Visit System!";
                    break;
                case 2:
                    headline = "Large Turnout At Spaceport to Welcome " + captain.getName() + "!";
                    break;
            }
        }
        return headline;
    }

    private String getStatusHeadline(SolarSystem.Status status) {
        String headline = null;
        switch (status) {
            case Uneventful:
                break;
            case War:
                headline = "War News: Offensives Continue!";
                break;
            case Plague:
                headline = "Plague Spreads! Outlook Grim.";
                break;
            case Drought:
                headline = "No Rain in Sight!";
                break;
            case Boredom:
                headline = "Editors: Won't Someone Entertain Us?";
                break;
            case Cold:
                headline = "Cold Snap Continues!";
                break;
            case Crops:
                headline = "Serious Crop Failure! Must We Ration?";
                break;
            case Workers:
                headline = "Jobless Rate at All-Time Low!";
                break;
        }
        return headline;
    }

    private String getSpecialEventHeadline(SolarSystem.SpecialEvent specialEvent) {
        String headline = null;
        switch (specialEvent) {
            case DragonflyDestroyed:
                headline = "Spectacular Display as Stolen Ship Destroyed in Fierce Space Battle.";
                break;
            case FlyBaratas:
                headline = "Investigators Report Strange Craft.";
                break;
            case FlyMelina:
                headline = "Rumors Continue: Melina Orbitted by Odd Starcraft.";
                break;
            case FlyRegulas:
                headline = "Strange Ship Observed in Regulas Orbit.";
                break;
            case MonsterKilled:
                headline = "Hero Slays Space Monster! Parade, Honors Planned for Today.";
                break;
            case MedicineDelivery:
                headline = "Disease Antidotes Arrive! Health Officials Optimistic.";
                break;
            case Retirement:
                break;
            case MoonForSale:
                break;
            case SkillIncrease:
                break;
            case MerchantPrince:
                break;
            case EraseRecord:
                break;
            case TribbleBuyer:
                break;
            case SpaceMonster:
                break;
            case Dragonfly:
                headline = "Experimental Craft Stolen! Critics Demand Security Review.";
                break;
            case CargoForSale:
                break;
            case LightningShield:
                break;
            case JaporiDisease:
                headline = "Editorial: We Must Help Japori!";
                break;
            case LotteryWinner:
                break;
            case ArtifactDelivery:
                headline = "Scientist Adds Alien Artifact to Museum Collection.";
                break;
            case AlienArtifact:
                break;
            case AmbassadorJarek:
                break;
            case AlienInvasion:
                headline = "Editorial: Who Will Warn Gemulon?";
                break;
            case GemulonInvaded:
                break;
            case FuelCompactor:
                break;
            case DangerousExperiment:
                break;
            case JonathanWild:
                break;
            case MorgansReactor:
                break;
            case InstallMorgansLaser:
                break;
            case ScarabStolen:
                headline = "Security Scandal: Test Craft Confirmed Stolen.";
                break;
            case UpgradeHull:
                break;
            case ScarabDestroyed:
                headline = "Wormhole Traffic Delayed as Stolen Craft Destroyed.";
                break;
            case ReactorDelivered:
                break;
            case JarekGetsOut:
                headline = "Ambassador Jarek Returns from Crisis.";
                break;
            case GemulonRescued:
                headline = "Invasion Imminent! Plans in Place to Repel Hostile Invaders.";
                break;
            case DisasterAverted:
                headline = "Scientists Cancel High-profile Test! Committee to Investigate Design.";
                break;
            case ExperimentFailed:
                headline = "Huge Explosion Reported at Research Facility.";
                break;
            case WildGetsOut:
                headline = "Rumors Suggest Known Criminal J. Wild May Come to Kravat!";
                break;
        }
        return headline;
    }

    private String generateTitle(String name, Politics politics) {
        // TODO. SystemInfoEvent.c:439-462
        return "The Local Newspaper";
    }

    public int getPrice() {
        return game.getDifficulty().getValue() + 1;
    }

    public void replaceEvent(NotableEvent removeme, NotableEvent addme) {
        if (!notableEvents.remove(removeme)) {
            logger.error("Tried to remove event that does not exist!");
        }
        notableEvents.add(addme);
    }

    public enum NotableEvent {
        WildArrested("Notorious Criminal Jonathan Wild Arrested!"),
        CaughtLittering("Police Trace Orbiting Space Litter to {{CAPTAIN_NAME}}."),
        ExperimentPerformed("Travelers Report Timespace Damage, Warp Problems!"),
        ArrivalViaSingularity("Travelers Claim Sighting of Ship Materializing in Orbit!"),
        CaptainHuieAttacked("Famed Captain Huie Attacked by Brigand!"),
        CaptainConradAttacked("Captain Conrad Comes Under Attack By Criminal!"),
        CaptainAhabAttacked("Thug Assaults Captain Ahab!"),
        CaptainHuieDestroyed("Citizens Mourn Destruction of Captain Huie's Ship!"),
        CaptainConradDestroyed("Captain Conrad's Ship Destroyed by Villain!"),
        CaptainAhabDestroyed("Destruction of Captain Ahab's Ship Causes Anger!");

        private String title;

        NotableEvent(String title) {
            this.title = title;
        }

        public String getHeadline() {
            return title;
        }
    }
}
