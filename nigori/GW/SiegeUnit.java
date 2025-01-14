import java.awt.Graphics;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Image;

public class SiegeUnit extends Unit {

    private double attackPower; // 攻撃力
    private double attackCooldown; // 攻撃間隔（秒）
    private double timeSinceLastAttack;
    private double targetX, targetY;
    private int size = 30; // ユニットのサイズ

    public SiegeUnit(double x, double y, Player owner) {
        super(x, y, 50.0, 100, 300.0, UnitType.SIEGE, owner);
        this.attackPower = 50;
        this.attackCooldown = 5.0;
        this.timeSinceLastAttack = 0;
        this.targetX = x; // 初期ターゲットは相手の城
        this.targetY = y;        
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
