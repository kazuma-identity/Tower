package game;

import java.awt.Graphics;
import java.awt.Color;

public class DefenseUnit extends Unit {
    public DefenseUnit(double x, double y) {
        super(x, y, 3, 10);
    }

    @Override
    public void update(double deltaTime) {
        // クライアント側では特に処理なし
    }

    @Override
    public void draw(Graphics g) {
        // 防衛ユニットの描画（青の丸）
        g.setColor(Color.BLUE);
        g.fillOval((int)x - 8, (int)y - 8, 16, 16);

        // レベル表示
        g.setColor(Color.WHITE);
        g.drawString("Lv " + level, (int)x - 10, (int)y + 5);

        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int)x - 8, (int)y - 12, 16, 3);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int)((hp / 100.0) * 16);
        g.fillRect((int)x - 8, (int)y - 12, hpBarWidth, 3);
    }
}