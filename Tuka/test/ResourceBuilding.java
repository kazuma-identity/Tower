import java.awt.Graphics;
import java.awt.Color;

public class ResourceBuilding extends Building {
    private int size = 50; // 資源設備のサイズ
    private double addResourceRate;

    public ResourceBuilding(double x, double y, Player owner) {
        super(x, y, 100, 100, BuildingType.RESOURCE, owner);
        this.addResourceRate = 5.0;
    }

    @Override
    public void update(double deltaTime, Game game) {
        // 被破壊時の処理
        if(isDead()) {
            active = false;
        }
        // 資源施設による追加資源生成
        game.getBuildingOwner(this).addResources((addResourceRate * deltaTime));
    }

    @Override
    public void draw(Graphics g) {
        if(!active) { return; }
        // 資源設備の描画（ピンクの丸）
        g.setColor(Color.PINK);
        g.fillOval((int) x - size / 2, (int) y - size / 2, size, size);

        // レベル表示
        g.setColor(Color.WHITE);
        g.drawString("Lv " + level, (int) x - 10, (int) y + 20);
        
        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int)x - 10, (int)y - 20, 20, 3);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int)((health / 100.0) * 20);
        g.fillRect((int)x - 10, (int)y - 20, hpBarWidth, 3);

        // 資源施設固有の情報があればここに追加
    }
}
