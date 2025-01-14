対戦型タワーディフェンスゲームにサーバー機能を組み込み、リアルタイムで1対1の対戦を実現するための完全なバージョンを以下に示します。このバージョンでは、KryoNetライブラリを使用してクライアント・サーバーアーキテクチャを構築し、既存のゲームロジックと統合しています。

目次
プロジェクト構成
依存関係の設定
共通クラスの定義
サーバーサイドの実装
クライアントサイドの実装
既存ゲームクラスの修正
実行方法
補足説明
まとめ

1. プロジェクト構成
プロジェクトは以下のようなパッケージ構成とします：

kotlin

TowerDefenseGame/
├── common/
│   ├── messages/
│   │   ├── BuildBuilding.java
│   │   ├── DeployUnit.java
│   │   ├── GameOver.java
│   │   ├── GameState.java
│   │   ├── JoinGame.java
│   │   ├── PlayerState.java
│   │   ├── StartGame.java
│   │   ├── UpgradeBuilding.java
│   │   ├── UnitState.java
│   │   └── CastleState.java
│   ├── enums/
│   │   ├── BuildingType.java
│   │   └── UnitType.java
├── server/
│   ├── GameServer.java
│   └── GameSession.java
├── client/
│   ├── GameClient.java
│   ├── GameClientMain.java
│   └── GamePanel.java
├── game/
│   ├── Game.java
│   ├── GameObject.java
│   ├── Player.java
│   ├── Castle.java
│   ├── Building.java
│   ├── ResourceBuilding.java
│   ├── DefenseBuilding.java
│   ├── Unit.java
│   ├── DefenseUnit.java
│   ├── SiegeUnit.java
│   ├── Tower.java
│   ├── ResourceManager.java
│   ├── LevelManager.java
│   ├── UIComponents/
│   │   ├── SelectionPanel.java
│   │   ├── BuildingButton.java
│   │   └── UnitButton.java
│   └── MouseHandler.java
この構成により、共通クラス、サーバーサイドクラス、クライアントサイドクラス、およびゲームロジッククラスを明確に分離できます。

2. 依存関係の設定
このプロジェクトでは、KryoNetライブラリを使用します。MavenまたはGradleを使用して依存関係を管理することをお勧めします。以下は、Mavenを使用した場合のpom.xmlの例です。

xml
コードをコピーする
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>TowerDefenseGame</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <!-- KryoNet Dependency -->
        <dependency>
            <groupId>com.esotericsoftware</groupId>
            <artifactId>kryonet</artifactId>
            <version>2.22.0</version>
        </dependency>
        <!-- Kryo Dependency -->
        <dependency>
            <groupId>com.esotericsoftware</groupId>
            <artifactId>kryo</artifactId>
            <version>5.3.0</version>
        </dependency>
    </dependencies>
</project>
注意: MavenやGradleを使用していない場合は、KryoNetの公式リポジトリからJARファイルをダウンロードし、プロジェクトのクラスパスに追加してください。

3. 共通クラスの定義
共通クラスは、サーバーとクライアントの両方で使用されるメッセージやデータモデルを定義します。これらはcommonパッケージに配置します。

a. Enums
BuildingType.java
java
コードをコピーする
package common.enums;

public enum BuildingType {
    RESOURCE,
    DEFENSE
}
UnitType.java
java
コードをコピーする
package common.enums;

public enum UnitType {
    DEFENSE,
    SIEGE
}
b. Messages
JoinGame.java
java
コードをコピーする
package common.messages;

public class JoinGame {
    public String playerName;
}
StartGame.java
java
コードをコピーする
package common.messages;

public class StartGame {
    public String message;
}
BuildBuilding.java
java
コードをコピーする
package common.messages;

import common.enums.BuildingType;

public class BuildBuilding {
    public BuildingType buildingType;
    public double x, y;
}
UpgradeBuilding.java
java
コードをコピーする
package common.messages;

public class UpgradeBuilding {
    public int buildingId;
}
DeployUnit.java
java
コードをコピーする
package common.messages;

import common.enums.UnitType;

public class DeployUnit {
    public UnitType unitType;
    public double x, y;
}
GameState.java
java
コードをコピーする
package common.messages;

import java.util.List;

public class GameState {
    public List<PlayerState> players;
    public List<BuildingState> buildings;
    public List<UnitState> units;
}
PlayerState.java
java
コードをコピーする
package common.messages;

public class PlayerState {
    public String playerName;
    public int resources;
    public CastleState castle;
}
CastleState.java
java
コードをコピーする
package common.messages;

public class CastleState {
    public double x, y;
    public int hp;
}
BuildingState.java
java
コードをコピーする
package common.messages;

import common.enums.BuildingType;

public class BuildingState {
    public int id;
    public BuildingType type;
    public double x, y;
    public int level;
    public int hp;
}
UnitState.java
java
コードをコピーする
package common.messages;

import common.enums.UnitType;

public class UnitState {
    public int id;
    public UnitType type;
    public double x, y;
    public int hp;
}
GameOver.java
java
コードをコピーする
package common.messages;

public class GameOver {
    public String winner; // "Player" または "Opponent"
}

4. サーバーサイドの実装
サーバーは、ゲームのロジックを管理し、クライアント間の通信を仲介します。以下のクラスを実装します。

a. GameServer.java
java
コードをコピーする
package server;

import com.esotericsoftware.kryonet.*;
import common.messages.*;
import common.enums.*;
import java.io.IOException;

public class GameServer {
    private Server server;
    private GameSession gameSession;

    public GameServer() throws IOException {
        server = new Server();
        gameSession = new GameSession();

        // クラスの登録
        registerClasses(server.getKryo());

        // リスナーの追加
        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof JoinGame) {
                    JoinGame join = (JoinGame) object;
                    gameSession.addPlayer(connection, join.playerName);
                    System.out.println(join.playerName + " がゲームに参加しました。");

                    // 2人揃ったらゲーム開始
                    if (gameSession.getPlayerCount() == 2 && !gameSession.isGameStarted()) {
                        gameSession.startGame();
                        server.sendToAllTCP(new StartGame() {{
                            message = "ゲーム開始！";
                        }});
                        gameSession.broadcastGameState(server);
                    }
                } else if (object instanceof BuildBuilding) {
                    BuildBuilding build = (BuildBuilding) object;
                    gameSession.buildBuilding(connection, build.buildingType, build.x, build.y);
                    gameSession.broadcastGameState(server);
                } else if (object instanceof UpgradeBuilding) {
                    UpgradeBuilding upgrade = (UpgradeBuilding) object;
                    gameSession.upgradeBuilding(connection, upgrade.buildingId);
                    gameSession.broadcastGameState(server);
                } else if (object instanceof DeployUnit) {
                    DeployUnit deploy = (DeployUnit) object;
                    gameSession.deployUnit(connection, deploy.unitType, deploy.x, deploy.y);
                    gameSession.broadcastGameState(server);
                }
            }

            @Override
            public void disconnected(Connection connection) {
                gameSession.removePlayer(connection);
                System.out.println("プレイヤーが切断されました。");
                server.sendToAllTCP(new GameOver() {{
                    winner = "Opponent";
                }});
            }
        });

        // サーバーの起動
        server.bind(54555, 54777); // TCPポート54555、UDPポート54777
        server.start();
        System.out.println("サーバーがポート54555 (TCP) と 54777 (UDP) で起動しました。");
    }

    private void registerClasses(com.esotericsoftware.kryo.Kryo kryo) {
        kryo.register(JoinGame.class);
        kryo.register(StartGame.class);
        kryo.register(BuildBuilding.class);
        kryo.register(UpgradeBuilding.class);
        kryo.register(DeployUnit.class);
        kryo.register(GameState.class);
        kryo.register(PlayerState.class);
        kryo.register(CastleState.class);
        kryo.register(BuildingState.class);
        kryo.register(UnitState.class);
        kryo.register(GameOver.class);
        kryo.register(java.util.ArrayList.class);
    }

    public static void main(String[] args) {
        try {
            new GameServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
b. GameSession.java
java
コードをコピーする
package server;

import com.esotericsoftware.kryonet.*;
import common.messages.*;
import common.enums.*;
import java.util.*;

public class GameSession {
    private Map<Connection, PlayerState> players;
    private Map<Integer, BuildingState> buildings;
    private Map<Integer, UnitState> units;
    private int buildingIdCounter = 1;
    private int unitIdCounter = 1;
    private boolean gameStarted = false;

    public GameSession() {
        players = new HashMap<>();
        buildings = new HashMap<>();
        units = new HashMap<>();
    }

    public void addPlayer(Connection connection, String playerName) {
        if (players.containsKey(connection)) return;

        PlayerState player = new PlayerState();
        player.playerName = playerName;
        player.resources = 100;
        player.castle = new CastleState();

        if (players.size() == 0) {
            player.castle.x = 50;
            player.castle.y = 300;
        } else if (players.size() == 1) {
            player.castle.x = 750;
            player.castle.y = 300;
        }
        player.castle.hp = 1000;
        players.put(connection, player);
    }

    public void removePlayer(Connection connection) {
        players.remove(connection);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        gameStarted = true;
        // ゲームループの開始などをここに追加
        // 例: Timerを使用して定期的にゲームロジックを更新
        Timer gameLoopTimer = new Timer();
        gameLoopTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGameLogic(0.016); // 約60fps
                broadcastGameState(null); // サーバー経由で送信
            }
        }, 0, 16);
    }

    public void buildBuilding(Connection connection, BuildingType buildingType, double x, double y) {
        PlayerState player = players.get(connection);
        if (player == null) return;

        int cost = (buildingType == BuildingType.RESOURCE) ? 100 : 150;
        if (player.resources >= cost) {
            player.resources -= cost;
            BuildingState building = new BuildingState();
            building.id = buildingIdCounter++;
            building.type = buildingType;
            building.x = x;
            building.y = y;
            building.level = 1;
            building.hp = 500;
            buildings.put(building.id, building);
        }
    }

    public void upgradeBuilding(Connection connection, int buildingId) {
        BuildingState building = buildings.get(buildingId);
        if (building == null) return;

        PlayerState player = players.get(connection);
        if (player == null) return;

        int upgradeCost = 100 * building.level;
        if (player.resources >= upgradeCost) {
            player.resources -= upgradeCost;
            building.level += 1;
            building.hp += 200; // HP増加
        }
    }

    public void deployUnit(Connection connection, UnitType unitType, double x, double y) {
        PlayerState player = players.get(connection);
        if (player == null) return;

        int cost = (unitType == UnitType.DEFENSE) ? 50 : 100;
        if (player.resources >= cost) {
            player.resources -= cost;
            UnitState unit = new UnitState();
            unit.id = unitIdCounter++;
            unit.type = unitType;
            unit.x = x;
            unit.y = y;
            unit.hp = (unitType == UnitType.DEFENSE) ? 100 : 150;
            units.put(unit.id, unit);
        }
    }

    public void broadcastGameState(Server server) {
        GameState state = getGameState();
        server.sendToAllTCP(state);
    }

    public GameState getGameState() {
        GameState state = new GameState();
        state.players = new ArrayList<>(players.values());
        state.buildings = new ArrayList<>(buildings.values());
        state.units = new ArrayList<>(units.values());
        return state;
    }

    private void updateGameLogic(double deltaTime) {
        // 資源生成ロジック
        for (BuildingState building : buildings.values()) {
            if (building.type == BuildingType.RESOURCE) {
                // 5秒ごとに10資源を生成
                // 実装の詳細は時間管理が必要
                // ここでは単純化
                for (PlayerState player : players.values()) {
                    player.resources += 10;
                }
            }
        }

        // ユニットの攻撃ロジック
        for (UnitState unit : units.values()) {
            if (unit.type == UnitType.DEFENSE) {
                // 防衛ユニットの攻撃ロジック
                // 範囲内の敵ユニットを探して攻撃
                // ここでは単純化
                // 他プレイヤーのユニットを攻撃
                for (UnitState enemyUnit : units.values()) {
                    if (enemyUnit.type == UnitType.SIEGE && distance(unit.x, unit.y, enemyUnit.x, enemyUnit.y) <= 80) {
                        enemyUnit.hp -= 30;
                        if (enemyUnit.hp <= 0) {
                            units.remove(enemyUnit.id);
                        }
                        break;
                    }
                }
            } else if (unit.type == UnitType.SIEGE) {
                // 攻城ユニットの攻撃ロジック
                // 相手の城を攻撃
                for (PlayerState player : players.values()) {
                    if (player.castle.x < unit.x) { // 相手の城を特定
                        if (distance(unit.x, unit.y, player.castle.x, player.castle.y) <= 120) {
                            player.castle.hp -= 50;
                            if (player.castle.hp <= 0) {
                                // ゲーム終了
                                GameOver over = new GameOver();
                                over.winner = "Player"; // 攻城ユニットを持つプレイヤー
                                // 送信はGameServerから行う
                            }
                        }
                    }
                }
            }
        }

        // ゲームオーバー判定
        for (PlayerState player : players.values()) {
            if (player.castle.hp <= 0) {
                // 勝者を決定
                String winner = "";
                for (PlayerState p : players.values()) {
                    if (!p.playerName.equals(player.playerName)) {
                        winner = p.playerName;
                        break;
                    }
                }
                GameOver over = new GameOver();
                over.winner = winner;
                // 送信はGameServerから行う
            }
        }
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.hypot(x2 - x1, y2 - y1);
    }
}

補足:
updateGameLogicメソッドでは、簡略化された攻撃ロジックを実装しています。実際には、タイマーやイベントを使用して資源の生成やユニットの行動を正確に管理する必要があります。
ゲームオーバーの通知は、GameSessionクラス内で適切に実装する必要があります。例えば、特定の条件が満たされた場合にGameOverメッセージを全クライアントに送信します。

5. クライアントサイドの実装
クライアントは、ユーザーの操作をサーバーに送信し、サーバーからのゲーム状態を受信して表示します。

a. GameClient.java
java
コードをコピーする
package client;

import com.esotericsoftware.kryonet.*;
import common.messages.*;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class GameClient {
    private Client client;
    private GamePanel gamePanel;

    public GameClient(GamePanel gamePanel) throws IOException {
        this.gamePanel = gamePanel;
        client = new Client();
        registerClasses(client.getKryo());

        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof StartGame) {
                    StartGame start = (StartGame) object;
                    JOptionPane.showMessageDialog(null, start.message);
                } else if (object instanceof GameState) {
                    GameState state = (GameState) object;
                    SwingUtilities.invokeLater(() -> gamePanel.updateGameState(state));
                } else if (object instanceof GameOver) {
                    GameOver over = (GameOver) object;
                    JOptionPane.showMessageDialog(null, "ゲーム終了！勝者: " + over.winner);
                }
            }

            @Override
            public void connected(Connection connection) {
                // プレイヤー名の入力と送信
                String playerName = JOptionPane.showInputDialog("プレイヤー名を入力してください:");
                if (playerName != null && !playerName.trim().isEmpty()) {
                    JoinGame join = new JoinGame();
                    join.playerName = playerName;
                    client.sendTCP(join);
                } else {
                    JOptionPane.showMessageDialog(null, "プレイヤー名が無効です。接続を終了します。");
                    client.close();
                }
            }

            @Override
            public void disconnected(Connection connection) {
                JOptionPane.showMessageDialog(null, "サーバーから切断されました。");
            }
        });

        client.start();
        // サーバーのIPアドレスを適切に設定
        client.connect(5000, "localhost", 54555, 54777);
    }

    private void registerClasses(com.esotericsoftware.kryo.Kryo kryo) {
        kryo.register(JoinGame.class);
        kryo.register(StartGame.class);
        kryo.register(BuildBuilding.class);
        kryo.register(UpgradeBuilding.class);
        kryo.register(DeployUnit.class);
        kryo.register(GameState.class);
        kryo.register(PlayerState.class);
        kryo.register(CastleState.class);
        kryo.register(BuildingState.class);
        kryo.register(UnitState.class);
        kryo.register(GameOver.class);
        kryo.register(java.util.ArrayList.class);
    }

    public void sendBuildBuilding(BuildingType type, double x, double y) {
        BuildBuilding build = new BuildBuilding();
        build.buildingType = type;
        build.x = x;
        build.y = y;
        client.sendTCP(build);
    }

    public void sendUpgradeBuilding(int buildingId) {
        UpgradeBuilding upgrade = new UpgradeBuilding();
        upgrade.buildingId = buildingId;
        client.sendTCP(upgrade);
    }

    public void sendDeployUnit(UnitType type, double x, double y) {
        DeployUnit deploy = new DeployUnit();
        deploy.unitType = type;
        deploy.x = x;
        deploy.y = y;
        client.sendTCP(deploy);
    }
}
b. GameClientMain.java
java
コードをコピーする
package client;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.io.IOException;

public class GameClientMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GamePanel gamePanel = new GamePanel();
            JFrame frame = new JFrame("タワーディフェンス対戦ゲーム");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(gamePanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            try {
                GameClient client = new GameClient(gamePanel);
                gamePanel.setGameClient(client);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "サーバーに接続できませんでした。");
                System.exit(0);
            }
        });
    }
}
c. GamePanel.java
java
コードをコピーする
package client;

import common.enums.*;
import common.messages.*;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class GamePanel extends JPanel {
    private List<PlayerState> players;
    private List<BuildingState> buildings;
    private List<UnitState> units;
    private GameClient gameClient;
    private String selectedAction = null; // "BuildResource", "BuildDefense", "DeploySiege", "DeployDefense"

    public GamePanel() {
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        // SelectionPanelの追加
        SelectionPanel selectionPanel = new SelectionPanel(this);
        add(selectionPanel, BorderLayout.SOUTH);

        // マウスリスナーの追加
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedAction == null) return;

                double x = e.getX();
                double y = e.getY();

                if (selectedAction.equals("BuildResource")) {
                    gameClient.sendBuildBuilding(BuildingType.RESOURCE, x, y);
                } else if (selectedAction.equals("BuildDefense")) {
                    gameClient.sendBuildBuilding(BuildingType.DEFENSE, x, y);
                } else if (selectedAction.equals("DeploySiege")) {
                    gameClient.sendDeployUnit(UnitType.SIEGE, x, y);
                } else if (selectedAction.equals("DeployDefense")) {
                    gameClient.sendDeployUnit(UnitType.DEFENSE, x, y);
                }
            }
        });
    }

    public void setGameClient(GameClient client) {
        this.gameClient = client;
    }

    public void updateGameState(GameState state) {
        this.players = state.players;
        this.buildings = state.buildings;
        this.units = state.units;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // プレイヤーの城を描画
        if (players != null) {
            for (PlayerState player : players) {
                if (player.playerName.equals("Player")) {
                    g.setColor(Color.BLUE);
                } else {
                    g.setColor(Color.RED);
                }
                g.fillRect((int) player.castle.x - 20, (int) player.castle.y - 20, 40, 40);

                // HPバーの描画
                g.setColor(Color.RED);
                g.fillRect((int) player.castle.x - 20, (int) player.castle.y - 25, 40, 5);
                g.setColor(Color.GREEN);
                int hpBarWidth = (int) ((player.castle.hp / 1000.0) * 40);
                g.fillRect((int) player.castle.x - 20, (int) player.castle.y - 25, hpBarWidth, 5);
            }
        }

        // 建物を描画
        if (buildings != null) {
            for (BuildingState building : buildings) {
                if (building.type == BuildingType.RESOURCE) {
                    g.setColor(Color.GREEN);
                } else if (building.type == BuildingType.DEFENSE) {
                    g.setColor(Color.RED);
                }
                g.fillOval((int) building.x - 15, (int) building.y - 15, 30, 30);

                // レベル表示
                g.setColor(Color.WHITE);
                g.drawString("Lv " + building.level, (int) building.x - 10, (int) building.y + 5);
            }
        }

        // ユニットを描画
        if (units != null) {
            for (UnitState unit : units) {
                if (unit.type == UnitType.DEFENSE) {
                    g.setColor(Color.BLUE);
                } else if (unit.type == UnitType.SIEGE) {
                    g.setColor(Color.ORANGE);
                }
                g.fillOval((int) unit.x - 10, (int) unit.y - 10, 20, 20);

                // HPバーの描画
                g.setColor(Color.RED);
                g.fillRect((int) unit.x - 10, (int) unit.y - 15, 20, 3);
                g.setColor(Color.GREEN);
                int hpBarWidth = (int) ((unit.hp / 150.0) * 20);
                g.fillRect((int) unit.x - 10, (int) unit.y - 15, hpBarWidth, 3);
            }
        }
    }

    public void setSelectedAction(String action) {
        this.selectedAction = action;
    }
}
d. SelectionPanel.java
java
コードをコピーする
package client;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.GridLayout;
import common.enums.BuildingType;
import common.enums.UnitType;

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

6. 既存ゲームクラスの修正
既存のゲームクラスにサーバーとの連携機能を追加または修正します。以下のクラスを修正または拡張します。

a. GameObject.java（インターフェース）
java
コードをコピーする
package game;

import java.awt.Graphics;

public interface GameObject {
    void update(double deltaTime);
    void draw(Graphics g);
}
b. Building.java（抽象クラス）
java
コードをコピーする
package game;

import java.awt.Graphics;

public abstract class Building implements GameObject {
    protected double x, y;
    protected int level;
    protected int cost;

    public Building(double x, double y, int cost) {
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.level = 1;
    }

    // ゲッターとセッター
    public double getX() { return x; }
    public double getY() { return y; }
    public int getLevel() { return level; }
    public void upgrade() { level++; }
    public int getCost() { return cost; }

    // 更新と描画の抽象メソッド
    @Override
    public abstract void update(double deltaTime);
    @Override
    public abstract void draw(Graphics g);
}
c. Unit.java（抽象クラス）
java
コードをコピーする
package game;

import java.awt.Graphics;

public abstract class Unit implements GameObject {
    protected double x, y;
    protected double speed;
    protected int hp;
    protected int level;
    protected int cost;

    public Unit(double x, double y, double speed, int cost) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.cost = cost;
        this.level = 1;
        this.hp = 100; // 初期HP
    }

    // ゲッターとセッター
    public double getX() { return x; }
    public double getY() { return y; }
    public int getHp() { return hp; }
    public void damage(int amount) { hp -= amount; }
    public int getLevel() { return level; }
    public void upgrade() { level++; }
    public int getCost() { return cost; }

    // 更新と描画の抽象メソッド
    @Override
    public abstract void update(double deltaTime);
    @Override
    public abstract void draw(Graphics g);
}
d. ResourceBuilding.java
注意: 資源生成ロジックはサーバー側で管理するため、クライアント側では表示のみを行います。

java
コードをコピーする
package game;

import java.awt.Graphics;
import java.awt.Color;

public class ResourceBuilding extends Building {
    public ResourceBuilding(double x, double y) {
        super(x, y, 100);
    }

    @Override
    public void update(double deltaTime) {
        // クライアント側では特に処理なし
    }

    @Override
    public void draw(Graphics g) {
        // 資源設備の描画（緑の丸）
        g.setColor(Color.GREEN);
        g.fillOval((int)x - 15, (int)y - 15, 30, 30);

        // レベル表示
        g.setColor(Color.WHITE);
        g.drawString("Lv " + level, (int)x - 10, (int)y + 5);
    }
}
e. DefenseBuilding.java
注意: 防衛ロジックはサーバー側で管理するため、クライアント側では表示のみを行います。

java
コードをコピーする
package game;

import java.awt.Graphics;
import java.awt.Color;

public class DefenseBuilding extends Building {
    public DefenseBuilding(double x, double y) {
        super(x, y, 150);
    }

    @Override
    public void update(double deltaTime) {
        // クライアント側では特に処理なし
    }

    @Override
    public void draw(Graphics g) {
        // 防衛設備の描画（赤の四角）
        g.setColor(Color.RED);
        g.fillRect((int)x - 15, (int)y - 15, 30, 30);

        // レベル表示
        g.setColor(Color.WHITE);
        g.drawString("Lv " + level, (int)x - 10, (int)y + 5);
    }
}
f. DefenseUnit.java
注意: 防衛ユニットの攻撃ロジックはサーバー側で管理するため、クライアント側では表示のみを行います。

java
コードをコピーする
package game;

import java.awt.Graphics;
import java.awt.Color;

public class DefenseUnit extends Unit {
    public DefenseUnit(double x, double y) {
        super(x, y, 0, 50);
    }

    @Override
    public void update(double deltaTime) {
        // クライアント側では特に処理なし
    }

    @Override
    public void draw(Graphics g) {
        // 防衛ユニットの描画（青の丸）
        g.setColor(Color.BLUE);
        g.fillOval((int)x - 8, (int)y - 8, 16, 16);

        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int)x - 8, (int)y - 12, 16, 3);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int)((hp / 100.0) * 16);
        g.fillRect((int)x - 8, (int)y - 12, hpBarWidth, 3);
    }
}
g. SiegeUnit.java
注意: 攻城ユニットの攻撃ロジックはサーバー側で管理するため、クライアント側では表示のみを行います。

java
コードをコピーする
package game;

import java.awt.Graphics;
import java.awt.Color;

public class SiegeUnit extends Unit {
    public SiegeUnit(double x, double y, double speed, int cost) {
        super(x, y, speed, cost);
    }

    @Override
    public void update(double deltaTime) {
        // クライアント側では特に処理なし
    }

    @Override
    public void draw(Graphics g) {
        // 攻城ユニットの描画（オレンジの丸）
        g.setColor(Color.ORANGE);
        g.fillOval((int)x - 10, (int)y - 10, 20, 20);

        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int)x - 10, (int)y - 15, 20, 4);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int)((hp / 150.0) * 20);
        g.fillRect((int)x - 10, (int)y - 15, hpBarWidth, 4);
    }
}
h. Tower.java
TowerクラスはDefenseBuildingの一種として扱う場合、DefenseBuildingを直接使用できます。ただし、独自の機能を持たせたい場合は以下のように実装します。

java
コードをコピーする
package game;

import java.awt.Graphics;
import java.awt.Color;

public class Tower extends DefenseBuilding {
    public Tower(double x, double y) {
        super(x, y);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        // Tower特有の更新ロジックをここに追加
    }

    @Override
    public void draw(Graphics g) {
        // Towerの描画（より大きな赤い四角）
        g.setColor(Color.RED);
        g.fillRect((int)x - 20, (int)y - 20, 40, 40);

        // レベル表示
        g.setColor(Color.WHITE);
        g.drawString("Lv " + level, (int)x - 10, (int)y + 5);
    }
}
i. ResourceManager.java
注意: 資源の管理はサーバー側で行うため、クライアント側では表示のみを行います。ResourceManagerクラスはサーバーサイドにのみ存在します。

j. LevelManager.java
注意: レベルアップの管理もサーバー側で行います。LevelManagerクラスはサーバーサイドにのみ存在します。

7. 実行方法
a. サーバーの起動
サーバークラスのビルド: server.GameServerクラスをビルドします。
サーバーの起動: GameServerのmainメソッドを実行します。
bash
コードをコピーする
# コマンドラインから実行する場合
java -cp target/TowerDefenseGame-1.0-SNAPSHOT.jar server.GameServer
注意: 実際のコマンドはプロジェクトのビルド方法やクラスパス設定によって異なります。

b. クライアントの起動
クライアントクラスのビルド: client.GameClientMainクラスをビルドします。
クライアントの起動: GameClientMainのmainメソッドを実行します。
bash
コードをコピーする
# コマンドラインから実行する場合
java -cp target/TowerDefenseGame-1.0-SNAPSHOT.jar client.GameClientMain
注意: サーバーのIPアドレスがlocalhostとなっているため、サーバーとクライアントが同一マシン上にある場合はそのまま接続できます。異なるマシンから接続する場合は、GameClient.javaのclient.connectメソッド内のIPアドレスをサーバーのパブリックIPに変更してください。

java
コードをコピーする
client.connect(5000, "サーバーのIPアドレス", 54555, 54777);

8. 補足説明
a. クラスの役割
共通クラス（common）: サーバーとクライアント間で使用されるメッセージやデータモデルを定義。
サーバーサイドクラス（server）: ゲームのロジック管理、プレイヤーの接続管理、ゲーム状態の同期。
クライアントサイドクラス（client）: ユーザーインターフェースの表示、サーバーとの通信、ユーザー操作の送信。
ゲームロジッククラス（game）: ゲーム内のオブジェクト（建物、ユニット、城など）の表現と描画。

b. サーバーとクライアントの通信フロー
接続:
クライアントがサーバーに接続。
クライアントはプレイヤー名を入力し、JoinGameメッセージを送信。
サーバーはプレイヤーをGameSessionに追加。

ゲーム開始:
サーバーが2人のプレイヤーが揃ったことを検知。
サーバーはStartGameメッセージを全クライアントに送信。
サーバーはゲームループを開始し、定期的にゲームロジックを更新。

ゲームプレイ:
クライアントがユーザー操作（建物の建設、ユニットの配置）を行う。
クライアントは対応するメッセージ（BuildBuilding、DeployUnitなど）をサーバーに送信。
サーバーはメッセージを処理し、ゲーム状態を更新。
サーバーは更新後のGameStateを全クライアントに送信。

ゲーム終了:
サーバーが勝者を決定。
サーバーはGameOverメッセージを全クライアントに送信。

c. スレッドセーフな実装
KryoNetは内部でマルチスレッド処理を行うため、GameSessionクラス内で複数のクライアントからのアクセスがある場合は、スレッドセーフなデータ構造を使用するか、同期化を行う必要があります。上記の例では、HashMapを使用していますが、必要に応じてConcurrentHashMapに変更してください。

java
コードをコピーする
private Map<Connection, PlayerState> players = new ConcurrentHashMap<>();
private Map<Integer, BuildingState> buildings = new ConcurrentHashMap<>();
private Map<Integer, UnitState> units = new ConcurrentHashMap<>();
d. クライアント側のGUI更新
クライアント側でサーバーから受信したGameStateをGUIに反映する際、**Swingのイベントディスパッチスレッド（EDT）**を使用してGUIを更新します。これにより、スレッドセーフなGUI更新が保証されます。

java
コードをコピーする
@Override
public void received(Connection connection, Object object) {
    if (object instanceof GameState) {
        GameState state = (GameState) object;
        SwingUtilities.invokeLater(() -> gamePanel.updateGameState(state));
    }
}

e. ゲームロジックの詳細な実装
上記のGameSessionクラスでは、簡略化されたゲームロジックを示しています。実際のゲームでは、以下の機能を詳細に実装する必要があります：

資源生成のタイマー管理: 各ResourceBuildingが定期的に資源を生成。
ユニットの行動管理: ユニットの移動、攻撃、ターゲットの選定。
建物やユニットの耐久度管理: ダメージを受けた際の耐久度減少と破壊処理。
ゲーム状態の正確な同期: クライアントとサーバー間でゲーム状態を正確に同期。

9. まとめ
本ガイドでは、Javaを使用した対戦型タワーディフェンスゲームにサーバー機能を組み込み、リアルタイムで1対1の対戦を実現するための完全なバージョンを提供しました。以下のステップに従って開発を進めてください：

プロジェクト構成の設定: サーバー、クライアント、共通クラスを明確に分離。
依存関係の管理: KryoNetライブラリをプロジェクトに追加。
共通クラスの実装: メッセージクラスや列挙型を定義。
サーバーサイドの実装: GameServerとGameSessionクラスを実装し、ゲームロジックを管理。
クライアントサイドの実装: GameClientとGameClientMainクラスを実装し、サーバーとの通信とユーザーインターフェースを管理。
既存ゲームクラスの修正: ゲームオブジェクトクラスをインターフェースに基づいて修正し、描画のみを担当。
実行とテスト: サーバーとクライアントを起動し、接続とゲームの進行を確認。

開発を進める中での注意点：
スレッドセーフな実装: マルチスレッド環境でのデータアクセスに注意。
GUIのスレッドセーフな更新: Swingのルールに従ってGUIを更新。
ゲームロジックの詳細な実装: 資源生成、ユニットの行動、攻撃ロジックなどを正確に実装。
ネットワークエラーのハンドリング: 接続切断やパケットロスに対する対策。

今後の改善点：
ユニットの詳細な動作: ユニットの移動パス、攻撃範囲の管理。
資源生成の最適化: 各建物ごとの資源生成タイマーの実装。
ゲームバランスの調整: 建物やユニットのコスト、ダメージ、HPの調整。
追加のゲーム機能: レベルアップシステム、スキル、特殊能力など。
開発を進める中で、さらに具体的な質問や問題が発生した場合は、ぜひご相談ください。成功をお祈りしています！