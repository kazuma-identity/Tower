import java.util.List;
import java.util.ArrayList;

public class Player {
    private String name;
    private double resources;
    private Castle castle;
    private List<Building> buildings;
    private List<Unit> units;
    private double MaxResources = 500.0; // 資源の最大値

    public Player(String name, double initialResources) {
        this.name = name;
        this.resources = initialResources;
        buildings = new ArrayList<>();
        units = new ArrayList<>();
    }

    // ゲッターとセッター
    public String getName() {
        return name;
    }

    public double getResources() {
        return resources;
    }

    public Castle getCastle() {
        return castle;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setCastle(Castle castle) {
        this.castle = castle;
    }

    public void addResources(double amount) {
        resources += amount;
        if (resources > MaxResources) {
            resources = MaxResources;
        }
    }

    public boolean spendResources(double amount) {
        if (resources >= amount) {
            resources -= amount;
            return true;
        }
        return false;
    }

    public void addBuilding(Building building) {
        buildings.add(building);
    }

    public void removeBuilding(Building building) {
        buildings.remove(building);
    }

    public void addUnit(Unit unit) {
        units.add(unit);
    }

    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    // ボットのためのメソッド
    // public void upgradeBuilding(Building building) {
    // if (building != null) {
    // building.levelUp();
    // }
    // }

}
