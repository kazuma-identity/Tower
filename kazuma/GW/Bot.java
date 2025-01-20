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

    public Bot(Game game, Player botPlayer, Player opponent, Difficulty difficulty) {
        this.game = game;
        this.botPlayer = botPlayer;
        this.opponent = opponent;
        this.random = new Random();
        this.difficulty = difficulty; // 難易度をセット
    }

    public void start() {
        botTimer = new Timer();
        botTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                performActions();
            }
        }, 0, 5000); // 5秒ごとに行動
    }

    private void performActions() {
        switch (difficulty) {
            case EASY -> performEasyActions();
            case NORMAL -> performNormalActions();
            case HARD -> performHardActions();
        }
    }

    private void performEasyActions() {
        summonUnits();
    }

    private void performNormalActions() {
        summonUnits();
        buildBuildings();
    }

    private void performHardActions() {
        summonUnits();
        buildBuildings();
        levelUpBuildingsAndUnits();
    }

    private void summonUnits() {
        // 攻城ユニットの生成
        if (botPlayer.getResources() >= 100) {
            double x = botPlayer.getCastle().getX();
            double y = botPlayer.getCastle().getY() + random.nextInt(100) - 50;
            SiegeUnit siegeUnit = new SiegeUnit(x, y, botPlayer, 1);
            botPlayer.addUnit(siegeUnit);
            game.addUnit(siegeUnit);
            botPlayer.spendResources(100);
        }

        // Mageユニットの生成
        if (botPlayer.getResources() >= 50) {
            double x = botPlayer.getCastle().getX() + random.nextInt(100) - 50;
            double y = botPlayer.getCastle().getY() + random.nextInt(100) - 50;
            MageUnit mageUnit = new MageUnit(x, y, botPlayer, 1);
            botPlayer.addUnit(mageUnit);
            game.addUnit(mageUnit);
            botPlayer.spendResources(50);
        }

        // Archerユニットの生成
        if (botPlayer.getResources() >= 50) {
            double x = botPlayer.getCastle().getX() + random.nextInt(100) - 50;
            double y = botPlayer.getCastle().getY() + random.nextInt(100) - 50;
            ArcherUnit archerUnit = new ArcherUnit(x, y, botPlayer, 1);
            botPlayer.addUnit(archerUnit);
            game.addUnit(archerUnit);
            botPlayer.spendResources(50);
        }
    }

    private void buildBuildings() {
        if (botPlayer.getResources() >= 100 && !isResourceBuildingBuilt) {
            double x = botPlayer.getCastle().getX() + 75;
            double y = botPlayer.getCastle().getY() + random.nextInt(200) - 100;

            ResourceBuilding resourceBuilding = new ResourceBuilding(x, y, botPlayer);
            botPlayer.addBuilding(resourceBuilding);
            botPlayer.spendResources(100);
            isResourceBuildingBuilt = true;
        }

        if (botPlayer.getResources() >= 100) {
            double x = botPlayer.getCastle().getX() + random.nextInt(100) - 50;
            double y = botPlayer.getCastle().getY() + random.nextInt(100) - 50;

            DefenseBuilding defenseBuilding = new DefenseBuilding(x, y, botPlayer);
            botPlayer.addBuilding(defenseBuilding);
            botPlayer.spendResources(100);
        }
    }

    private void levelUpBuildingsAndUnits() {
        for (Building building : botPlayer.getBuildings()) {
            if (building instanceof ResourceBuilding) {
                int nextLevel = building.getLevel() + 1; // 次のレベルを計算
                int upgradeCost = building.getCost() * nextLevel;
                if (botPlayer.spendResources(upgradeCost)) {
                    building.levelUp(nextLevel); // 適切なレベルを渡す
                }
            }
        }

        botPlayer.levelUpUnits(UnitType.ARCHER);
        botPlayer.levelUpUnits(UnitType.MAGE);
        botPlayer.levelUpUnits(UnitType.SIEGE);
    }

    public void stop() {
        botTimer.cancel();
    }
}
