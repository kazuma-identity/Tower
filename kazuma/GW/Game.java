import java.util.List;
import java.util.ArrayList;

public class Game {
    private Player player;
    private Player bot;
    private List<Unit> units;
    private List<Building> buildings;
    private List<Projectile> projectiles;
    private GamePanel gamePanel;

    // 基礎資源生成速度（資源/秒）
    private final double baseResourceRate = 10.0;

    public Game() {
        units = new ArrayList<>();
        buildings = new ArrayList<>();
        projectiles = new ArrayList<>();
    }

    // ゲッターとセッター
    public Player getPlayer() {
        return player;
    }

    public Player getBot() {
        return bot;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public Player getOpponent(Player me) {
        if (me == player) {
            return bot;
        }
        if (me == bot) {
            return player;
        }
        return null;
    }

    public Player getUnitOwner(Unit unit) {
        if (player.getUnits().contains(unit)) {
            return player;
        }
        if (bot.getUnits().contains(unit)) {
            return bot;
        }
        return null;
    }

    public Player getBuildingOwner(Building building) {
        if (player.getBuildings().contains(building)) {
            return player;
        }
        if (bot.getBuildings().contains(building)) {
            return bot;
        }
        return null;
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(player);
        players.add(bot);
        return players;
    }

    public void setGamePanel(GamePanel panel) {
        this.gamePanel = panel;
    }

    public void setPlayers(Player player, Player bot) {
        this.player = player;
        this.bot = bot;
    }

    public void addUnit(Unit unit) {
        units.add(unit);
    }

    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    public void addBuilding(Building building) {
        buildings.add(building);
    }

    public void removeBuilding(Building building) {
        buildings.remove(building);
    }

    public void addProjectile(Projectile p) {
        projectiles.add(p);
    }

    public void removeProjectile(Projectile p) {
        projectiles.remove(p);
    }

    // 画面サイズに基づいてユニットが陣地内にいるか判定
    public boolean isWithinTerritory(Player player, double x, double y) {
        if (player.getCastle().getX() < 400) { // 左側プレイヤー
            return x <= 400;
        } else { // 右側プレイヤー
            return x >= 400;
        }
    }

    public void update(double deltaTime) {
        // 基礎資源生成の更新
        player.addResources((baseResourceRate * deltaTime));
        bot.addResources((baseResourceRate * deltaTime));

        // 建物の更新
        for (Building building : new ArrayList<>(buildings)) {
            building.update(deltaTime, this);
            if (!building.isActive()) {
                removeBuilding(building);
                building.getOwner().removeBuilding(building);
            }
        }

        // ユニットの更新
        for (Unit unit : new ArrayList<>(units)) { // コピーリストを使用してConcurrentModificationExceptionを防ぐ
            unit.update(deltaTime, this);
            if (!unit.isActive()) {
                removeUnit(unit);
                unit.getOwner().removeUnit(unit);
            }
        }

        // プロジェクタイルの更新
        for (Projectile p : new ArrayList<>(projectiles)) {
            p.update(deltaTime, this);
            if (!p.isActive()) {
                removeProjectile(p);
            }
        }

        // 勝敗判定
        if (player.getCastle().getHp() <= 0) {
            if (gamePanel != null) {
                gamePanel.showGameOver(bot.getName());
            }
        }
        if (bot.getCastle().getHp() <= 0) {
            if (gamePanel != null) {
                gamePanel.showGameOver(player.getName());
            }
        }
    }
}
