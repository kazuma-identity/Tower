import java.awt.Graphics;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Image;

public class ResourceBuilding extends Building {
    protected int size = 50; // 資源設備のサイズ
    private double addResourceRate; // 資源生成速度

    public ResourceBuilding(double x, double y, Player owner) {
        super(x, y, 100, 100, BuildingType.RESOURCE, owner);
        this.addResourceRate = 5.0; // 資源生成速度（1秒あたりの資源生成量）
    }

    // レベルアップに必要なコスト
    public int getLevelUpCost() {
        if (this.level == 1)
            return 150;
        else if (this.level == 2)
            return 200;
        else
            return -1; // 不明な値が入力された場合
    }

    @Override
    public void levelUp(int level) {
        if (level > this.level) {
            while (this.level < level) {
                this.level++;
                // レベルアップごとの処理
                if (this.level == 2) {
                    this.MaxHealth = 200;
                    this.health += 50; // 最大HPに合わせて現在HPを増加
                } else if (this.level == 3) {
                    this.MaxHealth = 300;
                    this.health += 100;
                }
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
