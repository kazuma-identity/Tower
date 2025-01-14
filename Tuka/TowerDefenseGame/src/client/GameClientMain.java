package client;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.io.IOException;
import javax.swing.JOptionPane;

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