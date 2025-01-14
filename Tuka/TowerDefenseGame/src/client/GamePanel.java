package client;

import common.enums.*;
import common.messages.*;
import game.UIComponents.*;

import javax.swing.JPanel;
//import javax.swing.JButton;
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