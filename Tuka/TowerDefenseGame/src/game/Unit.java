package game;

import java.awt.Graphics;

public abstract class Unit implements GameObject {
    protected double x, y;
    protected double speed;
    protected int hp;
    protected int level;
    protected int cost;

    public Unit(double x, double y, double speed, int cost) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.cost = cost;
        this.level = 1;
        this.hp = 100; // 初期HP
    }

    // ゲッターとセッター
    public double getX() { return x; }
    public double getY() { return y; }
    public int getHp() { return hp; }
    public void damage(int amount) { hp -= amount; }
    public int getLevel() { return level; }
    public void levelup() { level++; }
    public int getCost() { return cost; }

    // 更新と描画の抽象メソッド
    @Override
    public abstract void update(double deltaTime);
    @Override
    public abstract void draw(Graphics g);
}