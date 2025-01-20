import javax.swing.JButton;
import java.awt.event.ActionListener;

public class BuildingButton extends JButton {
  private String action;
  private GamePanel gamePanel;

  public BuildingButton(String text, String action, GamePanel panel) {
    super(text);
    this.action = action;
    this.gamePanel = panel;

    addActionListener(e -> {
      gamePanel.setSelectedAction(this.action);
    });
  }
}
