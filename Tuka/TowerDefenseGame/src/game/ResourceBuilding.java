// ResourceBuilding --- 資源の生産速度を上昇させる(一定時間ごとに資源獲得)
// 時間経過でHP減少


package game;

import java.awt.Color;
import java.awt.Graphics;

public class ResourceBuilding extends Building {
    public ResourceBuilding(double x, double y) {
        super(x, y, 15, 50, 5);
    }

    @Override
    public void update(double deltaTime) {
        // 1秒ごとにHPが1減少
        health -= deltaTime * 1;

        // 10秒ごとに資源をpowerの数だけ獲得
        if(deltaTime%10==0) {
//            gameresource += power;
        }
    }

    @Override
    public void draw(Graphics g) {
        // 資源設備の描画（緑の丸）
        g.setColor(Color.GREEN);
        g.fillOval((int)x - 15, (int)y - 15, 30, 30);

        // レベル表示
        g.setColor(Color.WHITE);
        g.drawString("Lv " + level, (int)x - 10, (int)y + 5);

        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int)x - 8, (int)y - 12, 16, 3);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int)((health / 100.0) * 16);
        g.fillRect((int)x - 8, (int)y - 12, hpBarWidth, 3);
    }
}