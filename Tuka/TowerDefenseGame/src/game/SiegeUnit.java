package game;

import java.awt.Graphics;
import java.awt.Color;

public class SiegeUnit extends Unit {
    public SiegeUnit(double x, double y, double speed, int cost) {
        super(x, y, speed, cost);
    }

    @Override
    public void update(double deltaTime) {
        // クライアント側では特に処理なし
    }

    @Override
    public void draw(Graphics g) {
        // 攻城ユニットの描画（オレンジの丸）
        g.setColor(Color.ORANGE);
        g.fillOval((int)x - 10, (int)y - 10, 20, 20);

        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int)x - 10, (int)y - 15, 20, 4);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int)((hp / 150.0) * 20);
        g.fillRect((int)x - 10, (int)y - 15, hpBarWidth, 4);
    }
}