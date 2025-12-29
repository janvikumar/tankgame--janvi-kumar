package tankrotationexample.menus;

import tankrotationexample.game.GameObject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Wall extends GameObject {
    private Rectangle hitBox;

    public Rectangle getHitBox() {
        return this.hitBox.getBounds();
    }

    private final BufferedImage img;

    public Wall(int x, int y, BufferedImage img) {
        super(x, y);
        this.img = img;
        this.hitBox = new Rectangle(x, y, img.getWidth(), img.getHeight());

    }

    @Override
    public void drawImage(Graphics2D g) {
        if (img == null) return;
        g.drawImage(img, x, y, null);
    }

    @Override
    public void handleCollision(GameObject w) {

    }
}
