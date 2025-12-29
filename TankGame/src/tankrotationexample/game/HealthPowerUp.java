package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;
import tankrotationexample.util.AssetManager;


public class HealthPowerUp extends GameObject {
    private Rectangle hitBox;
    private final BufferedImage sprite;

    public boolean isAlive() {
        return alive;
    }


    public Rectangle getHitBox() {
        return this.hitBox.getBounds();
    }


    public HealthPowerUp(int x, int y, BufferedImage health) {
        super(x, y);
        this.sprite = health;
        this.hitBox = new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());

    }

    @Override
    public void drawImage(Graphics2D g) {
        if (!alive || sprite == null) return;
        g.drawImage(sprite, x, y, null);
    }

    @Override
    public void handleCollision(GameObject w) {
        //if (!alive) return;
        if (w instanceof Tank t) {
            t.heal();
            AssetManager.getSound("pickup").setVolume(1.0f).play();
            alive = false;
            hitBox.setBounds(0, 0, 0, 0);
        }
    }
}
