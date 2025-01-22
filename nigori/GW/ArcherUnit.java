import java.awt.Graphics;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Image;

// ArcherUnit ターゲットは最寄りの敵防衛設備、超遠距離攻撃
public class ArcherUnit extends Unit {
    
    private double attackRange = 300.0; // 攻撃範囲
    private double attackPower; // 攻撃力
    private double attackCooldown = 3.0;
    private double timeSinceLastAttack;
    private double targetX, targetY;
    private int size = 30; // ユニットのサイズ
    private Building target;

    public ArcherUnit(double x, double y, Player owner, int level) {
        super(x, y, 20.0, 50, 100.0, UnitType.MAGE, owner, level);
        levelUp(level);
        this.timeSinceLastAttack = 0;
    }

    // レベルアップに必要なコスト
    public int getLevelUpCost(int tolevel) {
        if (tolevel == 2) 
            return 250;
        else if (tolevel == 3)
            return 500;
        else
            return -1; // 不明な値が入力された場合
    }

    // レベルアップ処理（HPと攻撃力が変化）
    public void levelUp(int level) {
        switch (level) {
            case 1:
                this.MaxHealth = this.health = 100.0;
                this.attackPower = 30.0;
                break;
            case 2:
                this.MaxHealth = this.health = 150.0;
                this.attackPower = 50.0;
                break;
            case 3:
                this.MaxHealth = this.health = 300.0;
                this.attackPower = 90.0;
                break;
            default:
                return;
        }
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
        // 現在のターゲットが非アクティブまたは存在しないまたは攻撃範囲外の場合、再ターゲット
        if (target == null || !target.isActive() || !canAttack(target.getX(), target.getY())) {
            target = findTargetDefenseBuilding(game);
        }

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
                p.setImages("Playerarrow.png");
                game.addProjectile(p);
                timeSinceLastAttack = 0;
            }
        }
    }

    @Override
    public void draw(Graphics g, Image image) {
        if (!active) 
            return;
        
        // 攻城ユニットの描画
        g.drawImage(image, (int) x-15, (int) y-10, 24, 24, null);

        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int) x - 10, (int) y - 13, 20, 4);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int) ((health / MaxHealth) * 20);
        g.fillRect((int) x - 10, (int) y - 13, hpBarWidth, 4);
    }
}
