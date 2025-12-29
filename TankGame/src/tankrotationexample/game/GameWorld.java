package tankrotationexample.game;
import tankrotationexample.GameConstants;
import tankrotationexample.Launcher;
import tankrotationexample.menus.Wall;
import tankrotationexample.util.AssetManager;
import tankrotationexample.util.MapLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedTransferQueue;

/**
 * @author anthony-pc
 */
public class GameWorld extends JPanel implements Runnable {

    private BufferedImage world;
    private Tank t1;
    private Tank t2;
    private final Launcher lf;
    private long tick = 0;
    List<GameObject> gObjs = new ArrayList<>();
    private Sound bg;
    private int t1SpawnX, t1SpawnY;
    private float t1SpawnAngle = 0f;
    private int t2SpawnX, t2SpawnY;
    private float t2SpawnAngle = 180f;
    private boolean gameOver = false;
    private List<Animation> anims = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();

    public GameWorld(Launcher lf) {
        this.lf = lf;
    }

    private boolean isWall(GameObject o) {
        return (o instanceof Wall) || (o instanceof BreakableWall);
    }

    private boolean isPowerUp(GameObject o) {
        return (o instanceof SpeedPowerUp)
                || (o instanceof Shield)
                || (o instanceof HealthPowerUp);
    }
    private boolean pickedUp(GameObject o) {
        if (o instanceof SpeedPowerUp sp) return !sp.isAlive();
        if (o instanceof Shield sh) return !sh.isAlive();
        if (o instanceof HealthPowerUp hp) return !hp.isAlive();
        return false;
    }
    private boolean isUnbreakableWall(GameObject o) {
        return (o instanceof Wall); // your Map factory uses Wall for "9" and "3"
    }
    private volatile String pendingLevel = null;

    public void requestLoadLevel(String level) {
        System.out.println("GameWorld requestLoadLevel = [" + level + "]");
        this.pendingLevel = level;
    }



    private boolean isStaticCollidable(GameObject o) {
        return isWall(o) || isPowerUp(o);
    }

    private void checkCollisions() {
        List<GameObject> statics = new ArrayList<>();
        for (GameObject o : gObjs) {
            if (isStaticCollidable(o)) statics.add(o);
        }

        for (Bullet b : t1.getAmmo()) {
            if (!b.isAlive()) continue;


            for (GameObject s : statics) {
                if (b.getHitBox().intersects(s.getHitBox())) {
                    b.handleCollision(s);
                    s.handleCollision(b);
                    break;
                }
            }
            // bullet vs tank2
            if (b.isAlive() && b.getHitBox().intersects(t2.getHitBox())) {
                boolean wasAlive = t2.isAlive();

                t2.handleCollision(b);
                b.handleCollision(t2);

                //  tank  die -> explosion
                if (wasAlive && !t2.isAlive()) {
                    var frames = AssetManager.getAnimation("explosion_lg");
                    int fw = frames.get(0).getWidth();
                    int fh = frames.get(0).getHeight();

                    float drawX = t2.getCenterX() - fw / 2f;
                    float drawY = t2.getCenterY() - fh / 2f;

                    AnimationSystem.spawn(drawX, drawY, frames);
                    AssetManager.getSound("explosion").setVolume(1.2f).play(); // optional
                }
                continue;
            }

        }
        for (Bullet b : t2.getAmmo()) {
            if (!b.isAlive()) continue;

            for (GameObject s : statics) {
                if (b.getHitBox().intersects(s.getHitBox())) {
                    Rectangle bhb = b.getHitBox();
                    Rectangle shb = s.getHitBox();

                    b.handleCollision(s);
                    s.handleCollision(b);

                    if (isUnbreakableWall(s)) {
                        var frames = AssetManager.getAnimation("explosion_sm");
                        int fw = frames.get(0).getWidth();
                        int fh = frames.get(0).getHeight();

                        float cx = shb.x + shb.width / 2f;
                        float cy = shb.y + shb.height / 2f;

                        AnimationSystem.spawn(cx - fw / 2f, cy - fh / 2f, frames);
                    }
                    break;
                }

            }
            if (b.isAlive() && b.getHitBox().intersects(t1.getHitBox())) {
                boolean wasAlive = t1.isAlive();

                t1.handleCollision(b);
                b.handleCollision(t1);

                if (wasAlive && !t1.isAlive()) {
                    var frames = AssetManager.getAnimation("explosion_lg");
                    int fw = frames.get(0).getWidth();
                    int fh = frames.get(0).getHeight();

                    float drawX = t1.getCenterX() - fw / 2f;
                    float drawY = t1.getCenterY() - fh / 2f;

                    AnimationSystem.spawn(drawX, drawY, frames);
                    AssetManager.getSound("explosion").setVolume(1.2f).play(); // optional
                }
                continue;
            }

        }
       //  TANK -> WALLS
        for (GameObject s : statics) {
            if (!isWall(s)) continue;

            if (t1.getHitBox().intersects(s.getHitBox())) {
                t1.handleCollision(s);
                s.handleCollision(t1);
            }
            if (t2.getHitBox().intersects(s.getHitBox())) {
                t2.handleCollision(s);
                s.handleCollision(t2);
            }
        }
        // 3) TANK -> POWERUPS
        // -----------------------------------------
        for (GameObject s : statics) {
            if (!isPowerUp(s)) continue;

            // Tank 1
            if (t1.getHitBox().intersects(s.getHitBox())) {
                Rectangle hb = s.getHitBox();

                t1.handleCollision(s);
                s.handleCollision(t1);

                if (pickedUp(s)) {
                    var frames = AssetManager.getAnimation("powerpick");
                    int fw = frames.get(0).getWidth();
                    int fh = frames.get(0).getHeight();

                    int cx = hb.x + hb.width / 2;
                    int cy = hb.y + hb.height / 2;

                    float drawX = cx - fw / 2f;
                    float drawY = cy - fh / 2f;

                    AnimationSystem.spawn(drawX, drawY, frames);
                }
            }
            // Tank 2
            if (t2.getHitBox().intersects(s.getHitBox())) {
                Rectangle hb = s.getHitBox();

                t2.handleCollision(s);
                s.handleCollision(t2);

                if (pickedUp(s)) {
                    var frames = AssetManager.getAnimation("powerpick");
                    int fw = frames.get(0).getWidth();
                    int fh = frames.get(0).getHeight();

                    int cx = hb.x + hb.width / 2;
                    int cy = hb.y + hb.height / 2;

                    float drawX = cx - fw / 2f;
                    float drawY = cy - fh / 2f;

                    AnimationSystem.spawn(drawX, drawY, frames);                }
            }

        }

        if (t1.getHitBox().intersects(t2.getHitBox())) {
            t1.handleCollision(t2);
            t2.handleCollision(t1);
        }
    }
    private void drawHealthBar(Graphics2D g2, int x, int y, int w, int h, Tank t, String label) {
        // background
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y, w, h);

        // filled amount
        float pct = Math.max(0f, Math.min(1f, t.getHpPercent()));
        int filled = (int) (w * pct);

        g2.setColor(Color.GREEN);
        g2.fillRect(x, y, filled, h);

        // border
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, w, h);

        // text: "Red Player: 25/25"
        g2.setColor(Color.WHITE);
        g2.drawString(label + ": " + t.getHp() + " / " + t.getMaxHp(), x + 6, y + h - 4);
    }



    public void run() {
        try {
            this.anims.add(
                    new Animation(400,400,
                            AssetManager.getAnimation("explosion_lg"))
            );
            this.anims.add(
                    new Animation(450,400,
                            AssetManager.getAnimation("explosion_sm"))
            );
            this.anims.add(
                    new Animation(50,400,
                            AssetManager.getAnimation("bullethit"))
            );this.anims.add(
                    new Animation(400,400,
                            AssetManager.getAnimation("bulletshoot"))
            );this.anims.add(
                    new Animation(400,500,
                            AssetManager.getAnimation("powerpick"))
            );this.anims.add(
                    new Animation(450,500,
                            AssetManager.getAnimation("puffsmoke"))
            );
            this.anims.add(
                    new Animation(500,500,
                            AssetManager.getAnimation("rocketflame"))
            );
            this.anims.add(
                    new Animation(500,500,
                            AssetManager.getAnimation("rockethit"))
            );
            Sound bg = AssetManager.getSound("bg")
                    .loopContinuously()
                    .setVolume(1.2f)
                    .play();;
            while (true) {
                if (pendingLevel != null) {
                    String levelToLoad = pendingLevel;
                    pendingLevel = null;

                    System.out.println(">>> LOADING LEVEL IN RUN LOOP: [" + levelToLoad + "]");
                    InitializeGame(levelToLoad);

                    // optional: clear animations/bullets if you keep any lists
                }

                this.tick++;

                // update tanks
                this.t1.update();
                this.t2.update();
                this.checkCollisions();
                gObjs.removeIf(o -> (o instanceof BreakableWall bw && !bw.isAlive())
                        || (o instanceof SpeedPowerUp sp && !sp.isAlive())
                        || (o instanceof Shield sh && !sh.isAlive())
                        || (o instanceof HealthPowerUp hp && !hp.isAlive())
                );
                //this.anims.forEach(Animation::update);
                AnimationSystem.updateAll();
                // Respawn logic
                if (!t1.isAlive() && t1.getLives() > 0) {
                    t1.respawnAt(t1SpawnX, t1SpawnY, t1SpawnAngle);
                }

                if (!t2.isAlive() && t2.getLives() > 0) {
                    t2.respawnAt(t2SpawnX, t2SpawnY, t2SpawnAngle);
                }
                if (t1.isGameOver() || t2.isGameOver()) {
                    SwingUtilities.invokeLater(() -> lf.setFrame("end"));
                    return; // stop game loop
                }




                this.repaint();

                Thread.sleep(1000 / 144);
            }
        } catch (InterruptedException ignored) {
            System.out.println(ignored);
        }
    }

    /**
     * Reset game to its initial state.
     */
    public void resetGame() {
        this.tick = 0;
        this.t1.setX(300);
        this.t1.setY(300);
    }

    /**
     * Load all resources for Tank Wars Game. Set all Game Objects to their
     * initial state as well.
     */
    public void InitializeGame(String level)  {
        // world buffer (full map size)
        this.world = new BufferedImage(
                GameConstants.WORLD_WIDTH,
                GameConstants.WORLD_HEIGHT,
                BufferedImage.TYPE_INT_RGB
        );
        System.out.println(">>> InitializeGame called with level = " + level);

        //load map
        this.gObjs = MapLoader.loadMapObjects(level);
        t1 = null;
        t2 = null;
        AnimationSystem.clear();

        for (GameObject obj : gObjs) {
            if (obj instanceof Tank tank) {
                if (t1 == null) {
                    t1 = tank;
                } else {
                    t2 = tank;
                }
            }
        }
            if (t1 == null || t2 == null) {
                throw new RuntimeException("ERROR: Map must contain tank1 and tank2");
        }
        t1SpawnX = t1.getX();
        t1SpawnY = t1.getY();
        t1SpawnAngle = t1.getAngle();

        t2SpawnX = t2.getX();
        t2SpawnY = t2.getY();
        t2SpawnAngle = t2.getAngle();

        // 3️⃣ DEBUG: count bottom row walls
        int bottomY = (GameConstants.MAP_ROWS - 1) * GameConstants.TILE_SIZE; // 1408
        int bottomCount = 0;

        for (GameObject o : this.gObjs) {
            if (o != null && o.y == bottomY) {
                bottomCount++;
            }
        }
        System.out.println("Objects at bottom row y=" + bottomY + " = " + bottomCount);
        System.out.println("WORLD IMG: " + world.getWidth() + "x" + world.getHeight());
        System.out.println("WORLD CONST: " + GameConstants.WORLD_WIDTH + "x" + GameConstants.WORLD_HEIGHT);
        System.out.println("VIEW: " + GameConstants.GAME_SCREEN_WIDTH + "x" + GameConstants.GAME_SCREEN_HEIGHT);



        // controls
        TankControl tc1 = new TankControl(
                t1,
                KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D,
                KeyEvent.VK_SPACE
        );

        TankControl tc2 = new TankControl(
                t2,
                KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                KeyEvent.VK_ENTER
        );

        // attach listeners to the JFrame (NOT the panel)
        JFrame frame = this.lf.getJf();
        frame.addKeyListener(tc1);
        frame.addKeyListener(tc2);
    }


    private void drawFloor(Graphics buffer) {
        BufferedImage floor = AssetManager.getSprite("floor");
        int tileW = floor.getWidth();
        int tileH = floor.getHeight();

        for (int x = 0; x < GameConstants.WORLD_WIDTH; x += tileW) {
            for (int y = 0; y < GameConstants.WORLD_HEIGHT; y += tileH) {
                buffer.drawImage(floor, x, y, null);
            }
        }
    }

    private void buildMiniMap(Graphics2D g2) {

        double scale = 0.15;

        int miniW = (int) Math.round(GameConstants.WORLD_WIDTH * scale);
        int miniH = (int) Math.round(GameConstants.WORLD_HEIGHT * scale);

        Rectangle clip = g2.getClipBounds();
        int screenW = clip.width;
        int screenH = clip.height;
        int margin = 12;
        //int screenW = this.getWidth();
       // int screenH = this.getHeight();
        // bottom-center, but guaranteed on-screen
        int x = (GameConstants.GAME_SCREEN_WIDTH - miniW) / 2;
        int y = GameConstants.GAME_SCREEN_HEIGHT - miniH - margin;

        // clamp so it never goes off screen
        x = Math.max(margin, Math.min(x, screenW - miniW - margin));
        y = Math.max(margin, Math.min(y, screenH - miniH - margin));

        // optional border/background
        g2.setColor(Color.BLACK);
        g2.fillRect(x - 3, y - 3, miniW + 6, miniH + 6);

        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.scale(scale, scale);

        // draw FULL world (no subimage)
        g2.drawImage(this.world, at, null);
        //System.out.println("miniW=" + (int)(GameConstants.WORLD_WIDTH*0.15) +
           //     " miniH=" + (int)(GameConstants.WORLD_HEIGHT*0.15) +
           //     " screen=" + GameConstants.GAME_SCREEN_WIDTH + "x" + GameConstants.GAME_SCREEN_HEIGHT);

    }


    private void buildSplitScreen(Graphics window) {
       // int viewW = GameConstants.GAME_SCREEN_WIDTH / 2;
       // int viewH = GameConstants.GAME_SCREEN_HEIGHT;

        int viewW = this.getWidth() / 2;
        int viewH = this.getHeight();

        Graphics2D g2 = (Graphics2D) window;
        int maxSx = this.world.getWidth()  - viewW;
        int maxSy = this.world.getHeight() - viewH;

        // ---------- LEFT (Tank 1) ----------
        int sx1 = (int) (t1.getX() - viewW / 2f);
        int sy1 = (int) (t1.getY() - viewH / 2f);

        sx1 = Math.max(0, Math.min(sx1, GameConstants.WORLD_WIDTH - viewW));
        sy1 = Math.max(0, Math.min(sy1, GameConstants.WORLD_HEIGHT - viewH));



        BufferedImage left = this.world.getSubimage(sx1, sy1, viewW, viewH);
        g2.drawImage(left, 0, 0, null);

        //window.drawImage(left, 0, 0, null);

        // ---------- RIGHT (Tank 2) ----------
        int sx2 = (int) (t2.getX() - viewW / 2f);
        int sy2 = (int) (t2.getY() - viewH / 2f);

        sx2 = Math.max(0, Math.min(sx2, GameConstants.WORLD_WIDTH - viewW));
        sy2 = Math.max(0, Math.min(sy2, GameConstants.WORLD_HEIGHT - viewH));



        BufferedImage right = this.world.getSubimage(sx2, sy2, viewW, viewH);
        g2.drawImage(right, viewW, 0, null);

        //window.drawImage(right, viewW, 0, null);

        // Divider
        g2.setColor(Color.BLACK);
        g2.fillRect(viewW - 2, 0, 4, viewH);
     //   System.out.println("t1=(" + t1.getX() + "," + t1.getY() + ") sy1=" + sy1 + " maxSy=" + maxSy);
      //  System.out.println("t2=(" + t2.getX() + "," + t2.getY() + ") sy2=" + sy2 + " maxSy=" + maxSy);
      //  System.out.println("t1y=" + t1.getY() + " sy1=" + sy1 + " maxSy=" + maxSy);
     //   System.out.println("t2y=" + t2.getY() + " sy2=" + sy2 + " maxSy=" + maxSy);

    }
    private int clamp(int v, int min, int max) {
        if (max < min) return min; // safety if world is smaller than view
        return Math.max(min, Math.min(v, max));
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D buffer = world.createGraphics();

        // 0) CLEAR the whole world image every frame
        buffer.setColor(Color.BLACK);
        buffer.fillRect(0, 0, world.getWidth(), world.getHeight());

        // 1) Draw floor/background FIRST
        drawFloor(buffer);
        // 3) Draw map objects
        List<GameObject> snapshot = new ArrayList<>(gObjs);
        for (GameObject obj : snapshot) {
            obj.drawImage(buffer);
        }
        //this.anims.forEach(a -> a.render(buffer));
        // 4) Draw tanks (only if tanks are NOT drawn inside gObjs)
        t1.drawImage(buffer);
       t2.drawImage(buffer);
        AnimationSystem.renderAll(buffer);

       for (Bullet b : bullets) b.drawImage(buffer);
       buffer.dispose();

        // 5) Now crop cameras from the finished world buffer
        Graphics2D g2 = (Graphics2D) g;
        buildSplitScreen(g2);
        buildMiniMap(g2);
        Graphics2D g2ui = (Graphics2D) g;
        drawHealthBar(g2ui, 20, 20, 220, 18, t1, "Player 1");  // left player
        int rightX = this.getWidth() / 2 + 20;
        drawHealthBar(g2ui, rightX, 20, 220, 18, t2, "PLayer 2"); // right player



    }


}
