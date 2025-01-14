import javax.swing.JPanel;
import java.awt.GridLayout;

public class SelectionPanel extends JPanel {
  private GamePanel gamePanel;

  public SelectionPanel(GamePanel panel) {
    this.gamePanel = panel;
    setLayout(new GridLayout(1, 5));

    // 建物選択ボタン
    BuildingButton buildResourceBtn = new BuildingButton("資源設備", "BuildResource", gamePanel);
    BuildingButton buildDefenseBtn = new BuildingButton("防衛設備", "BuildDefense", gamePanel);
    add(buildResourceBtn);
    add(buildDefenseBtn);

    // ユニット選択ボタン
    UnitButton deploySiegeBtn = new UnitButton("攻城ユニット", "DeploySiege", gamePanel);
    UnitButton deployMageBtn = new UnitButton("Mageユニット", "DeployMage", gamePanel);
    UnitButton deployArcherBtn = new UnitButton("Archerユニット", "DeployArcher", gamePanel);
    add(deploySiegeBtn);
    add(deployMageBtn);
    add(deployArcherBtn);
  }
}
