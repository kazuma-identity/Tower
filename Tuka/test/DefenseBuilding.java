import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.*;

public class DefenseBuilding extends Building {
    private double attackRange = 150.0; // 攻撃範囲
    private double attackPower = 50.0; // 攻撃力
    private double attackCooldown = 1.5; // 攻撃間隔（秒）
    private double timeSinceLastAttack = 0; // 最後の攻撃からの時間
    private int size = 50; // 防衛設備のサイズ
    private Unit target = null; // 攻撃対象

    public DefenseBuilding(double x, double y, Player owner) {
        super(x, y, 150, 150.0, BuildingType.DEFENSE, owner);
    }

    // 攻撃可能判定
    

    // レベル処理
    public void levelprc(int level) {
        switch(level) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }

    // 与えられたユニットが攻撃範囲内にいるか判定
    private boolean canAttack(double targetX, double targetY) {
        double dx = targetX - this.x;
        double dy = targetY - this.y;
        return dx * dx + dy * dy <= attackRange * attackRange;
    }

    // 攻撃範囲内にいる最も近い敵ユニットを探す
    private Unit findTarget(Game game) {
        Unit closestUnit = null;
        double closestDistance = Double.MAX_VALUE;

        for (Unit unit : game.getUnits()) {
            // ユニットがアクティブかつ敵ユニットかつ攻撃範囲内にいる場合
            if (unit.isActive() && unit.getOwner() != this.getOwner() && canAttack(unit.getX(), unit.getY())) {
                double distance = Math.hypot(unit.getX() - this.x, unit.getY() - this.y);
                if (distance < closestDistance) {
                    closestUnit = unit;
                    closestDistance = distance;
                }
            }
        }
        return closestUnit;
    }

    @Override
    public void update(double deltaTime, Game game) {
        timeSinceLastAttack += deltaTime;

        // 被破壊時の処理
        if(isDead()) {
            active = false;
        }

        // 現在のターゲットが非アクティブまたは存在しないまたは攻撃範囲外の場合、再ターゲット
        if (target == null || !target.isActive() || !canAttack(target.getX(), target.getY())) {
            target = findTarget(game);
        }

        // ターゲットが存在する場合、攻撃
        if (target != null) {
            if (timeSinceLastAttack >= attackCooldown) {
                // プロジェクタイルを発射
                Projectile p = new Projectile(x, y, 300, attackPower, 10, Color.WHITE);
                p.setTargetUnit(target);
                game.addProjectile(p);
                timeSinceLastAttack = 0;
            }
        }
    }

    // 施設自体の描画
    @Override
    public void draw(Graphics g) {
        if(!active) { return; }
        drawRange(g);
        // 防衛設備の描画（赤の四角）
        g.setColor(Color.RED);
        g.fillRect((int) x - size / 2, (int) y - size / 2, size, size);

        // レベル表示
        g.setColor(Color.WHITE);
        g.drawString("Lv " + level, (int) x - 10, (int) y + 20);
        
        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int)x - 14, (int)y - 22, 30, 3);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int)((health / MaxHealth) * 30);
        g.fillRect((int)x - 14, (int)y - 22, hpBarWidth, 3);

        // クールダウンバーの描画
        g.setColor(Color.BLACK);
        g.fillRect((int)x - 14, (int)y - 16, 30, 3);
        g.setColor(new Color(157, 204, 224));
        int cdBarWidth = (int)((timeSinceLastAttack / attackCooldown) * 30);
        if (timeSinceLastAttack >= attackCooldown) { cdBarWidth = 30; }
        g.fillRect((int)x - 14, (int)y - 16, cdBarWidth, 3);
    }

    // 攻撃可能範囲の描画
    public void drawRange(Graphics g) {
        g.setColor(new Color(255, 0, 0, 25)); // 半透明の赤
        g.fillOval((int) (x - attackRange), (int) (y - attackRange), (int) (attackRange * 2), (int) (attackRange * 2));
    }
}
