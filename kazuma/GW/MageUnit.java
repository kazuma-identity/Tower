import java.awt.Graphics;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Image;

// MageUnit ターゲットは最寄りの敵オブジェクト、中距離攻撃
public class MageUnit extends Unit {

    private double attackRange = 120.0; // 攻撃範囲
    private double attackPower = 20; // 攻撃力
    private double attackCooldown = 1.0;
    private double timeSinceLastAttack;
    private double targetX, targetY;
    private int size = 30; // ユニットのサイズ
    private Unit targetUnit;
    private Building targetBuilding;
    private int c = 0; // 最も近い敵が、ユニット:c=1、建物:c=2、城:c=0）

    public MageUnit(double x, double y, Player owner, int level) {
        super(x, y, 50.0, 100, 100.0, UnitType.MAGE, owner, level);
        // ユニットは種類ごとにレベルアップ（個々のユニットについてレベル処理はしない）
        levelUp(level);
        this.timeSinceLastAttack = 0;
    }

    // レベルアップに必要なコスト
    public int getLevelUpCost(int tolevel) {
        return -1; // 不明な値が入力された場合
    }

    // レベルアップ処理（HPと攻撃力が変化）
    public void levelUp(int targetLevel) {
        // 既に targetLevel 以上のレベルであれば何もしない
        if (targetLevel <= this.level) {
            return;
        }
        while (this.level < targetLevel) {
            this.level++;
            this.MaxHealth += 100;
            this.attackPower += 20;

            System.out.println("ユニットがレベル " + this.level
                    + " に上がりました！ HP: " + this.MaxHealth
                    + ", 攻撃力: " + this.attackPower);
        }
    }

    // 与えられた座標が攻撃範囲内か判定
    private boolean canAttack(double targetX, double targetY) {
        double dx = targetX - this.x;
        double dy = targetY - this.y;
        return dx * dx + dy * dy <= attackRange * attackRange;
    }

    // 最も近い敵ユニットを探す
    private Unit findTargetUnit(Game game) {
        Unit closestUnit = null;
        double closestDistance = Double.MAX_VALUE;

        for (Unit unit : game.getUnits()) {
            // ユニットがアクティブかつ敵ユニットの場合
            if (unit.isActive() && unit.getOwner() != this.getOwner()) {
                double distance = Math.hypot(unit.getX() - this.x, unit.getY() - this.y);
                if (distance < closestDistance) {
                    closestUnit = unit;
                    closestDistance = distance;
                }
            }
        }
        return closestUnit;
    }

    // 最も近い敵の建物を探す
    private Building findTargetBuilding(Game game) {
        Building closestBuilding = null;
        double closestDistance = Double.MAX_VALUE;

        for (Building building : game.getBuildings()) {
            // 建物がアクティブかつ敵の建物の場合
            if (building.isActive() && building.getOwner() != this.getOwner()) {
                double distance = Math.hypot(building.getX() - this.x, building.getY() - this.y);
                if (distance < closestDistance) {
                    closestBuilding = building;
                    closestDistance = distance;
                }
            }
        }
        return closestBuilding;
    }

    // 最も近い敵はユニットか建物か城かの判定（ユニット:1、建物:2、城:0）
    private int closestObject(Unit unit, Building building, Castle castle) {
        if (unit == null && building == null) {
            return 0;
        }

        double unitX = (unit == null) ? Double.MAX_VALUE : unit.getX() - this.x;
        double unitY = (unit == null) ? Double.MAX_VALUE : unit.getY() - this.y;
        double buildingX = (building == null) ? Double.MAX_VALUE : building.getX() - this.x;
        double buildingY = (building == null) ? Double.MAX_VALUE : building.getY() - this.y;
        double castleX = castle.getX() - this.x;
        double castleY = castle.getY() - this.y;

        if (castleX * castleX + castleY * castleY < unitX * unitX + unitY * unitY) {
            if (castleX * castleX + castleY * castleY < buildingX * buildingX + buildingY * buildingY) {
                return 0;
            } else {
                return 2;
            }
        } else {
            if (unitX * unitX + unitY * unitY < buildingX * buildingX + buildingY * buildingY) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    @Override
    public void update(double deltaTime, Game game) {
        timeSinceLastAttack += deltaTime;

        Player owner = game.getUnitOwner(this);
        Player opponent = game.getOpponent(owner);

        // 死亡時の処理
        if (isDead())
            active = false;

        // 常に最寄りの敵とユニットを探す
        // 現在のターゲットが非アクティブまたは攻撃範囲外の場合、再ターゲット
        if (c == 0 && canAttack(opponent.getCastle().getX(), opponent.getCastle().getY())) {
        } else if (c == 1 && targetUnit.isActive() && canAttack(targetUnit.getX(), targetUnit.getY())) {
        } else if (c == 2 && targetBuilding.isActive() && canAttack(targetBuilding.getX(), targetBuilding.getY())) {
        } else {
            // 最寄りの敵ユニットと敵建物を探す
            targetUnit = findTargetUnit(game);
            targetBuilding = findTargetBuilding(game);
            c = closestObject(targetUnit, targetBuilding, opponent.getCastle()); // 最も近い敵が、ユニット:c=1、建物:c=2、城:c=0）
        }

        // 最も近い敵が、ユニット:c=1、建物:c=2、城:c=0）
        if (c == 0) {
            targetX = opponent.getCastle().getX();
            targetY = opponent.getCastle().getY();
        } else if (c == 1) {
            targetX = targetUnit.getX();
            targetY = targetUnit.getY();
        } else {
            targetX = targetBuilding.getX();
            targetY = targetBuilding.getY();
        }

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
        } else {
            if (timeSinceLastAttack >= attackCooldown) {
                // プロジェクタイルを発射
                Projectile p = new Projectile(x, y, 300, attackPower, 10, Color.MAGENTA);
                if (c == 0) {
                    p.setTargetCastle(opponent.getCastle());
                } else if (c == 1) {
                    p.setTargetUnit(targetUnit);
                } else {
                    p.setTargetBuilding(targetBuilding);
                }
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
        g.drawImage(image, (int) x - 15, (int) y - 10, 24, 24, null);

        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int) x - 10, (int) y - 13, 20, 4);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int) ((health / MaxHealth) * 20);
        g.fillRect((int) x - 10, (int) y - 13, hpBarWidth, 4);
    }
}
