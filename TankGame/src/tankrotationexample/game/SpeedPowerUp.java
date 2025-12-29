package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;
import tankrotationexample.util.AssetManager;

public class SpeedPowerUp extends GameObject {
    private boolean alive = true;

    public boolean isAlive() {
        return alive;
    }

    private Rectangle hitBox;

    public Rectangle getHitBox() {
        return this.hitBox.getBounds();
    }

    private final BufferedImage sprite;

    public SpeedPowerUp(int x, int y, BufferedImage speed) {
        super(x, y);
        this.sprite = speed;
        this.hitBox = new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());

    }

    @Override
    public void drawImage(Graphics2D g) {
        if (sprite == null) return;
        g.drawImage(sprite, x, y, null);
    }

    @Override
    public void handleCollision(GameObject w) {
        if (w instanceof Tank t) {
            t.applySpeedBoost();
            AssetManager.getSound("pickup").setVolume(1.0f).play();
            alive = false;
            hitBox.setBounds(0, 0, 0, 0);
        }
    }
}
