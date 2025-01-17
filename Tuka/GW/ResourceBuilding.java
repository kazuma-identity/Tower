import java.awt.Graphics;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Image;

public class ResourceBuilding extends Building {
    private double addResourceRate; // 追加資源生成速度（秒）

    public ResourceBuilding(double x, double y, Player owner) {
        super(x, y, 100, 100, BuildingType.RESOURCE, owner);
        this.addResourceRate = 5.0;
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

    // レベルアップ処理（HPと追加資源生成速度が変化）
    public void levelUp() {
        switch (this.level) {
            case 1:
                this.MaxHealth = 150.0;
                this.health += 50.0; // 最大HPの増加に合わせて現在HPも増加
                this.addResourceRate = 10.0;
                this.level++;
                break;
            case 2:
                this.MaxHealth = 200.0;
                this.health += 50.0;
                this.addResourceRate = 30.0;
                this.level++;
                break;
            default:
                return;
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
        if(!active) return;
        // 資源設備の描画
        g.drawImage(image, (int) x-15, (int) y-10, 32, 32, null);

        // レベル表示
        g.setColor(Color.WHITE);
        g.drawString("Lv " + level, (int) x - 10, (int) y + 28);
        
        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int)x - 10, (int)y - 20, 20, 3);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int)((health / 100.0) * 20);
        g.fillRect((int)x - 10, (int)y - 20, hpBarWidth, 3);

        // 資源施設固有の情報があればここに追加
    }
}
