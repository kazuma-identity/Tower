import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.GridLayout;
import java.awt.Color;

public class SelectionPanel extends JPanel {
    private GamePanel gamePanel;
    private JButton lastSelectedButton;
    private final Color defaultColor = new JButton().getBackground(); // デフォルトの背景色
    private final Color highlightColor = Color.YELLOW; // ハイライト時の背景色

    public SelectionPanel(GamePanel panel) {
        this.gamePanel = panel;
        setLayout(new GridLayout(2, 6)); // 2行×6列のレイアウト

        // 建物ボタンの作成と追加
        add(createBuildingButton("資源設備", "BuildResource", "resourcebuilding.png", 100));
        add(createBuildingButton("防衛設備", "BuildDefense", "defensebuilding.png", 150));

        // ユニットボタンの作成と追加
        add(createUnitButton("攻城ユニット", "DeploySiege", "Playersiegeunit.png", 75));
        add(createUnitButton("Mageユニット", "DeployMage", "Playermageunit.png", 100));
        add(createUnitButton("Archerユニット", "DeployArcher", "Playerarcherunit.png", 50));

        // レベルアップボタンの作成と追加
        add(createLevelUpButton("資源設備レベルアップ", "LevelUpResourceBuilding", 100));
        add(createLevelUpButton("防衛設備レベルアップ", "LevelUpDefenseBuilding", 100));
        add(createLevelUpButton("攻城レベルアップ", "LevelUpSiege", 100));
        add(createLevelUpButton("Mageレベルアップ", "LevelUpMage", 100));
        add(createLevelUpButton("Archerレベルアップ", "LevelUpArcher", 100));
    }

    private JButton createBuildingButton(String text, String actionCommand, String imagePath, int cost) {
        JButton button = new JButton("<html>" + text + "<br>必要資源: " + cost + "</html>");
        button.setActionCommand(actionCommand);
        button.addActionListener(e -> {
            gamePanel.setSelectedAction(actionCommand);
            highlightButton(button);
        });

        // 画像の読み込みとリサイズ
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image resizedImage = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        // アイコンの設定
        button.setIcon(resizedIcon);

        // アイコンとテキストの配置
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.BOTTOM);

        return button;
    }

    private JButton createUnitButton(String text, String actionCommand, String imagePath, int cost) {
        JButton button = new JButton("<html>" + text + "<br>必要資源: " + cost + "</html>");
        button.setActionCommand(actionCommand);
        button.addActionListener(e -> {
            gamePanel.setSelectedAction(actionCommand);
            highlightButton(button);
        });

        // 画像の読み込みとリサイズ
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image resizedImage = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH); // 幅64, 高さ64にリサイズ
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        // アイコンの設定
        button.setIcon(resizedIcon);

        // アイコンとテキストの配置
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.BOTTOM);

        return button;
    }

    private JButton createLevelUpButton(String text, String actionCommand, int cost) {
        JButton button = new JButton("<html>" + text + "<br>必要資源: " + cost + "</html>");
        button.setActionCommand(actionCommand);
        button.addActionListener(e -> {
            gamePanel.levelUpByType(actionCommand, cost);
            highlightButton(button);
        });
        return button;
    }

    private void highlightButton(JButton button) {
        if (lastSelectedButton != null) {
            lastSelectedButton.setBackground(defaultColor); // 前回のボタンの背景色を元に戻す
        }
        button.setBackground(highlightColor); // 新しいボタンをハイライト
        lastSelectedButton = button; // 直近のボタンを更新
    }
}
