import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class GameMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // プレイヤー名の入力
            String playerName = JOptionPane.showInputDialog("プレイヤー名を入力してください:");
            if (playerName == null || playerName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "プレイヤー名が無効です。ゲームを終了します。");
                System.exit(0);
            }

            // プレイヤーとボットの作成
            Player player = new Player(playerName, 100.0);
            Castle playerCastle = new Castle(100, 300, 1000.0);
            player.setCastle(playerCastle);

            Player bot = new Player("Bot", 100.0);
            Castle botCastle = new Castle(900, 300, 1000.0);
            bot.setCastle(botCastle);

            // ゲームの初期化
            Game game = new Game();
            game.setPlayers(player, bot);

            // ゲームパネルの作成
            GamePanel gamePanel = new GamePanel();
            gamePanel.setGame(game);
            game.setGamePanel(gamePanel);

            // ボットの作成と開始
            Bot botAI = new Bot(game, bot, player);
            botAI.start();

            // JFrameの設定
            JFrame frame = new JFrame("対戦型タワーディフェンスゲーム");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(gamePanel);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}
