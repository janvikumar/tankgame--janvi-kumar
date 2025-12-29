package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BreakableWall extends GameObject {

    private int hp;
    private final BufferedImage sprite;
    private boolean alive = true;
    private Rectangle hitBox;


    public Rectangle getHitBox() {
        return this.hitBox.getBounds();
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
    }

    public BreakableWall(int x, int y, int hp, BufferedImage sprite) {
        super(x, y);
        this.hp = hp;
        this.sprite = sprite;
        this.hitBox = new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
    }


    public boolean isAlive() {
        return alive;
    }

    public void takeHit() {
        if (!alive) return;
        hp--;
        if (hp <= 0) {
            alive = false;
            // disable hitbox so it stops colliding immediately
            hitBox.setBounds(0, 0, 0, 0);
        }
    }

    @Override
    public void drawImage(Graphics2D g) {
        if (!alive || sprite == null) return;
        g.drawImage(sprite, x, y, null);
    }

    @Override
    public void handleCollision(GameObject w) {
        if (!alive) return;

        if (w instanceof Bullet b) {
            takeHit();
            b.kill(); // bullet disappears on impact



        }
    }
}
