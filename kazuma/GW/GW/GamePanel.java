import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.awt.Image;

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
  private Image siegeunitImage; // 攻城ユニットの画像
  private Image defenseunitImage; // 防衛ユニットの画像


  public GamePanel() {
    setBackground(Color.BLACK);
    setLayout(new BorderLayout());

    // 画像の読み込み
    castleImage = new ImageIcon("castle.png").getImage();
    defensebuildingImage = new ImageIcon("defensebuilding.png").getImage();
    resourcebuildingImage = new ImageIcon("resourcebuilding.png").getImage();
    siegeunitImage = new ImageIcon("siege.png").getImage();
    defenseunitImage = new ImageIcon("defenseunit.png").getImage();

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
        if(castleX - x > -50 && castleX - x < 50 && castleY - y > -50 && castleY - y < 50) {  // 50:城のサイズの半分
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
    for(Building building : player.getBuildings()) {
      if(building.getX()==x && building.getY()==y) {
        return false;
      }
    }
    return true;
  }

  private void buildBuilding(BuildingType type, double x, double y) {
    Player player = game.getPlayer();
    if (player == null)
      return;

    int cost = 100;
    if (player.getResources() >= cost) {
      player.spendResources(cost);
      Building building;
      if (type == BuildingType.RESOURCE) {
        building = new ResourceBuilding(x, y, player);
      } else {
        building = new DefenseBuilding(x, y, player);
      }
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

    int cost = (type == UnitType.ARCHER) ? 50 : 100;
    if (player.getResources() >= cost) {
      player.spendResources(cost);
      Unit unit;
      if (type == UnitType.MAGE) {
        unit = new MageUnit(x, y, player, 1);
      } else if(type == UnitType.SIEGE) {
        unit = new SiegeUnit(x, y, player, 1);
      } else {
        unit = new ArcherUnit(x, y, player, 1);
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
      g.fillRect(0, 0, 400, 600);
      g.fillRect(600, 0, 400, 600);

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
            unit.draw(g, defenseunitImage);
          } else {
            unit.draw(g, siegeunitImage);
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

      //プレイヤーの資源
      g.setColor(Color.WHITE);
      g.drawString(player.getName() + " 資源: " + (int)player.getResources(), 10, 20);
      g.setColor(Color.GREEN);
      int playerResourceWidth = (int)(((double)player.getResources() / MaxResources) * 200) ;
      g.fillRect(100, 10, playerResourceWidth, 10);
      g.setColor(Color.GRAY);
      g.drawRect(100, 10, 200, 10);

      //Botの資源
      g.setColor(Color.WHITE);
      g.drawString(bot.getName() + " 資源: " + (int)bot.getResources(), 910, 20);
      g.setColor(Color.GREEN);
      int botResourceWidth = (int)(((double)bot.getResources() / MaxResources) * 200);
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
        int messageY = getHeight() - 50; // 画面下部から少し上のY座標
            
        g.drawString(temporaryMessage, messageX, messageY);
      }      
    }
  }

  public void showGameOver(String winner) {
    JOptionPane.showMessageDialog(this, "ゲーム終了！勝者: " + winner);
    System.exit(0);
  }
}
