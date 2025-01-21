import java.awt.Graphics;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Image;

public class ResourceBuilding extends Building {
    protected int size = 50; // 資源設備のサイズ
    private double addResourceRate; // 資源生成速度

    public ResourceBuilding(double x, double y, Player owner) {
        super(x, y, 100, 200, BuildingType.RESOURCE, owner);
        this.addResourceRate = 2.5; // 資源生成速度（1秒あたりの資源生成量）
    }

    // レベルアップに必要なコスト
    public int getLevelUpCost() {
        return -1;
    }

    @Override
    public void levelUp(int targetLevel) {
        if (targetLevel > this.level) {
            while (this.level < targetLevel) {
                this.level++;
                // 一律の計算式
                this.MaxHealth += 200;
                this.health += 100;
                this.addResourceRate += 1.25;
                System.out.println("Buildingレベルが " + this.level + " になりました");
            }
        }
    }

    @Override
    public void update(double deltaTime, Game game) {

        if (isDead()) {
            active = false;
        }
        game.getBuildingOwner(this).addResources((addResourceRate * deltaTime));
    }

    @Override
    public void draw(Graphics g, Image image) {
        if (!active)
            return;
        // 資源設備の描画
        g.drawImage(image, (int) x - 15, (int) y - 10, 32, 32, null);

        // レベル表示
        g.setColor(Color.WHITE);
        g.drawString("Lv " + level, (int) x - 10, (int) y + 28);

        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int) x - 10, (int) y - 20, 20, 3);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int) ((health / 100.0) * 20);
        g.fillRect((int) x - 10, (int) y - 20, hpBarWidth, 3);

        // 資源施設固有の情報があればここに追加
    }
}
