import java.awt.Graphics;
import java.awt.Color;

public class Projectile {
    private double x, y;
    private double speed; // 弾の早さ
    private double directionX, directionY; // 弾の進む方向
    private double attackPower; // 弾の攻撃力
    private Unit targetUnit = null; // 弾の標的（ユニット）
    private Building targetBuilding = null; // 弾の標的（建物）
    private Castle targetCastle = null; // 弾の標的（城）
    private double targetX, targetY; // 弾の標的の座標
    private int size = 10; // 弾のサイズ
    private Color color; // 弾の色
    private boolean active;

    public Projectile(double x, double y, double speed, double attackPower, int size, Color color) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.attackPower = attackPower;
        this.size = size;
        this.color = color;
        this.active = true;
    }

    // ゲッターとセッター
    public boolean isActive() { return active; }
    public void setTargetUnit(Unit targetUnit) { this.targetUnit = targetUnit; }
    public void setTargetBuilding(Building targetBuilding) { this.targetBuilding = targetBuilding; }
    public void setTargetCastle(Castle targetCastle) { this.targetCastle = targetCastle; }


    // 標的が何かを判定（ユニット:1、建物:2、城:0）
    private int targetObject() {
        if(targetUnit != null) {
            return 1;
        } else if(targetBuilding != null) {
            return 2;
        } else {
            return 0;
        }
    }

    public void update(double deltaTime, Game game) {

        // 標的が、ユニット:c=1、建物:c=2、城:c=0）
        int c = targetObject();
        if(c == 1) {
            targetX = targetUnit.getX();
            targetY = targetUnit.getY();
        } else if(c == 2) {
            targetX = targetBuilding.getX();
            targetY = targetBuilding.getY();
        } else {
            targetX = targetCastle.getX();
            targetY = targetCastle.getY();
        }
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.hypot(dx, dy);
        if (distance == 0) {
            this.directionX = 0;
            this.directionY = 0;
        } else {
            this.directionX = dx / distance;
            this.directionY = dy / distance;
        }
        x += directionX * speed * deltaTime;
        y += directionY * speed * deltaTime;

        // ターゲットにダメージを与える
        double dist = Math.hypot(x - targetX, y - targetY);
        if (dist <= 10) { // 当たり判定
            if(c == 1) {
                targetUnit.takeDamage(attackPower);
            } else if(c == 2) {
                targetBuilding.takeDamage(attackPower);
            } else {
                targetCastle.takeDamage(attackPower);
            }
            active = false;     // 当たったら無効化
        }

        // 画面外に出たら無効化
        if (x < 0 || x > 1000 || y < 0 || y > 600) { // 画面サイズに合わせて調整
            active = false;
        }
    }

    public void draw(Graphics g) {
        if (!active)
            return;
        // プロジェクタイルを小さな白い円として描画
        g.setColor(color);
        g.fillOval((int) x - size / 2, (int) y - size / 2, size, size);
    }
}
