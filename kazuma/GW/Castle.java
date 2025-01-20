import java.awt.Graphics;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Image;

public class Castle {
  private double x, y;
  private double MaxHealth; // 城の最大耐久値
  private double health; // 城の現在の耐久値
  private int size = 100; // 城のサイズ

  public Castle(double x, double y, double health) {
    this.x = x;
    this.y = y;
    this.MaxHealth = this.health = health;
  }

  // ゲッターとセッター
  public double getX() { return x; }
  public double getY() { return y; }
  public double getHp() { return health; }

  public void takeDamage(double damage) {
    health -= damage;
  }

  public void draw(Graphics g, Color color, Image castleImage) {
    // 城の描画（例: 指定された色の四角）
    g.drawImage(castleImage, (int) x-30, (int) y-28, 64, 64, null);

    // HPバーの描画
    g.setColor(Color.RED);
    g.fillRect((int) x - size / 2, (int) y - size / 2 - 10, size, 5);
    g.setColor(Color.GREEN);
    int hpBarWidth = (int) ((health / MaxHealth) * size); 
    g.fillRect((int) x - size / 2, (int) y - size / 2 - 10, hpBarWidth, 5);
  }
}
