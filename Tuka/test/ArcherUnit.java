import java.awt.Graphics;
import java.awt.Color;

// ArcherUnit ターゲットは最寄りの敵防衛設備、超遠距離攻撃
public class ArcherUnit extends Unit {
    
    private double attackRange; // 攻撃範囲
    private double attackPower; // 攻撃力
    private double attackCooldown;
    private double timeSinceLastAttack;
    private double targetX, targetY;
    private int size = 30; // ユニットのサイズ
    private Building target;

    public ArcherUnit(double x, double y, Player owner) {
        super(x, y, 20.0, 50, 100.0, UnitType.MAGE, owner);
        this.attackRange = 300;
        this.attackPower = 30;
        this.attackCooldown = 3.0; // 攻撃間隔（秒）
        this.timeSinceLastAttack = 0;
    }

    // 与えられた座標が攻撃範囲内か判定
    private boolean canAttack(double targetX, double targetY) {
        double dx = targetX - this.x;
        double dy = targetY - this.y;
        return dx * dx + dy * dy <= attackRange * attackRange;
    }

    // 最も近い敵防衛設備を探す
    private Building findTargetDefenseBuilding(Game game) {
        Building closestBuilding = null;
        double closestDistance = Double.MAX_VALUE;

        for (Building building: game.getBuildings()) {
            // 建物がアクティブかつ敵の建物かつ防衛設備の場合
            if (building.isActive() && building.getOwner() != this.getOwner() && building.getType() == BuildingType.DEFENSE) {
                double distance = Math.hypot(building.getX() - this.x, building.getY() - this.y);
                if (distance < closestDistance) {
                    closestBuilding = building;
                    closestDistance = distance;
                }
            }
        }
        return closestBuilding;
    }

    @Override
    public void update(double deltaTime, Game game) {
        timeSinceLastAttack += deltaTime;

        // 死亡時の処理
        if(isDead())
            active = false;

        // 常に最寄りの敵防衛設備を探す
        target = findTargetDefenseBuilding(game);

        // ターゲットがいない場合、何もしない
        if (target == null)
            return;
        
        targetX = target.getX();
        targetY = target.getY();
        
        // ターゲットが攻撃範囲外の場合、移動
        if (!canAttack(targetX, targetY)) {
            double dx = targetX - x;
            double dy = targetY - y;
            double distance = Math.hypot(dx, dy);
            if (distance > this.speed * deltaTime) {
                this.x += (dx / distance) * this.speed * deltaTime;
                this.y += (dy / distance) * this.speed * deltaTime;
            } else {
                this.x = targetX;
                this.y = targetY;
            }
        // ターゲットが攻撃範囲内の場合、攻撃
        } else  {
            if (timeSinceLastAttack >= attackCooldown) {
                // プロジェクタイルを発射
                Projectile p = new Projectile(x, y, 100, attackPower, 10, Color.YELLOW);
                p.setTargetBuilding(target);
                game.addProjectile(p);
                timeSinceLastAttack = 0;
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        if (!active) 
            return;
        
        // 攻城ユニットの描画（赤の丸）
        g.setColor(Color.RED);
        g.fillOval((int) x - size / 2, (int) y - size / 2, size, size);

        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int) x - 10, (int) y - 13, 20, 4);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int) ((health / MaxHealth) * 20);
        g.fillRect((int) x - 10, (int) y - 13, hpBarWidth, 4);
    }
}
