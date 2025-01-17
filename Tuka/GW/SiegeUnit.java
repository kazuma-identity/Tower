import java.awt.Graphics;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Image;

public class SiegeUnit extends Unit {

    private double attackPower; // 攻撃力
    private double attackCooldown = 5.0; // 攻撃間隔（秒）
    private double timeSinceLastAttack;
    private double targetX, targetY;

    public SiegeUnit(double x, double y, Player owner, int level) {
        super(x, y, 50.0, 100, 300.0, UnitType.SIEGE, owner, level);
        levelUp(level);
        this.timeSinceLastAttack = 0;
    }

    // レベルアップに必要なコスト
    public int getLevelUpCost(int tolevel) {
        if (tolevel == 2) 
            return 300;
        else if (tolevel == 3)
            return 500;
        else
            return -1; // 不明な値が入力された場合
    }

    // レベルアップ処理（HPと攻撃力が変化）
    public void levelUp(int level) {
        switch (level) {
            case 1:
                this.MaxHealth = this.health = 300.0;
                this.attackPower = 50.0;
                break;
            case 2:
                this.MaxHealth = this.health = 400.0;
                this.attackPower = 70.0;
                break;
            case 3:
                this.MaxHealth = this.health = 700.0;
                this.attackPower = 90.0;
                break;
            default:
                return;
        }
    }


    @Override
    public void update(double deltaTime, Game game) {
        timeSinceLastAttack += deltaTime;

        // 死亡時の処理
        if(isDead())
            active = false;
        
        // 相手の城に向かって移動
        Player owner = game.getUnitOwner(this);
        Player opponent;
        if (owner == game.getPlayer()) {
            opponent = game.getBot();
        } else if(owner == game.getBot()) {
            opponent = game.getPlayer();
        } else {
            return;
        }

        targetX = opponent.getCastle().getX();
        targetY = opponent.getCastle().getY();

        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.hypot(dx, dy);
        if (distance > this.speed * deltaTime) {
            x += (dx / distance) * this.speed * deltaTime;
            y += (dy / distance) * this.speed * deltaTime;
        } else {
            x = targetX;
            y = targetY;
        }

        // 城に到達した場合のみ攻撃を行う
        if (x == targetX && y == targetY) {
            if (timeSinceLastAttack >= attackCooldown) {
                // 城を攻撃
                opponent.getCastle().takeDamage(attackPower);
                timeSinceLastAttack = 0;
            }
        }
    }

    @Override
    public void draw(Graphics g, Image image) {
        if (!active) { return; }
        g.drawImage(image, (int) x-15, (int) y-10, 24, 24, null);

        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int) x - 10, (int) y - 13, 20, 4);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int) ((health / MaxHealth) * 20);
        g.fillRect((int) x - 10, (int) y - 13, hpBarWidth, 4);
    }
}
