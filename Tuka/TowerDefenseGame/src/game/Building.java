package game;

import java.awt.Graphics;

public abstract class Building implements GameObject {
    protected double x, y; // 設置位置
    protected int level; // レベル
    protected int health; // 耐久値
    protected int cost; // 必要コスト
    protected int power; // 防衛：攻撃力　資源：資源獲得数
    protected Player owner; // 建物の所有者

    public Building(double x, double y, int cost, int health, int power, Player owner) {
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.health = health;
        this.level = 1;
        this.power = power;
        this.owner = owner;
    }

    // ゲッターとセッター
    public double getX() { return x; }
    public double getY() { return y; }
    public int getLevel() { return level; }
    public void levelup() { level++; }
    public int gethealth() { return health; }
    public int getCost() { return cost; }
    public int getPower() { return power; }
    public Player getPlayer() { return owner; }

    // 更新と描画の抽象メソッド
    @Override
    public abstract void update(double deltaTime);
    @Override
    public abstract void draw(Graphics g);
}