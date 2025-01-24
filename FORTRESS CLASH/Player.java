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

    private int archerLevel = 1;
    private int mageLevel = 1;
    private int siegeLevel = 1;
    private int resourceBuildingLevel = 1;
    private int defenseBuildingLevel = 1;

    // ゲッターとセッター
    public int getArcherLevel() {
        return archerLevel;
    }

    public int getMageLevel() {
        return mageLevel;
    }

    public int getSiegeLevel() {
        return siegeLevel;
    }

    public int getResourceBuildingLevel() {
        return resourceBuildingLevel;
    }

    public int getDefenseBuildingLevel() {
        return defenseBuildingLevel;
    }

    public void setArcherLevel(int level) {
        archerLevel = level;
    }

    public void setMageLevel(int level) {
        mageLevel = level;
    }

    public void setSiegeLevel(int level) {
        siegeLevel = level;
    }

    public void setResourceBuildingLevel(int level) {
        resourceBuildingLevel = level;
    }

    public void setDefenseBuildingLevel(int level) {
        defenseBuildingLevel = level;
    }

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

    // 指定されたタイプのユニットをすべてレベルアップ
    public boolean levelUpUnits(UnitType type) {
        boolean leveledUp = false;
        int maxLevel = 0;
        for (Unit unit : units) {
            if (unit.getType() == type) {
                int nextLevel = unit.getLevel() + 1;
                unit.levelUp(nextLevel);
                maxLevel = Math.max(maxLevel, nextLevel);
                leveledUp = true;
            }
        }
        // プレイヤーのレベルを更新
        switch (type) {
            case ARCHER:
                archerLevel = maxLevel;
                break;
            case MAGE:
                mageLevel = maxLevel;
                break;
            case SIEGE:
                siegeLevel = maxLevel;
                break;
        }
        return leveledUp;
    }

    public boolean levelUpBuildings(BuildingType type) {
        boolean leveledUp = false;
        int maxLevel = 0;

        for (Building building : buildings) {
            if (building.getType() == type) {
                int nextLevel = building.getLevel() + 1;
                building.levelUp(nextLevel);
                maxLevel = Math.max(maxLevel, nextLevel);
                leveledUp = true;
            }
        }

        // 現在の建物レベルを更新
        switch (type) {
            case RESOURCE : resourceBuildingLevel = maxLevel;
            case DEFENSE : defenseBuildingLevel = maxLevel;
        }

        return leveledUp;
    }

}
