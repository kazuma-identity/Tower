import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GameMain {
    public static Font customFont = null;

    // ウィンドウを再利用するため static で持つ
    public static JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            loadCustomFont();
            startGame();
        });
    }

    /**
     * ゲームを初期化して開始するメソッド。
     * ここを再度呼び出すことで「リトライ」時に新しいゲームを始められます。
     */
    public static void startGame() {
        // プレイヤー名入力用のカスタムダイアログ
        String playerName = showCustomInputDialog();
        // 何も入力されなかったら終了
        if (playerName == null || playerName.trim().isEmpty()) {
            System.exit(0);
        }

        // プレイヤーとボットの作成
        Player player = new Player(playerName, 100.0);
        Castle playerCastle = new Castle(100, 400, 1000.0);
        player.setCastle(playerCastle);

        Player bot = new Player("Bot", 100.0);
        Castle botCastle = new Castle(900, 400, 1000.0);
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
        frame = new JFrame("対戦型タワーディフェンスゲーム");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private static void loadCustomFont() {
        try {
            File fontFile = new File("Battle Tough.otf");
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            // フォントサイズ等を変更
            customFont = baseFont.deriveFont(24f);

            // GraphicsEnvironment に登録 (任意)
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);

        } catch (IOException e) {
            e.printStackTrace();
            // 読み込みに失敗した場合、fallbackフォントを設定
            customFont = new Font("SansSerif", Font.PLAIN, 18);
        } catch (Exception e) {
            e.printStackTrace();
            customFont = new Font("SansSerif", Font.PLAIN, 18);
        }
    }

    /**
     * プレイヤー名入力用のカスタムダイアログ
     */
    private static String showCustomInputDialog() {
        // ダイアログの作成
        JDialog dialog = new JDialog((Frame) null, "Tower Defense Game", true);
        dialog.setSize(1000, 700);
        dialog.setLocationRelativeTo(null);

        // === 背景用のパネルを用意し、ダイアログの contentPane にする ===
        ImageIcon bgIcon = new ImageIcon("TitleBackground.jpg"); // 背景画像ファイル
        BackgroundPanel backgroundPanel = new BackgroundPanel(bgIcon.getImage());
        backgroundPanel.setLayout(new BorderLayout());
        dialog.setContentPane(backgroundPanel);

        // === タイトル部分 ===
        JLabel titleLabel = new JLabel("TOWER DEFENSE GAME");
        if (customFont != null) {
            titleLabel.setFont(customFont.deriveFont(Font.BOLD, 64f));
        } else {
            titleLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        }
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(Box.createVerticalStrut(130)); 
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(10));
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);

        // === 中央の入力パネル ===
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel messageLabel = new JLabel("プレイヤー名を入力してください:");
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setOpaque(true);
        messageLabel.setBackground(Color.WHITE);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        messageLabel.setFont(new Font("Dialog", Font.BOLD, 32));

        centerPanel.add(Box.createVerticalStrut(100));
        centerPanel.add(messageLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        JTextField textField = new JTextField(20);
        textField.setMaximumSize(new Dimension(250, 30));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(textField);

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // === 下部のボタンパネル ===
        JButton okButton = new JButton("スタート");
        JButton cancelButton = new JButton("ゲーム終了");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // ボタンパネルをさらに BoxLayout で包んで下部余白
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(buttonPanel);
        bottomPanel.add(Box.createVerticalStrut(20));
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        // ボタン動作
        final String[] result = { null };
        okButton.addActionListener(e -> {
            result[0] = textField.getText().trim();
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });

        dialog.setVisible(true);
        return result[0];
    }

    /**
     * 背景画像を描画するパネル
     */
    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        public BackgroundPanel(Image image) {
            this.backgroundImage = image;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // パネルの大きさに合わせて背景を拡大/縮小して描画
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
