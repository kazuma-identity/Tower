import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel {
    private Game game;
    private String selectedAction = null; // "BuildResource", "BuildDefense", "DeploySiege", "DeployMage"
    private Timer gameTimer;
    protected int size = 50; // 防衛設備、資源設備のサイズ

    public GamePanel() {
        setBackground(new Color(50,50,50));
        setLayout(new BorderLayout());

        // SelectionPanelの追加
        SelectionPanel selectionPanel = new SelectionPanel(this);
        add(selectionPanel, BorderLayout.SOUTH);

        // マウスリスナーの追加
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedAction == null)
                    return;

                // 建物の位置を綺麗にする
                double x = (e.getX() / size) * size + size / 2;
                double y = (e.getY() / size) * size + size / 2;

                // この操作が誰によるものか
                Player currentPlayer = game.getPlayer();
                if (currentPlayer == null)
                    return;

                // 配置場所が自陣外だった場合
                if (!isWithinTerritory(x, y, currentPlayer)) {
                    JOptionPane.showMessageDialog(null, "自分の陣地内にのみ建物やユニットを配置できます。");
                    return;
                }

                // 操作内容を判別（ユニット）
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

                // 城の上に建物が重ならないか
                double castleX = currentPlayer.getCastle().getX();
                double castleY = currentPlayer.getCastle().getY();
                if(castleX - x > -50 && castleX - x < 50 && castleY - y > -50 && castleY - y < 50) {  // 50:城のサイズの半分
                    return;
                }

                // 操作内容を判別（建物）
                if (selectedAction.equals("BuildResource")) {
                    buildBuilding(BuildingType.RESOURCE, x, y);
                } else if (selectedAction.equals("BuildDefense")) {
                    buildBuilding(BuildingType.DEFENSE, x, y);
                }
            }
        });

        // ゲームループの設定（60FPS）
        gameTimer = new Timer(16, new ActionListener() { // 約60FPS（16msごとにタイマー発火）
            // 抽象クラスであるActionListenerの匿名クラスを定義
            private long lastTime = System.nanoTime();

            @Override
            public void actionPerformed(ActionEvent e) {
                long currentTime = System.nanoTime();
                double deltaTime = (currentTime - lastTime) / 1_000_000_000.0; // 秒単位
                lastTime = currentTime;
                game.update(deltaTime); // Game.class内のupdateメソッドを呼び出す
                repaint(); // このクラスにあるpaintComponentメソッドを呼び出す
            }
        });

        // 上で定義したGameTimerを起動
        gameTimer.start();
    }

    // ゲッターとセッター
    public void setGame(Game game) { this.game = game; }


    // 与えられた座標が自陣内か判定
    private boolean isWithinTerritory(double x, double y, Player player) {
        if (player.getCastle().getX() < 400) { // 左側プレイヤー
            return x <= 400;
        } else { // 右側プレイヤー
            return x >= 600;
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

    // 与えられた座標に与えられた種類の建物を配置
    private void buildBuilding(BuildingType type, double x, double y) {
        Player player = game.getPlayer();
        if (player == null)
            return;

        int cost = 100;  // 資源設備、防衛設備のコスト：100
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
            repaint(); // paintComponentメソッドを呼び出す
        } else { // コスト不足の場合
            JOptionPane.showMessageDialog(null, "資源が不足しています。");
        }
    }

    // 与えられた座標に与えられた種類のユニットを配置
    private void deployUnit(UnitType type, double x, double y) {
        Player player = game.getPlayer();
        if (player == null)
            return;

        int cost = (type == UnitType.ARCHER) ? 50 : 100; // アーチャーだけコスト50、それ以外はコスト100
        if (player.getResources() >= cost) {
            player.spendResources(cost);
            Unit unit;
            if (type == UnitType.MAGE) {
                unit = new MageUnit(x, y, player);
            } else if(type == UnitType.SIEGE) {
                unit = new SiegeUnit(x, y, player);
            } else {
                unit = new ArcherUnit(x, y, player);
            }
            player.addUnit(unit);
            game.addUnit(unit);
            repaint();
        } else {
            JOptionPane.showMessageDialog(null, "資源が不足しています。");
        }
    }

    public void setSelectedAction(String action) {
        this.selectedAction = action;
    }

    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (game != null) {
            // 各陣地の背景の描画
            g.setColor(new Color(200, 200, 200));
            g.fillRect(0, 0, 400, 600);
            g.fillRect(600, 0, 400, 600);

            // 城の描画
            for (Player p : game.getPlayers()) {
                if (p.getName().equals("Bot")) {
                    p.getCastle().draw(g, Color.RED);
                } else {
                    p.getCastle().draw(g, Color.BLUE);
                }
            }

            // 建物の描画
            for (Player p : game.getPlayers()) {
                for (Building building : p.getBuildings()) {
                    building.draw(g);
                }
            }

            // ユニットの描画
            for (Player p : game.getPlayers()) {
                for (Unit unit : p.getUnits()) {
                    unit.draw(g);
                }
            }

            // プロジェクタイルの描画
            for (Projectile p : game.getProjectiles()) {
                p.draw(g);
            }

            // 資源のプログレスバーの表示
            Player player = game.getPlayer();
            Player bot = game.getBot();

            double MaxResources = 500; // 資源の最大値（スケーリング用）
            // プレイヤーの資源表示
            g.setColor(Color.WHITE);
            g.drawString(player.getName() + " 資源: " + (int)player.getResources(), 10, 20);
            g.setColor(Color.GREEN);
            int playerResourceWidth = (int)(((double)player.getResources() / MaxResources) * 200) ;
            g.fillRect(100, 10, playerResourceWidth, 10);
            g.setColor(Color.GRAY);
            g.drawRect(100, 10, 200, 10);

            // ボットの資源表示
            g.setColor(Color.WHITE);
            g.drawString(bot.getName() + " 資源: " + (int)bot.getResources(), 910, 20);
            g.setColor(Color.GREEN);
            int botResourceWidth = (int)(((double)bot.getResources() / MaxResources) * 200);
            g.fillRect(700, 10, botResourceWidth, 10);
            g.setColor(Color.GRAY);
            g.drawRect(700, 10, 200, 10);
        }
    }

    public void showGameOver(String winner) {
        JOptionPane.showMessageDialog(this, "ゲーム終了！勝者: " + winner);
        System.exit(0);
    }
}
