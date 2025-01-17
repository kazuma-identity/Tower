import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.awt.Image;

public abstract class Unit {
    protected double x, y;
    protected double speed;
    protected double health; // 現在のHP
    protected int level;
    protected int cost;
    protected Player owner; // ユニットの所有者
    protected UnitType type;
    protected boolean active;

    public Unit(double x, double y, double speed, int cost, double health, UnitType type, Player owner) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.cost = cost;
        this.health = health;
        this.type = type;
        this.owner = owner;
        this.level = 1;
        this.active = true;
    }

    // ゲッターとセッター
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSpeed() { return speed; }
    public double getHp() { return health; }
    public int getLevel() { return level; }
    public int getCost() { return cost; }
    public Player getOwner() { return owner; }
    public UnitType getType() { return type; }
    public boolean isActive() { return active; }

    public void levelup() { level++; }

    // 被ダメージ処理
    public void takeDamage(double damage) { health -= damage; }

    // 死亡判定
    public boolean isDead() { return health<=0; }

    public abstract void update(double deltaTime, Game game);

    public abstract void draw(Graphics g, Image image);
}
