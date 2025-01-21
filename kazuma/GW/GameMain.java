import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GameMain {
    public static Font customFont = null;

    // ウィンドウを再利用するため static で持つ
    public static JFrame frame;

    // デフォルトは EASY
    private static Difficulty difficulty = Difficulty.EASY;
    private static Object[] selectedDifficulty = { Difficulty.EASY };

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
        // 最終的に選択された難易度を表示
        System.out.println("最終的に選択された難易度: " + difficulty);

        // プレイヤー名入力用のカスタムダイアログ
        String playerName = showCustomInputDialog();
        if (playerName == null || playerName.trim().isEmpty()) {
            System.exit(0); // 未入力の場合は終了
        }

        // プレイヤーとボットの作成
        Player player = new Player(playerName, 100.0);
        Castle playerCastle = new Castle(100, 400, 1000.0);
        player.setCastle(playerCastle);

        Player botPlayer = new Player("Bot", 100.0);
        Castle botCastle = new Castle(900, 400, 1000.0);
        botPlayer.setCastle(botCastle);

        // ゲームの初期化
        Game game = new Game();
        game.setPlayers(player, botPlayer);

        // ゲームパネルの作成
        GamePanel gamePanel = new GamePanel();
        gamePanel.setGame(game);
        game.setGamePanel(gamePanel);

        // ボットの作成と開始 (difficultyを直接使用)
        Bot botAI = new Bot(game, botPlayer, player, difficulty);
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
            customFont = baseFont.deriveFont(24f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            customFont = new Font("SansSerif", Font.PLAIN, 18);
        }
    }

    private static String showCustomInputDialog() {
        JDialog dialog = new JDialog((Frame) null, "Tower Defense Game", true);
        dialog.setSize(1000, 700);
        dialog.setLocationRelativeTo(null);

        // 背景用のパネル
        ImageIcon bgIcon = new ImageIcon("TitleBackground.jpg");
        BackgroundPanel backgroundPanel = new BackgroundPanel(bgIcon.getImage());
        backgroundPanel.setLayout(new BorderLayout());
        dialog.setContentPane(backgroundPanel);

        // タイトル
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

        // 中央の入力パネル
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

        centerPanel.add(Box.createVerticalStrut(50));
        centerPanel.add(messageLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        JTextField textField = new JTextField(20);
        textField.setMaximumSize(new Dimension(250, 30));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(textField);

        // 難易度選択ボタン
        JPanel difficultyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        difficultyPanel.setOpaque(false);

        JButton easyButton = new JButton("Easy");
        JButton normalButton = new JButton("Normal");
        JButton hardButton = new JButton("Hard");
        centerPanel.add(difficultyPanel);

        // ボタンの初期色
        Color defaultColor = easyButton.getBackground();
        Color selectedColor = Color.YELLOW;

        easyButton.addActionListener(e -> {
            selectedDifficulty[0] = Difficulty.EASY;
            difficulty = Difficulty.EASY; // ★ ボタンが押されるたびに更新
            System.out.println("Easyが選択されました。現在のselectedDifficulty: " + selectedDifficulty[0]);
            easyButton.setBackground(selectedColor);
            normalButton.setBackground(defaultColor);
            hardButton.setBackground(defaultColor);
        });

        normalButton.addActionListener(e -> {
            selectedDifficulty[0] = Difficulty.NORMAL;
            difficulty = Difficulty.NORMAL; // ★ ボタンが押されるたびに更新
            System.out.println("Normalが選択されました。現在のselectedDifficulty: " + selectedDifficulty[0]);
            easyButton.setBackground(defaultColor);
            normalButton.setBackground(selectedColor);
            hardButton.setBackground(defaultColor);
        });

        hardButton.addActionListener(e -> {
            selectedDifficulty[0] = Difficulty.HARD;
            difficulty = Difficulty.HARD; // ★ ボタンが押されるたびに更新
            System.out.println("Hardが選択されました。現在のselectedDifficulty: " + selectedDifficulty[0]);
            easyButton.setBackground(defaultColor);
            normalButton.setBackground(defaultColor);
            hardButton.setBackground(selectedColor);
        });

        difficultyPanel.add(easyButton);
        difficultyPanel.add(normalButton);
        difficultyPanel.add(hardButton);

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // 下部のボタン
        JButton okButton = new JButton("スタート");
        JButton cancelButton = new JButton("ゲーム終了");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

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
            System.out.println("OKボタンが押されました。最終的なdifficulty: " + difficulty);
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });

        dialog.setVisible(true);
        return result[0];
    }

    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(Image image) {
            this.backgroundImage = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
