import javax.swing.JButton;
import java.awt.event.ActionListener;

public class UnitButton extends JButton {
  private String action;
  private GamePanel gamePanel;

  public UnitButton(String text, String action, GamePanel panel) {
    super(text);
    this.action = action;
    this.gamePanel = panel;

    addActionListener(e -> {
      gamePanel.setSelectedAction(this.action);
    });
  }
}
