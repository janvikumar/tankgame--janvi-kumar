package tankrotationexample.game;
import tankrotationexample.GameConstants;
import tankrotationexample.menus.Wall;
import tankrotationexample.util.AssetManager;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author anthony-pc
 */

public class Tank extends GameObject {
   private static int NEXT_ID = 1;
    private final int id = NEXT_ID++;

    public int getId() { return id; }

    public float getScreenX() {
        return screenX;
    }

    private float screenX;

    public float getScreenY() {
        return screenY;
    }
    public float getAngle() { return angle; }

    // spawn point (center of tank)
    public int getCenterX() { return this.x + img.getWidth() / 2; }
    public int getCenterY() { return this.y + img.getHeight() / 2; }

    private float screenY;
    private float vx;
    private float vy;
    private float angle;
    private int prevX;
    private int prevY;
    private int maxHp = 25;
    private int hp = maxHp;
    public int getHp() { return hp; }
    public int getMaxHp() {
        return maxHp;
    }

    public float getHpPercent() {
        if (maxHp == 0) return 0f;   // safety
        return (float) hp / (float) maxHp;
    }

    private boolean alive = true;
    public boolean isAlive() { return alive; }


    private float R = 5;
    private float baseSpeed;              // stores original R
    private float speedMultiplier = 1.0f;
    private long speedBoostEndTime = 0;
    private boolean shieldActive = false;
    private long shieldEndTime = 0;

    private float ROTATIONSPEED = 3.0f;

    private BufferedImage img;
    private Rectangle hitBox;

    public Rectangle getHitBox() {
        return this.hitBox.getBounds();
    }
    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    private boolean shootPressed;
    private long delay = 2000;
    private long timeSincelastShot = 0;
    private int lives = 3;

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        lives--;
        if (lives < 0) lives = 0;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    public void respawnAt(int spawnX, int spawnY, float spawnAngle) {
        this.x = spawnX;
        this.y = spawnY;
        this.angle = spawnAngle;
        this.hp = maxHp;
        this.alive = true;
        this.hitBox.setLocation(x, y);
        this.ammo.clear();
    }


    public List<Bullet> getAmmo() { return this.ammo;}
    List<Bullet> ammo = new ArrayList<Bullet>();

     Tank(int x, int y, int vx, int vy, float angle, BufferedImage img) {
        super(x, y);   // sets GameObject.x/y
        this.vx = vx;
        this.vy = vy;
        this.img = img;
        this.angle = angle;
         this.hitBox = new Rectangle(x, y, img.getWidth(), img.getHeight());
         this.baseSpeed =R;

     }
    public boolean checkCollisions(List<GameObject>objects) {
         return false;
    }
    public void heal(int amount) {
        if (!alive) return;

        hp = Math.min(maxHp, hp + amount);
        System.out.println("HEALED +" + amount + " => HP = " + hp);
    }
    void setX(int x){ this.x = x; }

    void setY(int y) { this. y = y;}

    void toggleUpPressed() {
        this.UpPressed = true;
        System.out.println("UP pressed for tank " + System.identityHashCode(this));

    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }
    void unToggleShootPressed() {
        this.shootPressed = false;
    }
    void ToggleShootPressed() {
        this.shootPressed = true;
    }
    private boolean wasBoosting = false;


    void update() {
        prevX = x;
        prevY = y;
        long now = System.currentTimeMillis();
        if (now > speedBoostEndTime) {
            speedMultiplier = 1.0f;
        }
        if (shieldActive && now > shieldEndTime) {
            shieldActive = false;
            System.out.println("SHIELD OFF for tank " + id);
        }




        if (this.UpPressed) {
            this.moveForwards();
        }

        if (this.DownPressed) {
            this.moveBackwards();
        }

        if (this.LeftPressed) {
            this.rotateLeft();
        }

        if (this.RightPressed) {
            this.rotateRight();
        }
        long currentTime = System.currentTimeMillis();
        if (this.shootPressed && currentTime > this.timeSincelastShot + this.delay) {
            AssetManager.getSound("bulletshoot").play();
                Bullet b = new Bullet(
                        getCenterX(),
                        getCenterY(),
                        angle,
                        AssetManager.getSprite("bullet"),
                        this.id
                );
                this.ammo.add(b);
            this.timeSincelastShot = System.currentTimeMillis();

        }
        for (int i = 0; i<this.ammo.size(); i++){
            this.ammo.get(i).update();
        }
        ammo.removeIf(b -> !b.isAlive());

        for(Bullet b: ammo){

        }


        centerScreen();
        this.hitBox.setLocation((int)x, (int)y);

    }


    private void centerScreen() {
        float viewW = GameConstants.GAME_SCREEN_WIDTH / 2f;  // split-screen width
        float viewH = GameConstants.GAME_SCREEN_HEIGHT;      // full height

        // center camera on tank
        this.screenX = this.x - viewW / 2f;
        this.screenY = this.y - viewH / 2f;

        // clamp to world bounds
        float maxX = GameConstants.WORLD_WIDTH  - viewW;
        float maxY = GameConstants.WORLD_HEIGHT - viewH;

        if (screenX < 0) screenX = 0;
        if (screenY < 0) screenY = 0;
        if (screenX > maxX) screenX = maxX;
        if (screenY > maxY) screenY = maxY;
    }


    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }

    private void moveBackwards() {
        float moveSpeed = R * speedMultiplier;
        int dx = (int) Math.round(moveSpeed * Math.cos(Math.toRadians(angle)));
        int dy = (int) Math.round(moveSpeed * Math.sin(Math.toRadians(angle)));
        this.x -= dx;
        this.y -= dy;
        checkBorder();
    }

    private void moveForwards() {
        float moveSpeed = R * speedMultiplier;
        int dx = (int) Math.round(moveSpeed * Math.cos(Math.toRadians(angle)));
        int dy = (int) Math.round(moveSpeed * Math.sin(Math.toRadians(angle)));
        this.x += dx;
        this.y += dy;
        checkBorder();
    }



    private void checkBorder() {
        int w = this.img.getWidth();
        int h = this.img.getHeight();

        if (x < 0) x = 0;
        if (x > GameConstants.WORLD_WIDTH - w) x = GameConstants.WORLD_WIDTH - w;

        if (y < 0) y = 0;
        if (y > GameConstants.WORLD_HEIGHT - h) y = GameConstants.WORLD_HEIGHT - h;
    }



    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }


    @Override
    public void drawImage(Graphics2D g2d) {
        if (!alive) return;

        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(
                Math.toRadians(angle),
                img.getWidth() / 2.0,
                img.getHeight() / 2.0
        );

        g2d.drawImage(this.img, rotation, null);
        g2d.setColor(Color.RED);
        g2d.drawRect(x, y, this.img.getWidth(), this.img.getHeight());
        for(int i = 0; i < ammo.size(); i++){
            ammo.get(i).drawImage(g2d);
        }
    }
    public boolean justDied() {
        return !alive && lives > 0;   // dead but still has lives left
    }

    @Override
    public void handleCollision(GameObject w) {
        if (w instanceof Bullet b) {
            if (b.getOwnerId() == this.id)
                return; // ignore own bullets
            if (shieldActive) {
                System.out.println("BLOCKED by shield tank " + id);
                b.kill();              // bullet disappears
                return;                // NO DAMAGE taken
            }
            hp -= 5;
            System.out.println("Tank " + id + " hit! HP = " + hp);
            if (hp <= 0) {
                hp = 0;
                alive = false;
                loseLife();
            }
            b.kill();
            return;
        }
        if (w instanceof Wall || w instanceof BreakableWall) {
            x = prevX;
            y = prevY;
            hitBox.setLocation(x, y);
        }

    }
    public void applySpeedBoost() {
        speedMultiplier = 1.75f; // boost amount (try 1.5–2.0)
        speedBoostEndTime = System.currentTimeMillis() + 4000; // 4 seconds
    }
    public void applyShield() {
        shieldActive = true;
        shieldEndTime = System.currentTimeMillis() + 5000;
        System.out.println("SHIELD ON for tank " + id);


    }
    public void heal(){
        if (!alive) return;

        int before = hp;
        hp = Math.min(maxHp, hp + 10); // heal amount (10)
        System.out.println("HEAL: " + before + " -> " + hp + " (tank " + id + ")");

    }




}



