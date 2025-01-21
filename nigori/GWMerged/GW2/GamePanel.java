import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
  private Game game;
  private String selectedAction = null; // "BuildResource", "BuildDefense", "DeploySiege", "DeployDefense"
  private Timer gameTimer;
  protected int size = 50; // 防衛設備、資源設備のサイズ
  private String temporaryMessage = null; // 一時的なメッセージ
  private int temporaryMessageDuration = 0; // 表示時間をカウントダウンする変数
  private Timer messageTimer; // メッセージのカウントダウン用タイマー
  private Image castleImage; // 城の画像
  private Image defensebuildingImage; // 攻城ユニットの画像
  private Image resourcebuildingImage; // 資源設備の画像
  private Image PlayersiegeunitImage; // 攻城ユニットの画像
  private Image BotsiegeunitImage; // 攻城ユニットの画像
  private Image PlayerArcherunitImage; // Archerユニットの画像
  private Image BotArcherunitImage; // Archerユニットの画像
  private Image PlayerMageunitImage; // Mageユニットの画像
  private Image PlayerMageunitLV2Image; // MageLV.2ユニットの画像
  private Image PlayerMageunitLV3Image; // MageLV.3ユニットの画像
  private Image BotMageunitImage; // Mageユニットの画像
  private Image TerritoryImage; // 領土の画像

  public GamePanel() {
    setBackground(Color.BLACK);
    setLayout(new BorderLayout());

    // 画像の読み込み
    castleImage = new ImageIcon("castle.png").getImage();
    defensebuildingImage = new ImageIcon("defensebuilding.png").getImage();
    resourcebuildingImage = new ImageIcon("resourcebuilding.png").getImage();
    PlayersiegeunitImage = new ImageIcon("Playersiegeunit.png").getImage();
    BotsiegeunitImage = new ImageIcon("Botsiegeunit.png").getImage();
    PlayerArcherunitImage = new ImageIcon("Playerarcherunit.png").getImage();
    BotArcherunitImage = new ImageIcon("Botarcherunit.png").getImage();
    PlayerMageunitImage = new ImageIcon("Playermageunit.png").getImage();
    PlayerMageunitLV2Image = new ImageIcon("PlayermageunitLV2.png").getImage();
    PlayerMageunitLV3Image = new ImageIcon("PlayermageunitLV3.png").getImage();
    BotMageunitImage = new ImageIcon("Botmageunit.png").getImage();
    TerritoryImage = new ImageIcon("Territory.png").getImage();

    // SelectionPanelの追加
    SelectionPanel selectionPanel = new SelectionPanel(this);
    add(selectionPanel, BorderLayout.SOUTH);

    // マウスリスナーの追加
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (selectedAction == null)
          return;

        double x = (e.getX() / size) * size + size / 2;
        double y = (e.getY() / size) * size + size / 2;

        Player currentPlayer = game.getPlayer();
        if (currentPlayer == null)
          return;

        if (!isWithinTerritory(x, y, currentPlayer)) {
          showTemporaryMessage("自分の陣地内にのみ建物やユニットを配置できます。", 30);
          return;
        }

        if (selectedAction.equals("DeploySiege")) {
          deployUnit(UnitType.SIEGE, e.getX(), e.getY());
        } else if (selectedAction.equals("DeployMage")) {
          deployUnit(UnitType.MAGE, e.getX(), e.getY());
        } else if (selectedAction.equals("DeployArcher")) {
          deployUnit(UnitType.ARCHER, e.getX(), e.getY());
        }

        // 配置するものが建物の場合、他と重なってはいけない
        if (!isThereNewPlace(x, y, currentPlayer)) {
          return;
        }

        double castleX = currentPlayer.getCastle().getX();
        double castleY = currentPlayer.getCastle().getY();
        if (castleX - x > -50 && castleX - x < 50 && castleY - y > -50 && castleY - y < 50) { // 50:城のサイズの半分
          return;
        }

        if (selectedAction.equals("BuildResource")) {
          buildBuilding(BuildingType.RESOURCE, x, y);
        } else if (selectedAction.equals("BuildDefense")) {
          buildBuilding(BuildingType.DEFENSE, x, y);
        }
      }
    });
    messageTimer = new Timer(100, e -> {
      if (temporaryMessageDuration > 0) {
        temporaryMessageDuration--;
        repaint();
      } else {
        temporaryMessage = null;
        messageTimer.stop();
      }
    });
    // ゲームループの設定（60FPS）
    gameTimer = new Timer(16, new ActionListener() { // 約60FPS
      private long lastTime = System.nanoTime();

      @Override
      public void actionPerformed(ActionEvent e) {
        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastTime) / 1_000_000_000.0; // 秒単位
        lastTime = currentTime;
        game.update(deltaTime);
        repaint();
      }
    });
    gameTimer.start();
  }

  public void setGame(Game game) {
    this.game = game;
  }

  private boolean isWithinTerritory(double x, double y, Player player) {
    if (player.getCastle().getX() < 400) { // 左側プレイヤー
      return x <= 400;
    } else { // 右側プレイヤー
      return x >= 400;
    }
  }

  // 与えられた座標に建物がないか判定
  private boolean isThereNewPlace(double x, double y, Player player) {
    for (Building building : player.getBuildings()) {
      if (building.getX() == x && building.getY() == y) {
        return false;
      }
    }
    return true;
  }

  private void buildBuilding(BuildingType type, double x, double y) {
    Player player = game.getPlayer();
    if (player == null)
      return;

    int cost = 0;
    if (type == BuildingType.RESOURCE) {
      cost = 100;
    } else if (type == BuildingType.DEFENSE) {
      cost = 150;
    }
    if (player.getResources() >= cost) {
      player.spendResources(cost);
      Building building;
      int level = 1; // デフォルトレベル
      switch (type) {
        case RESOURCE:
          level = player.getResourceBuildingLevel();
          break;
        case DEFENSE:
          level = player.getDefenseBuildingLevel();
          break;
      }
      if (type == BuildingType.RESOURCE) {
        building = new ResourceBuilding(x, y, player);
      } else {
        building = new DefenseBuilding(x, y, player);
      }
      building.levelUp(level); // 初期レベルを設定
      player.addBuilding(building);
      game.addBuilding(building);
      repaint();
    } else {
      showTemporaryMessage("資源が不足しています。", 30);
    }
  }

  private void deployUnit(UnitType type, double x, double y) {
    Player player = game.getPlayer();
    if (player == null)
      return;

    int cost = 0;
    if (type == UnitType.ARCHER) {
      cost = 50;
    } else if (type == UnitType.MAGE) {
      cost = 100;
    } else if (type == UnitType.SIEGE) {
      cost = 75;
    }
    if (player.getResources() >= cost) {
      player.spendResources(cost);
      Unit unit;
      int level = 1; // デフォルトレベル
      switch (type) {
        case ARCHER:
          level = player.getArcherLevel();
          break;
        case MAGE:
          level = player.getMageLevel();
          break;
        case SIEGE:
          level = player.getSiegeLevel();
          break;
      }
      if (type == UnitType.MAGE) {
        unit = new MageUnit(x, y, player, level);
      } else if (type == UnitType.SIEGE) {
        unit = new SiegeUnit(x, y, player, level);
      } else {
        unit = new ArcherUnit(x, y, player, level);
      }
      player.addUnit(unit);
      game.addUnit(unit);
      repaint();
    } else {
      showTemporaryMessage("資源が不足しています。", 30);
    }
  }

  public void setSelectedAction(String action) {
    this.selectedAction = action;
    repaint();
  }

  public void showTemporaryMessage(String message, int duration) {
    this.temporaryMessage = message;
    this.temporaryMessageDuration = duration;
    messageTimer.start();
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (game != null) {
      // 各陣地の背景の描画
      g.setColor(new Color(200, 200, 200));
      g.drawImage(TerritoryImage, 0, 0, 1000, 600, null);

      for (Player p : game.getPlayers()) {
        if (p.getName().equals("Bot")) {
          p.getCastle().draw(g, Color.RED, castleImage);
        } else {
          p.getCastle().draw(g, Color.BLUE, castleImage);
        }
      }

      for (Player p : game.getPlayers()) {
        for (Building building : p.getBuildings()) {
          if (building instanceof ResourceBuilding) {
            building.draw(g, resourcebuildingImage);
          } else {
            building.draw(g, defensebuildingImage);
          }
        }
      }

      for (Player p : game.getPlayers()) {
        for (Unit unit : p.getUnits()) {
          if (unit instanceof ArcherUnit) {
            if (p.getName().equals("Bot")) {
              unit.draw(g, BotArcherunitImage);
            } else {
              unit.draw(g, PlayerArcherunitImage);
            }
          } else if (unit instanceof SiegeUnit) {
            if (p.getName().equals("Bot")) {
              unit.draw(g, BotsiegeunitImage);
            } else {
              unit.draw(g, PlayersiegeunitImage);
            }
          } else if (unit instanceof MageUnit) {
            if (p.getName().equals("Bot")) {
              unit.draw(g, BotMageunitImage);
            } else {
              if (unit.getLevel() == 1) {
                unit.draw(g, PlayerMageunitImage);
              } else if (unit.getLevel() == 2) {
                unit.draw(g, PlayerMageunitLV2Image);
              } else {
                unit.draw(g, PlayerMageunitLV3Image);
              }
            }
          }
        }
      }

      for (Projectile p : game.getProjectiles()) {
        p.draw(g);
      }

      // 資源のプログレスバーの表示
      Player player = game.getPlayer();
      Player bot = game.getBot();
      double MaxResources = 500; // 資源の最大値（スケーリング用）

      // プレイヤーの資源
      g.setColor(Color.WHITE);
      g.drawString(player.getName() + " 資源: " + (int) player.getResources(), 10, 20);
      g.setColor(Color.GREEN);
      int playerResourceWidth = (int) (((double) player.getResources() / MaxResources) * 200);
      g.fillRect(100, 10, playerResourceWidth, 10);
      g.setColor(Color.GRAY);
      g.drawRect(100, 10, 200, 10);

      // Botの資源
      g.setColor(Color.WHITE);
      g.drawString(bot.getName() + " 資源: " + (int) bot.getResources(), 910, 20);
      g.setColor(Color.GREEN);
      int botResourceWidth = (int) (((double) bot.getResources() / MaxResources) * 200);
      g.fillRect(700, 10, botResourceWidth, 10);
      g.setColor(Color.GRAY);
      g.drawRect(700, 10, 200, 10);

      // 一時的なメッセージの描画
      if (temporaryMessage != null && temporaryMessageDuration > 0) {
        g.setColor(Color.RED);
        g.setFont(g.getFont().deriveFont(18f)); // フォントサイズを調整

        // メッセージを中央に配置
        int messageWidth = g.getFontMetrics().stringWidth(temporaryMessage);
        int messageX = (getWidth() - messageWidth) / 2; // 画面中央のX座標
        int messageY = getHeight() / 2 - 170; // 少し上のY座標

        g.drawString(temporaryMessage, messageX, messageY);
      }
    }
  }

  public void levelUpByType(String actionCommand, int cost) {
    Player player = game.getPlayer();
    if (player == null)
      return;

    // プレイヤーのリソース確認
    if (player.getResources() < cost) {
      showTemporaryMessage("資源が不足しています。", 30);
      return;
    }

    boolean leveledUp = false;

    switch (actionCommand) {
      case "LevelUpArcher":
        leveledUp = player.levelUpUnits(UnitType.ARCHER);
        break;
      case "LevelUpMage":
        leveledUp = player.levelUpUnits(UnitType.MAGE);
        break;
      case "LevelUpSiege":
        leveledUp = player.levelUpUnits(UnitType.SIEGE);
        break;
      case "LevelUpResourceBuilding":
        leveledUp = player.levelUpBuildings(BuildingType.RESOURCE);
        break;
      case "LevelUpDefenseBuilding":
        leveledUp = player.levelUpBuildings(BuildingType.DEFENSE);
        break;
    }

    if (leveledUp) {
      player.spendResources(cost);
      showTemporaryMessage(actionCommand, 30);
    } else {
      showTemporaryMessage("対象がありません。", 30);
    }
  }

  public void showGameOver(String winner) {
    // (1) ゲームループやBotスレッドを停止 (必要ならここで行う)
    if (gameTimer != null) {
      gameTimer.stop();
    }

    // (2) 表示する文字を winner で切り替える
    String message;
    if ("Bot".equals(winner)) {
      message = "GAME OVER";
    } else {
      message = "VICTORY";
    }

    // (3) カスタムパネルを作って、そこにラベルを載せる
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(true);
    panel.setBackground(Color.WHITE);

    // ラベルを作る
    JLabel msgLabel = new JLabel(message, SwingConstants.CENTER);
    msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // GameMain で読み込んだカスタムフォントを取得して、サイズ調整
    Font baseFont = GameMain.customFont;
    // もし static でなくゲッターにしているなら: Font baseFont = GameMain.getCustomFont();

    if (baseFont != null) {
      msgLabel.setFont(baseFont.deriveFont(Font.BOLD, 72f));
    } else {
      // フォント読み込み失敗時はフォールバック
      msgLabel.setFont(new Font("SansSerif", Font.BOLD, 60));
    }
    msgLabel.setForeground(Color.RED);

    // ラベルをパネルに追加
    panel.add(Box.createVerticalStrut(20));
    panel.add(msgLabel);
    panel.add(Box.createVerticalStrut(20));

    // (4) JOptionPane で「リトライ」「終了」のオプションを付けて表示
    Object[] options = { "リトライ", "終了" };
    int choice = JOptionPane.showOptionDialog(
        this,
        panel, // カスタムパネルをメッセージ部に指定
        "ゲーム終了", // タイトル
        JOptionPane.YES_NO_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        options,
        null);

    if (choice == JOptionPane.YES_OPTION) {
      // リトライ
      // 古いウィンドウを閉じて新しいゲームを開始
      GameMain.frame.dispose();
      GameMain.startGame();
    } else {
      // 終了
      System.exit(0);
    }
  }

}
