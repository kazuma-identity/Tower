import java.awt.Graphics;
import java.awt.Image;

public abstract class Building {
    protected double x, y; // 設置位置
    protected int level; // レベル
    protected double MaxHealth; // 最大耐久値
    protected double health; // 現在の耐久値
    protected int cost; // 必要コスト
    protected BuildingType type; // 建物の種類
    protected Player owner; // 建物の所有者
    protected boolean active; // 破壊されていないか

    public Building(double x, double y, int cost, double MaxHealth, BuildingType type, Player owner) {
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.MaxHealth = this.health = MaxHealth;
        this.level = 1;
        this.type = type;
        this.owner = owner;
        this.active = true;
    }

    // ゲッターとセッター
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getLevel() {
        return level;
    }

    public double getHp() {
        return health;
    }

    public int getCost() {
        return cost;
    }

    public BuildingType getType() {
        return type;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isActive() {
        return active;
    }

    // 被ダメージ処理
    public void takeDamage(double damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
            active = false; // 耐久値が0以下で非アクティブ化
        }
    }

    // 被破壊判定
    public boolean isDead() {
        return !active;
    }

    // レベルアップメソッド
    public abstract void levelUp(int level);

    // レベルアップに必要なコストを取得
    public abstract int getLevelUpCost();

    // 更新と描画の抽象メソッド
    public abstract void update(double deltaTime, Game game);

    public abstract void draw(Graphics g, Image image);
}
