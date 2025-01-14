package game.UIComponents;

import client.*;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class SelectionPanel extends JPanel {
    private GamePanel gamePanel;

    public SelectionPanel(GamePanel panel) {
        this.gamePanel = panel;
        setLayout(new GridLayout(1, 4));

        // 建物選択ボタン
        JButton buildResourceBtn = new JButton("資源設備");
        buildResourceBtn.addActionListener(e -> gamePanel.setSelectedAction("BuildResource"));
        JButton buildDefenseBtn = new JButton("防衛設備");
        buildDefenseBtn.addActionListener(e -> gamePanel.setSelectedAction("BuildDefense"));

        // ユニット選択ボタン
        JButton deploySiegeBtn = new JButton("攻城ユニット");
        deploySiegeBtn.addActionListener(e -> gamePanel.setSelectedAction("DeploySiege"));
        JButton deployDefenseBtn = new JButton("防衛ユニット");
        deployDefenseBtn.addActionListener(e -> gamePanel.setSelectedAction("DeployDefense"));

        add(buildResourceBtn);
        add(buildDefenseBtn);
        add(deploySiegeBtn);
        add(deployDefenseBtn);
    }
}