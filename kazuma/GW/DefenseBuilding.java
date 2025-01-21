import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.ImageIcon;
import java.awt.Image;

public class DefenseBuilding extends Building {
    private double attackRange = 150.0; // 攻撃範囲
    private double attackPower; // 攻撃力
    private double attackCooldown; // 攻撃間隔（秒）
    private double timeSinceLastAttack = 0; // 最後の攻撃からの時間
    private int size = 50; // 防衛設備のサイズ
    private Unit target = null; // 攻撃対象

    public DefenseBuilding(double x, double y, Player owner) {
        super(x, y, 150, 400.0, BuildingType.DEFENSE, owner);
        this.attackPower = 1;
        this.timeSinceLastAttack = 0;
    }

    // レベルアップに必要なコスト
    public int getLevelUpCost() {
        return -1;
    }

    // レベルアップ処理（HPが変化）
    @Override
    public void levelUp(int targetLevel) {
        if (targetLevel > this.level) {
            while (this.level < targetLevel) {
                this.level++;
                // 一律の計算式
                this.MaxHealth += 100;
                this.health += 100;
                System.out.println("Buildingレベルが " + this.level + " になりました");
            }
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
        if (isDead()) {
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
    public void draw(Graphics g, Image image) {
        if (!active) {
            return;
        }
        drawRange(g);
        // 防衛設備の描画
        g.drawImage(image, (int) x - size / 2, (int) y - size / 2, size, size, null);

        // レベル表示
        g.setColor(Color.WHITE);
        g.drawString("Lv " + level, (int) x - 10, (int) y + 20);

        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int) x - 14, (int) y - 22, 30, 3);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int) ((health / MaxHealth) * 30);
        g.fillRect((int) x - 14, (int) y - 22, hpBarWidth, 3);

    }

    // 攻撃可能範囲の描画
    public void drawRange(Graphics g) {
        g.setColor(new Color(255, 0, 0, 25)); // 半透明の赤
        g.fillOval((int) (x - attackRange), (int) (y - attackRange), (int) (attackRange * 2), (int) (attackRange * 2));
    }
}
