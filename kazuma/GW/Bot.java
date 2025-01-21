import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class Bot {
    private Player botPlayer;
    private Player opponent;
    private Game game;
    private Timer botTimer;
    private Random random;
    private boolean isResourceBuildingBuilt = false;
    private Difficulty difficulty;

    private int actionCounter = 0;

    public Bot(Game game, Player botPlayer, Player opponent, Difficulty difficulty) {
        this.game = game;
        this.botPlayer = botPlayer;
        this.opponent = opponent;
        this.random = new Random();
        this.difficulty = difficulty;

        System.out.println("Botの難易度: " + difficulty);
    }

    public void start() {
        botTimer = new Timer();
        botTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                performActions();
            }
        }, 0, 1000); // 2秒ごとに行動
    }

    private void performActions() {
        switch (difficulty) {
            case EASY -> performEasyActions();
            case NORMAL -> performNormalActions();
            case HARD -> performHardActions();
        }
    }

    private void performEasyActions() {
        summonUnits(); // EASY はユニット召喚のみ
    }

    private void performNormalActions() {
        switch (actionCounter % 4) {
            case 0 -> buildBuildings();
            case 1, 2, 3 -> summonUnits();
        }
        actionCounter++;
    }

    private void performHardActions() {
        switch (actionCounter % 8) {
            case 0 -> buildBuildings();
            case 1, 2, 3, 4, 5, 6 -> summonUnits();
            case 7 -> levelUpBuildingsAndUnits();
        }
        actionCounter++;
    }

    private boolean summonUnits() {
        int choice = random.nextInt(3); // ランダムでユニットを選択
        switch (choice) {
            case 0 -> {
                if (botPlayer.getResources() >= 30) {
                    double x = botPlayer.getCastle().getX() + random.nextInt(100);
                    double y = botPlayer.getCastle().getY() + random.nextInt(100);
                    SiegeUnit siegeUnit = new SiegeUnit(x, y, botPlayer, 1);
                    botPlayer.addUnit(siegeUnit);
                    game.addUnit(siegeUnit);
                    botPlayer.spendResources(30);
                    System.out.println("Bot summoned SiegeUnit.");
                    return true;
                }
            }
            case 1 -> {
                if (botPlayer.getResources() >= 60) {
                    double x = botPlayer.getCastle().getX() + random.nextInt(100);
                    double y = botPlayer.getCastle().getY() + random.nextInt(100);
                    MageUnit mageUnit = new MageUnit(x, y, botPlayer, 1);
                    botPlayer.addUnit(mageUnit);
                    game.addUnit(mageUnit);
                    botPlayer.spendResources(60);
                    System.out.println("Bot summoned MageUnit.");
                    return true;
                }
            }
            case 2 -> {
                if (botPlayer.getResources() >= 20) {
                    double x = botPlayer.getCastle().getX() + random.nextInt(100);
                    double y = botPlayer.getCastle().getY() + random.nextInt(100);
                    ArcherUnit archerUnit = new ArcherUnit(x, y, botPlayer, 1);
                    botPlayer.addUnit(archerUnit);
                    game.addUnit(archerUnit);
                    botPlayer.spendResources(20);
                    System.out.println("Bot summoned ArcherUnit.");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean buildBuildings() {
        if (botPlayer.getResources() >= 60 && !isResourceBuildingBuilt) {
            double x = botPlayer.getCastle().getX() + random.nextInt(50);
            double y = botPlayer.getCastle().getY() + random.nextInt(100);

            ResourceBuilding resourceBuilding = new ResourceBuilding(x, y, botPlayer);
            botPlayer.addBuilding(resourceBuilding);
            game.addBuilding(resourceBuilding);
            botPlayer.spendResources(60);
            isResourceBuildingBuilt = true;
            System.out.println("Bot built ResourceBuilding.");
            return true;
        }

        if (botPlayer.getResources() >= 75) {
            double x = botPlayer.getCastle().getX() + random.nextInt(50);
            double y = botPlayer.getCastle().getY() + random.nextInt(100);

            DefenseBuilding defenseBuilding = new DefenseBuilding(x, y, botPlayer);
            botPlayer.addBuilding(defenseBuilding);
            game.addBuilding(defenseBuilding);
            botPlayer.spendResources(75);
            System.out.println("Bot built DefenseBuilding.");
            return true;
        }

        return false;
    }

    private boolean levelUpBuildingsAndUnits() {
        for (Building building : botPlayer.getBuildings()) {
            int nextLevel = building.getLevel() + 1;
            int upgradeCost = building.getCost() * nextLevel;
            if (botPlayer.spendResources(upgradeCost)) {
                building.levelUp(nextLevel);
                System.out.println("Bot leveled up building to level " + nextLevel);
                return true;
            }
        }

        if (botPlayer.levelUpUnits(UnitType.ARCHER)) {
            System.out.println("Bot leveled up Archer units.");
            return true;
        }
        if (botPlayer.levelUpUnits(UnitType.MAGE)) {
            System.out.println("Bot leveled up Mage units.");
            return true;
        }
        if (botPlayer.levelUpUnits(UnitType.SIEGE)) {
            System.out.println("Bot leveled up Siege units.");
            return true;
        }

        return false;
    }

    public void stop() {
        botTimer.cancel();
    }
}
