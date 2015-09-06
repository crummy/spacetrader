package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;
import static com.malcolmcrum.spacetrader.Utils.RandomEnum;

/**
 * Created by Malcolm on 8/28/2015.
 */
public class SolarSystem {
    static final int COST_MOON = 500000;
    private static final Logger logger = LoggerFactory.getLogger(SolarSystem.class);

    private Vector2i location;
    private final Name name;
    private final TechLevel techLevel;
    private final Politics politics;
    private final SpecialResource specialResource;
    private Status status;
    private final Size size;
    private final Market market;
    private boolean visited;
    private SpecialEvent specialEvent;
    private SolarSystem wormholeDestination;
    private Crew mercenary;
    private Game game;

    SolarSystem(Game game, int index) {
        name = Name.values()[index];
        this.game = game;

        TechLevel t;
        Politics p;
        do {
            p = RandomEnum(Politics.class);
            t = RandomEnum(TechLevel.class);
        } while (t.isBefore(p.getMinTechLevel()) || t.isBeyond(p.getMaxTechLevel()));
        politics = p;
        techLevel = t;

        if (GetRandom(5) >= 3) {
            specialResource = RandomEnum(SpecialResource.class, 1);
        } else {
            specialResource = SpecialResource.Nothing;
        }

        if (GetRandom(100) < 15) {
            status = RandomEnum(Status.class, 1);
        } else {
            status = Status.Uneventful;
        }

        size = RandomEnum(Size.class);
        specialEvent = null;
        visited = false;
        wormholeDestination = null;

        market = new Market(game, this);
    }

    public static int GetMaxSystems() {
        return Name.values().length;
    }

    public boolean hasWormhole() {
        return wormholeDestination != null;
    }

    public void setWormhole(SolarSystem destination) {
        wormholeDestination = destination;
    }

    public Vector2i getLocation() {
        return location;
    }

    public Name getType() {
        return name;
    }

    public String getName() {
        return name.getTitle();
    }

    public boolean hasMercenary() {
        return mercenary != null;
    }

    public void addMercenary(Crew mercenary) {
        assert(this.mercenary == null);
        this.mercenary = mercenary;
    }

    public SpecialEvent getSpecialEvent() {
        return specialEvent;
    }

    public void setSpecialEvent(SpecialEvent specialEvent) {
        if (hasSpecialEvent() && this.specialEvent != specialEvent) {
            logger.error("Tried to add " + specialEvent + " but already have event " + this.specialEvent);
        }
        this.specialEvent = specialEvent;
    }

    public void setLocation(Vector2i location) {
        this.location = location;
    }

    public SolarSystem getWormholeDestination() {
        return wormholeDestination;
    }

    Crew getMercenary() {
        return mercenary;
    }

    public Politics getPolitics() {
        return politics;
    }

    public TechLevel getTechLevel() {
        return techLevel;
    }

    public boolean isVisited() {
        return visited;
    }

    public Status getStatus() {
        return status;
    }

    public boolean hasSpecialEvent() {
        return specialEvent != null;
    }

    public void setVisited() {
        visited = true;
    }

    public int getPirateStrength() {
        return getPolitics().getPirateStrength().getStrength();
    }

    public int getPoliceStrength() {
        return getPolitics().getPoliceStrength().getStrength();
    }

    public int getTraderStrength() {
        return getPolitics().getPoliceStrength().getStrength();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SpecialResource getSpecialResource() {
        return specialResource;
    }

    public Size getSize() {
        return size;
    }

    public Market getMarket() {
        return market;
    }

    enum SpecialResource {
        Nothing("Nothing special"),
        MineralRich("Mineral rich"),
        MineralPoor("Mineral poor"),
        Desert("Desert"),
        SweetwaterOceans("Sweetwater oceans"),
        RichSoil("Rich soil"),
        PoorSoil("Poor soil"),
        RichFauna("Rich fauna"),
        Lifeless("Lifeless"),
        WeirdMushrooms("Weird mushrooms"),
        SpecialHerbs("Special herbs"),
        ArtisticPopulace("Artistic populace"),
        WarlikePopulace("Warlike populace");

        private String name;

        SpecialResource(String name) {
            this.name = name;
        }
    }

    enum Size {
        Tiny(1, "Tiny"),
        Small(2, "Small"),
        Medium(3, "Medium"),
        Large(4, "Large"),
        Huge(5, "Huge");

        private String name;
        private int multiplier;

        Size(int multiplier, String name) {
            this.multiplier = multiplier;
            this.name = name;
        }

        public int getMultiplier() {
            return multiplier;
        }
    }

    public enum Name {
        Acamar("Acamar"),
        Adahn("Adahn"),        // The alternate personality for The Nameless One in "Planescape: Torment"
        Aldea("Aldea"),
        Andevian("Andevian"),
        Antedi("Antedi"),
        Balosnee("Balosnee"),
        Baratas("Baratas"),
        Brax("Brax"),            // One of the heroes in Master of Magic
        Bretel("Bretel"),        // This is a Dutch device for keeping your pants up.
        Calondia("Calondia"),
        Campor("Campor"),
        Capelle("Capelle"),        // The city I lived in while programming this game
        Carzon("Carzon"),
        Castor("Castor"),        // A Greek demi-god
        Cestus("Cestus"),
        Cheron("Cheron"),
        Courteney("Courteney"),    // After Courteney Cox...
        Daled("Daled"),
        Damast("Damast"),
        Davlos("Davlos"),
        Deneb("Deneb"),
        Deneva("Deneva"),
        Devidia("Devidia"),
        Draylon("Draylon"),
        Drema("Drema"),
        Endor("Endor"),
        Esmee("Esmee"),        // One of the witches in Pratchett's Discworld
        Exo("Exo"),
        Ferris("Ferris"),        // Iron
        Festen("Festen"),        // A great Scandinavian movie
        Fourmi("Fourmi"),        // An ant, in French
        Frolix("Frolix"),        // A solar system in one of Philip K. Dick's novels
        Gemulon("Gemulon"),
        Guinifer("Guinifer"),        // One way of writing the name of king Arthur's wife
        Hades("Hades"),        // The underworld
        Hamlet("Hamlet"),        // From Shakespeare
        Helena("Helena"),        // Of Troy
        Hulst("Hulst"),        // A Dutch plant
        Iodine("Iodine"),        // An element
        Iralius("Iralius"),
        Janus("Janus"),        // A seldom encountered Dutch boy's name
        Japori("Japori"),
        Jarada("Jarada"),
        Jason("Jason"),        // A Greek hero
        Kaylon("Kaylon"),
        Khefka("Khefka"),
        Kira("Kira"),            // My dog's name
        Klaatu("Klaatu"),        // From a classic SF movie
        Klaestron("Klaestron"),
        Korma("Korma"),        // An Indian sauce
        Kravat("Kravat"),        // Interesting spelling of the French word for "tie"
        Krios("Krios"),
        Laertes("Laertes"),        // A king in a Greek tragedy
        Largo("Largo"),
        Lave("Lave"),            // The starting system in Elite
        Ligon("Ligon"),
        Lowry("Lowry"),        // The name of the "hero" in Terry Gilliam's "Brazil"
        Magrat("Magrat"),        // The second of the witches in Pratchett's Discworld
        Malcoria("Malcoria"),
        Melina("Melina"),
        Mentar("Mentar"),        // The Psilon home system in Master of Orion
        Merik("Merik"),
        Mintaka("Mintaka"),
        Montor("Montor"),        // A city in Ultima III and Ultima VII part 2
        Mordan("Mordan"),
        Myrthe("Myrthe"),        // The name of my daughter
        Nelvana("Nelvana"),
        Nix("Nix"),            // An interesting spelling of a word meaning "nothing" in Dutch
        Nyle("Nyle"),            // An interesting spelling of the great river
        Odet("Odet"),
        Og("Og"),            // The last of the witches in Pratchett's Discworld
        Omega("Omega"),        // The end of it all
        Omphalos("Omphalos"),        // Greek for navel
        Orias("Orias"),
        Othello("Othello"),        // From Shakespeare
        Parade("Parade"),        // This word means the same in Dutch and in English
        Penthara("Penthara"),
        Picard("Picard"),        // The enigmatic captain from ST:TNG
        Pollux("Pollux"),        // Brother of Castor
        Quator("Quator"),
        Rakhar("Rakhar"),
        Ran("Ran"),            // A film by Akira Kurosawa
        Regulas("Regulas"),
        Relva("Relva"),
        Rhymus("Rhymus"),
        Rochani("Rochani"),
        Rubicum("Rubicum"),        // The river Ceasar crossed to get into Rome
        Rutia("Rutia"),
        Sarpeidon("Sarpeidon"),
        Sefalla("Sefalla"),
        Seltrice("Seltrice"),
        Sigma("Sigma"),
        Sol("Sol"),            // That's our own solar system
        Somari("Somari"),
        Stakoron("Stakoron"),
        Styris("Styris"),
        Talani("Talani"),
        Tamus("Tamus"),
        Tantalos("Tantalos"),        // A king from a Greek tragedy
        Tanuga("Tanuga"),
        Tarchannen("Tarchannen"),
        Terosa("Terosa"),
        Thera("Thera"),        // A seldom encountered Dutch girl's name
        Titan("Titan"),        // The largest moon of Jupiter
        Torin("Torin"),        // A hero from Master of Magic
        Triacus("Triacus"),
        Turkana("Turkana"),
        Tyrus("Tyrus"),
        Umberlee("Umberlee"),        // A god from AD&D, which has a prominent role in Baldur's Gate
        Utopia("Utopia"),        // The ultimate goal
        Vadera("Vadera"),
        Vagra("Vagra"),
        Vandor("Vandor"),
        Ventax("Ventax"),
        Xenon("Xenon"),
        Xerxes("Xerxes"),        // A Greek hero
        Yew("Yew"),            // A city which is in almost all of the Ultima games
        Yojimbo("Yojimbo"),        // A film by Akira Kurosawa
        Zalkon("Zalkon"),
        Zuul("Zuul");            // From the first Ghostbusters movie

        private final String title;
        Name(String title) {
            this.title = title;
        }

        protected String getTitle() {
            return title;
        }
    }

    enum Status {
        Uneventful("UNUSED_TEXT", "under no particular pressure"),
        War("Strife and War", "at war"),
        Plague("Plague Outbreaks", "ravaged by a plague"),
        Drought("Severe Drought", "suffering from a drought"),
        Boredom("Terrible Boredom", "suffering from extreme boredom"),
        Cold("Cold Weather", "suffering from a cold spell"),
        Crops("Crop Failures", "suffering from a crop failure"),
        Workers("Labor Shortages", "lacking enough workers");

        private final String description; // used for reporting status
        private final String title; // used for newspaper headlines

        Status(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }
    }

    public enum SpecialEvent {
        DragonflyDestroyed("Dragonfly Destroyed", "MISSING_STRING", 0, 0, true, true),
        FlyBaratas("Weird Ship", "MISSING_STRING", 0, 0, true, true),
        FlyMelina("Lightning Ship", "MISSING_STRING", 0, 0, true, true),
        FlyRegulas("Strange Ship", "MISSING_STRING", 0, 0, true, true),
        MonsterKilled("Monster Killed", "MISSING_STRING", -15000, 0, true, true),
        MedicineDelivery("Medicine Delivery", "MISSING_STRING", 0, 0, true, true),
        Retirement("Retirement", "MISSING_STRING", 0, 0, false, true),
        MoonForSale("Moon For Sale", "MISSING_STRING", COST_MOON, 4, false),
        SkillIncrease("Skill Increase", "MISSING_STRING", 3000, 3, false),
        MerchantPrince("Merchant Prince", "MISSING_STRING", 1000, 1, false),
        EraseRecord("Erase Record", "MISSING_STRING", 5000, 3, false),
        TribbleBuyer("Tribble Buyer", "MISSING_STRING", 0, 3, false),
        SpaceMonster("Space Monster", "MISSING_STRING", 0, 1, true),
        Dragonfly("Dragonfly", "MISSING_STRING", 0, 1, true),
        CargoForSale("Cargo For Sale", "MISSING_STRING", 1000, 3, false),
        LightningShield("Lightning Shield", "MISSING_STRING", 0, 0, false),
        JaporiDisease("Japori Disease", "MISSING_STRING", 0, 1, false),
        LotteryWinner("Lottery Winner", "MISSING_STRING", -1000, 0, true),
        ArtifactDelivery("Artifact Delivery", "MISSING_STRING", -20000, 0, true),
        AlienArtifact("Alien Artifact", "MISSING_STRING", 0, 1, false),
        AmbassadorJarek("Ambassador Jarek", "MISSING_STRING", 0, 1, false),
        AlienInvasion("Alien Invasion", "MISSING_STRING", 0, 0, true),
        GemulonInvaded("Gemulon Invaded", "MISSING_STRING", 0, 0, true),
        FuelCompactor("Fuel Compactor", "MISSING_STRING", 0, 0, false),
        DangerousExperiment("Dangerous Experiment", "MISSING_STRING", 0, 0, true),
        JonathanWild("Jonathan Wild", "MISSING_STRING", 0, 1, false),
        MorgansReactor("Morgan's Reactor", "MISSING_STRING", 0, 0, false),
        InstallMorgansLaser("Install Morgan's Laser", "MISSING_STRING", 0, 0, false),
        ScarabStolen("Scarab Stolen", "MISSING_STRING", 0, 1, true),
        UpgradeHull("Upgrade Hull", "MISSING_STRING", 0, 0, false),
        ScarabDestroyed("Scarab Destroyed", "MISSING_STRING", 0, 0, true, true),
        ReactorDelivered("Reactor Delivered", "MISSING_STRING", 0, 0, true, true),
        JarekGetsOut("Jarek Gets Out", "MISSING_STRING", 0, 0, true, true),
        GemulonRescued("Gemulon Rescued", "MISSING_STRING", 0, 0, true, true),
        DisasterAverted("Disaster Averted", "MISSING_STRING", 0, 0, true, true),
        ExperimentFailed("Experiment Failed", "MISSING_STRING", 0, 0, true, true),
        WildGetsOut("Wild Gets Out", "MISSING_STRING", 0, 0, true, true);

        private final String title;
        private final String text;
        private final int price;
        private int occurrence;
        private final boolean justAMessage;
        private final boolean fixedLocation;

        SpecialEvent(String title, String text, int price, int occurrence, boolean justAMessage) {
            this(title, text, price, occurrence, justAMessage, false);
        }

        SpecialEvent(String title, String text, int price, int occurrence, boolean justAMessage, boolean fixedLocation) {
            this.title = title;
            this.text = text;
            this.price = price;
            this.occurrence = occurrence;
            this.justAMessage = justAMessage;
            this.fixedLocation = fixedLocation;
        }

        public boolean hasFixedLocation() {
            return fixedLocation;
        }

        public String getTitle() {
            return title;
        }

        public int getOccurrence() {
            return occurrence;
        }
    }

}
