package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Animation {
    private float x,y;
    private List<BufferedImage> frames;
    private int currentFrame;
    private int delay; //how fast the animation goes
    private long timeSinceLastUpdate;
    private boolean running;


    public Animation(float x, float y, List<BufferedImage> frames) {
        this.x = x;
        this.y = y;
        this.frames = frames;
        this.delay = 30;
        this.currentFrame = 0;
        this.timeSinceLastUpdate = 0;
        this.running = true;
    }
    public Animation(float x, float y, List<BufferedImage> frames, int delay) {
        this.x = x;
        this.y = y;
        this.frames = frames;
        this.delay = delay;
        this.currentFrame = 0;
        this.timeSinceLastUpdate = 0;
        this.running = true;

    }
    public void update() {
        long now = System.currentTimeMillis();
        if (now > this.delay + this.timeSinceLastUpdate) {
            this.timeSinceLastUpdate = now;

            this.currentFrame++;
            if (this.currentFrame >= this.frames.size()) {
                this.running = false;

            }
        }
    }
    public void render(Graphics g) {
        if (this.running) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(this.frames.get(currentFrame), (int) x, (int) y, null);
        }

    }
    public boolean isRunning() {
        return running;
    }


}
