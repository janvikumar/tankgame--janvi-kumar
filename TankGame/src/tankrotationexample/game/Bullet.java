package tankrotationexample.game;

import tankrotationexample.GameConstants;
import tankrotationexample.menus.Wall;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet extends GameObject {

    private float R = 10;
    private float angle;
    private BufferedImage img;
    private boolean hasCollided = false;
    private Rectangle hitBox;
    private final int ownerId;

    public int getOwnerId() { return ownerId; }


    public Rectangle getHitBox() {
        return this.hitBox.getBounds();
    }



    public Bullet(int x, int y, float angle, BufferedImage img, int ownerId) {

        super(x, y);
        this.angle = angle;
        this.img = img;
        this.ownerId = ownerId;
        this.hitBox = new Rectangle(x, y, img.getWidth(), img.getHeight());

    }
    public void update(){
        int dx = (int) Math.round(R * Math.cos(Math.toRadians(angle)));
        int dy = (int) Math.round(R * Math.sin(Math.toRadians(angle)));
        this.x += dx;
        this.y += dy;
        checkBorder();
        this.hitBox.setLocation((int)x, (int)y);

    }
    private void checkBorder() {
        if (x < 30) {
            hasCollided = true;
            x=30;
        }
        if (x >= GameConstants.WORLD_WIDTH - 50) {
            hasCollided = true;
            x=GameConstants.WORLD_WIDTH - 50;
        }
        if (y < 40) {
            hasCollided = true;
            y=40;
        }
        if (y >= GameConstants.WORLD_HEIGHT - 50) {
            hasCollided = true;
            y=GameConstants.WORLD_HEIGHT - 50;
        }
    }
    public boolean isAlive() {
        return !hasCollided;
    }

    public void kill() {
        this.hasCollided = true;
        if (this.hitBox != null) {
            this.hitBox.setBounds(0, 0, 0, 0);
        }
    }



    @Override
    public void drawImage(Graphics2D g) {
        if(!hasCollided){
            AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
            rotation.rotate(
                    Math.toRadians(angle),
                    img.getWidth() / 2.0,
                    img.getHeight() / 2.0
            );
            g.drawImage(this.img, rotation, null);
            g.setColor(Color.RED);
            g.drawRect(this.x, this.y, img.getWidth(), img.getHeight());

        }

    }

    @Override
    public void handleCollision(GameObject w) {
        if (!isAlive()) return;
        if (w instanceof Wall
                || w instanceof BreakableWall
                || w instanceof Shield
                || w instanceof SpeedPowerUp
                || w instanceof HealthPowerUp
                || w instanceof Tank) {
            kill();
        }
    }
}
