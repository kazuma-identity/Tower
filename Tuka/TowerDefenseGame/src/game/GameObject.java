package game;

import java.awt.Graphics;

public interface GameObject {
    void update(double deltaTime);
    void draw(Graphics g);
}