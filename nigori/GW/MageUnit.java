import java.awt.Graphics;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Image;

// MageUnit ターゲットは最寄りの敵オブジェクト、中距離攻撃
public class MageUnit extends Unit {
    
    private double attackRange; // 攻撃範囲
    private double attackPower; // 攻撃力
    private double attackCooldown;
    private double timeSinceLastAttack;
    private double targetX, targetY;
    private int size = 30; // ユニットのサイズ
    private Unit targetUnit;
    private Building targetBuilding;

    public MageUnit(double x, double y, Player owner) {
        super(x, y, 50.0, 100, 100.0, UnitType.MAGE, owner);
        this.attackRange = 120;
        this.attackPower = 50;
        this.attackCooldown = 1.0; // 攻撃間隔（秒）
        this.timeSinceLastAttack = 0;
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

        for (Building building: game.getBuildings()) {
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
        if(unit == null && building == null) { return 0; }

        double unitX = (unit==null) ? Double.MAX_VALUE : unit.getX() - this.x;
        double unitY = (unit==null) ? Double.MAX_VALUE : unit.getY() - this.y;
        double buildingX = (building==null) ? Double.MAX_VALUE : building.getX() - this.x;
        double buildingY = (building==null) ? Double.MAX_VALUE : building.getY() - this.y;
        double castleX = castle.getX() - this.x;
        double castleY = castle.getY() - this.y;

        if(castleX*castleX + castleY*castleY < unitX*unitX + unitY*unitY) {
            if(castleX*castleX + castleY*castleY < buildingX*buildingX + buildingY*buildingY) {
                return 0;
            } else {
                return 2;
            }
        } else {
            if(unitX*unitX + unitY*unitY < buildingX*buildingX + buildingY*buildingY) {
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
        if(isDead())
            active = false;

        // 常に最寄りの敵とユニットを探す
        targetUnit = findTargetUnit(game);
        targetBuilding = findTargetBuilding(game);

        // 最も近い敵が、ユニット:c=1、建物:c=2、城:c=0）
        int c = closestObject(targetUnit, targetBuilding, opponent.getCastle());
        if(c == 0) {
            targetX = opponent.getCastle().getX();
            targetY = opponent.getCastle().getY();
        } else if(c == 1) {
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
        } else  {
            if (timeSinceLastAttack >= attackCooldown) {
                // プロジェクタイルを発射
                Projectile p = new Projectile(x, y, 300, attackPower, 10, Color.MAGENTA);
                if(c == 0) {
                    p.setTargetCastle(opponent.getCastle());
                } else if(c == 1) {
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
        g.drawImage(image, (int) x-15, (int) y-10, 24, 24, null);

        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int) x - 10, (int) y - 13, 20, 4);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int) ((health / MaxHealth) * 20);
        g.fillRect((int) x - 10, (int) y - 13, hpBarWidth, 4);
    }
}
