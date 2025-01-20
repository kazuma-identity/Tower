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

    public Bot(Game game, Player botPlayer, Player opponent) {
        this.game = game;
        this.botPlayer = botPlayer;
        this.opponent = opponent;
        this.random = new Random();
    }

    public void start() {
        botTimer = new Timer(); // Timer オブジェクトを生成
        botTimer.scheduleAtFixedRate(new TimerTask() { // TimerTask を匿名クラスで定義
            @Override
            public void run() {
                performActions();
            }
        }, 0, 5000); // 5秒ごとに行動
    }

    private void performActions() {
        // 資源生成建物の生成
        if (botPlayer.getResources() >= 100 && !isResourceBuildingBuilt) { // 資源生成建物のコスト
            double x = botPlayer.getCastle().getX() + 75; // 城の後ろのどっちかに配置（修正したい）
            double y = botPlayer.getCastle().getY() + random.nextInt(200) - 100;

            ResourceBuilding resourceBuilding = new ResourceBuilding(x, y, botPlayer);
            botPlayer.addBuilding(resourceBuilding);
            botPlayer.spendResources(100);
            isResourceBuildingBuilt = true;
        }
    
        // 攻城ユニットの生成
        if (botPlayer.getResources() >= 100) { // 攻城ユニットのコスト
            double x = botPlayer.getCastle().getX();
            double y = botPlayer.getCastle().getY() + random.nextInt(100) - 50; // 城周辺に配置
            SiegeUnit siegeUnit = new SiegeUnit(x, y, botPlayer, 1);
            botPlayer.addUnit(siegeUnit);
            game.addUnit(siegeUnit);
            botPlayer.spendResources(100);
        }

        // Mageユニットの生成
        if (botPlayer.getResources() >= 50) { // 防衛ユニットのコスト
            double x = botPlayer.getCastle().getX() + random.nextInt(100) - 50;
            double y = botPlayer.getCastle().getY() + random.nextInt(100) - 50;
            MageUnit mageUnit = new MageUnit(x, y, botPlayer, 1);
            botPlayer.addUnit(mageUnit);
            game.addUnit(mageUnit);
            botPlayer.spendResources(100);
        }

        // Archerユニットの生成
        if (botPlayer.getResources() >= 50) { // 防衛ユニットのコスト
            double x = botPlayer.getCastle().getX() + random.nextInt(100) - 50;
            double y = botPlayer.getCastle().getY() + random.nextInt(100) - 50;
            ArcherUnit archerUnit = new ArcherUnit(x, y, botPlayer, 1);
            botPlayer.addUnit(archerUnit);
            game.addUnit(archerUnit);
            botPlayer.spendResources(50);
        }

        // 資源生成建物のアップグレード
//        for (Building building : botPlayer.getBuildings()) {
//            if (building instanceof ResourceBuilding) {
//                int upgradeCost = building.getCost() * building.getLevel();
//                if (botPlayer.spendResources(upgradeCost)) {
//                    ResourceBuilding Rbuilding = building;
//                    Rbuilding.levelUp();
//                }
//            }
//        }
    }

    public void stop() {
        botTimer.cancel();
    }
}