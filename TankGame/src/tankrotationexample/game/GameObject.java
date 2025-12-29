package tankrotationexample.game;

import tankrotationexample.menus.Wall;
import tankrotationexample.util.AssetManager;

import java.awt.*;

public abstract class GameObject {

    protected int x;
    protected int y;
    protected boolean alive = true;

    protected GameObject() { }

    protected GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }

    // Factory method
    public static GameObject newInstance(String type, int x, int y) {
        return switch (type) {
            case "9", "3" -> new Wall(x, y, AssetManager.getSprite("unbreak"));
            case "1"      -> new BreakableWall(x, y, 1, AssetManager.getSprite("break"));
            case "2"      -> new BreakableWall(x, y, 3, AssetManager.getSprite("break"));
            case "4"      -> new Shield(x, y, AssetManager.getSprite("shield"));
            case "5"      -> new SpeedPowerUp(x, y, AssetManager.getSprite("speed"));   // example
            case "6"      -> new HealthPowerUp(x, y, AssetManager.getSprite("health")); // example
            case "tank1" -> new Tank(x, y, 0, 0, 0f, AssetManager.getSprite("tank1"));
            case "tank2" -> new Tank(x, y, 0, 0, 180f, AssetManager.getSprite("tank2"));
            default -> throw new IllegalArgumentException("Unknown object type: " + type);
        };
    }

   public abstract Rectangle getHitBox();
    public abstract void drawImage(Graphics2D g);
    public abstract void handleCollision(GameObject w);
    // Base draw method (overridden by subclasses)

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }




    //public  void handleCollision(GameObject w);

}
