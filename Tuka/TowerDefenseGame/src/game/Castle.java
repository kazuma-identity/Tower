package game;

import java.awt.Graphics;
import java.awt.Color;

public class Castle implements GameObject {
    private double x, y;
    private int health;

    public Castle(double x, double y, int health) {
        this.x = x;
        this.y = y;
        this.health = health;
    }

    // ゲッターとセッター
    public int gethealth() { return health; }

    public void takeDamage(int damage) {
        health -= damage;
        if(health<0) { health = 0; }
    }

    // 更新メソッド
    @Override
    public void update(double deltaTime) {}

    // 描画メソッド
    @Override
    public void draw(Graphics g) {
        // 城の描画（グレーの四角）
        g.setColor(Color.GRAY);
        g.fillRect((int)x, (int)y, 50, 50);
        
        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int)x - 8, (int)y - 12, 16, 3);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int)((health / 100.0) * 16);
        g.fillRect((int)x - 8, (int)y - 12, hpBarWidth, 3);
    }
}