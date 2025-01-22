import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.awt.Image;

public abstract class Unit {
    protected double x, y;
    protected double speed;
    protected double MaxHealth; // 最大HP
    protected double health; // 現在のHP
    protected int level;
    protected int cost;
    protected Player owner; // ユニットの所有者
    protected UnitType type;
    protected boolean active;

    public Unit(double x, double y, double speed, int cost, double health, UnitType type, Player owner, int level) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.cost = cost;
        this.MaxHealth = this.health = health;
        this.type = type;
        this.owner = owner;
        this.level = level;
        this.active = true;
    }

    // ゲッターとセッター
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSpeed() {
        return speed;
    }

    public double getHp() {
        return health;
    }

    public int getLevel() {
        return level;
    }

    public int getCost() {
        return cost;
    }

    public Player getOwner() {
        return owner;
    }

    public UnitType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    // 被ダメージ処理
    public void takeDamage(double damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
            active = false;
        }
    }

    // 死亡判定
    public boolean isDead() {
        return health <= 0;
    }

    // レベルアップ関連の抽象メソッド
    public abstract void levelUp(int targetLevel);

    public abstract int getLevelUpCost(int targetLevel);

    // 更新と描画
    public abstract void update(double deltaTime, Game game);

    public abstract void draw(Graphics g, Image image);

}
