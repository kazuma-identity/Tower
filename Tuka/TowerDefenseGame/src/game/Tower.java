// 不明

package game;

import java.awt.Graphics;
import java.awt.Color;

public class Tower extends DefenseBuilding {
    public Tower(double x, double y) {
        super(x, y);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        // Tower特有の更新ロジックをここに追加
    }

    @Override
    public void draw(Graphics g) {
        // Towerの描画（より大きな赤い四角）
        g.setColor(Color.RED);
        g.fillRect((int)x - 20, (int)y - 20, 40, 40);

        // レベル表示
        g.setColor(Color.WHITE);
        g.drawString("Lv " + level, (int)x - 10, (int)y + 5);
    }
}