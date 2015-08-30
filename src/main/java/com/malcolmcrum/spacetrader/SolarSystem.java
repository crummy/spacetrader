package com.malcolmcrum.spacetrader;

import java.util.HashMap;
import java.util.Map;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;
import static com.malcolmcrum.spacetrader.Utils.RandomEnum;

/**
 * Created by Malcolm on 8/28/2015.
 */
public class SolarSystem {
    private Vector2i location;
    private final String name;
    private final TechLevel techLevel;
    private final Politics politics;
    private final SpecialResource specialResource;
    private Status status;
    private final Size size;
    private final Map<TradeItem, Integer> tradeItems;
    private int tradeResetCountdown;
    private boolean visited;
    private SpecialEvent specialEvent;
    private int wormholeDestination;
    private Crew mercenary;

    public SolarSystem(Vector2i location, int index, Difficulty difficulty) {
        this.location = location;
        this.name = names[index];

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
            status = Status.None;
        }

        size = RandomEnum(Size.class);
        specialEvent = null;
        tradeResetCountdown = 0;
        visited = false;
        wormholeDestination = -1;

        tradeItems = initializeTradeItems(difficulty);
    }

    public boolean hasWormhole() {
        return wormholeDestination != -1;
    }

    private Map<TradeItem, Integer> initializeTradeItems(Difficulty difficulty) {
        Map<TradeItem, Integer> items = new HashMap<>();
        for (TradeItem item : TradeItem.values()) {
            boolean bannedItem = (item == TradeItem.Narcotics && !politics.getDrugsOK()) ||
                                 (item == TradeItem.Firearms && !politics.getFirearmsOK());
            boolean itemTooAdvanced = item.getTechLevelRequiredForProduction().isBeyond(techLevel);

            if (bannedItem || itemTooAdvanced) {
                items.put(item, 0);
                continue;
            }

            int quantity = ((9 + GetRandom(5)) - techLevel.erasBetween(item.getTechLevelForTopProduction())) * (1 + size.getMultiplier());

            // Cap robots and narcotics due to potential for easy profits
            if (item == TradeItem.Robots || item == TradeItem.Narcotics) {
                int difficultyValue = difficulty.getValue();
                quantity = ((quantity * (5 - difficultyValue)) / (6 - difficultyValue)) + 1;
            }

            if (item.getCheapResourceTrigger() != SpecialResource.Nothing
                    && specialResource == item.getCheapResourceTrigger()) {
                quantity = (quantity * 4) / 3;
            }

            if (item.getExpensiveResourceTrigger() != SpecialResource.Nothing
                    && specialResource == item.getExpensiveResourceTrigger()) {
                quantity = (quantity * 3) >> 2;
            }

            if (item.getDoublePriceTrigger() != Status.None
                    && status == item.getDoublePriceTrigger()) {
                quantity = quantity / 5;
            }

            quantity = quantity - GetRandom(10) + GetRandom(10);
            if (quantity < 0) {
                quantity = 0;
            }

            items.put(item, quantity);
        }
        return items;
    }

    public void addWormhole(int destination) {
        wormholeDestination = destination;
    }

    public Vector2i getLocation() {
        return location;
    }

    public String getName() {
        return name;
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
        this.specialEvent = specialEvent;
    }

    public void setLocation(Vector2i location) {
        this.location = location;
    }

    public int getWormholeDestination() {
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

    public int getTradeResetCountdown() {
        return tradeResetCountdown;
    }

    public Map<TradeItem, Integer> getTradeItems() {
        return tradeItems;
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

    enum Status {
        None("under no particular pressure"),
        War("at war"),
        Plague("ravaged by a plague"),
        Drought("suffering from a drought"),
        Boredom("suffering from extreme boredom"),
        Cold("suffering from a cold spell"),
        Crops("suffering from a crop failure"),
        Workers("lacking enough workers");

        private String name;

        Status(String name) {
            this.name = name;
        }
    }

    public enum SpecialEvent {
        DragonflyDestroyed,
        WeirdShip,
        LightningShip,
        MonsterKilled,
        MedicineDelivery,
        Retirement,
        MoonForSale,
        SkillIncrease,
        MerchantPrince,
        EraseRecord,
        TribbleBuyer,
        SpaceMonster,
        Dragonfly,
        CargoForSale,
        LightningShield,
        JaporiDisease,
        LotteryWinner,
        ArtifactDelivery,
        AlienArtifact,
        AmbassadorJarek,
        AlienInvasion,
        GemulonInvaded,
        FuelCompactor,
        DangerousExperiment,
        JonathanWild,
        MorgansReactor,
        InstallMorgansLaser,
        ScarabStolen,
        UpgradeHull,
        ScarabDestroyed,
        ReactorDelivered,
        JarekGetsOut,
        GemulonRescued,
        DisasterAverted,
        ExperimentFailed,
        FlyBaratas, FlyMelina, FlyRegulas, WildGetsOut
    }

    public static String[] names = {
            "Acamar",
            "Adahn",        // The alternate personality for The Nameless One in "Planescape: Torment"
            "Aldea",
            "Andevian",
            "Antedi",
            "Balosnee",
            "Baratas",
            "Brax",            // One of the heroes in Master of Magic
            "Bretel",        // This is a Dutch device for keeping your pants up.
            "Calondia",
            "Campor",
            "Capelle",        // The city I lived in while programming this game
            "Carzon",
            "Castor",        // A Greek demi-god
            "Cestus",
            "Cheron",
            "Courteney",    // After Courteney Cox...
            "Daled",
            "Damast",
            "Davlos",
            "Deneb",
            "Deneva",
            "Devidia",
            "Draylon",
            "Drema",
            "Endor",
            "Esmee",        // One of the witches in Pratchett's Discworld
            "Exo",
            "Ferris",        // Iron
            "Festen",        // A great Scandinavian movie
            "Fourmi",        // An ant, in French
            "Frolix",        // A solar system in one of Philip K. Dick's novels
            "Gemulon",
            "Guinifer",        // One way of writing the name of king Arthur's wife
            "Hades",        // The underworld
            "Hamlet",        // From Shakespeare
            "Helena",        // Of Troy
            "Hulst",        // A Dutch plant
            "Iodine",        // An element
            "Iralius",
            "Janus",        // A seldom encountered Dutch boy's name
            "Japori",
            "Jarada",
            "Jason",        // A Greek hero
            "Kaylon",
            "Khefka",
            "Kira",            // My dog's name
            "Klaatu",        // From a classic SF movie
            "Klaestron",
            "Korma",        // An Indian sauce
            "Kravat",        // Interesting spelling of the French word for "tie"
            "Krios",
            "Laertes",        // A king in a Greek tragedy
            "Largo",
            "Lave",            // The starting system in Elite
            "Ligon",
            "Lowry",        // The name of the "hero" in Terry Gilliam's "Brazil"
            "Magrat",        // The second of the witches in Pratchett's Discworld
            "Malcoria",
            "Melina",
            "Mentar",        // The Psilon home system in Master of Orion
            "Merik",
            "Mintaka",
            "Montor",        // A city in Ultima III and Ultima VII part 2
            "Mordan",
            "Myrthe",        // The name of my daughter
            "Nelvana",
            "Nix",            // An interesting spelling of a word meaning "nothing" in Dutch
            "Nyle",            // An interesting spelling of the great river
            "Odet",
            "Og",            // The last of the witches in Pratchett's Discworld
            "Omega",        // The end of it all
            "Omphalos",        // Greek for navel
            "Orias",
            "Othello",        // From Shakespeare
            "Parade",        // This word means the same in Dutch and in English
            "Penthara",
            "Picard",        // The enigmatic captain from ST:TNG
            "Pollux",        // Brother of Castor
            "Quator",
            "Rakhar",
            "Ran",            // A film by Akira Kurosawa
            "Regulas",
            "Relva",
            "Rhymus",
            "Rochani",
            "Rubicum",        // The river Ceasar crossed to get into Rome
            "Rutia",
            "Sarpeidon",
            "Sefalla",
            "Seltrice",
            "Sigma",
            "Sol",            // That's our own solar system
            "Somari",
            "Stakoron",
            "Styris",
            "Talani",
            "Tamus",
            "Tantalos",        // A king from a Greek tragedy
            "Tanuga",
            "Tarchannen",
            "Terosa",
            "Thera",        // A seldom encountered Dutch girl's name
            "Titan",        // The largest moon of Jupiter
            "Torin",        // A hero from Master of Magic
            "Triacus",
            "Turkana",
            "Tyrus",
            "Umberlee",        // A god from AD&D, which has a prominent role in Baldur's Gate
            "Utopia",        // The ultimate goal
            "Vadera",
            "Vagra",
            "Vandor",
            "Ventax",
            "Xenon",
            "Xerxes",        // A Greek hero
            "Yew",            // A city which is in almost all of the Ultima games
            "Yojimbo",        // A film by Akira Kurosawa
            "Zalkon",
            "Zuul"            // From the first Ghostbusters movie
    };

}
