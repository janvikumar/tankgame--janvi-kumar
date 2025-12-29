package tankrotationexample.game;

import tankrotationexample.util.AssetManager;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class AnimationSystem {
    private static final List<Animation> anims = new ArrayList<>();

    public static void spawn(float x, float y, List<BufferedImage> frames) {
        synchronized (anims) {
            anims.add(new Animation(x, y, (List) frames));
        }
    }

    public static void updateAll() {
        synchronized (anims) {
            for (Animation a : anims) a.update();
            anims.removeIf(a -> !a.isRunning());
        }
    }


    public static void renderAll(Graphics g) {
        List<Animation> snapshot;
        synchronized (anims) {
            snapshot = new ArrayList<>(anims);
        }
        for (Animation a : snapshot) a.render(g);
    }
    public static void clear() {
        synchronized (anims) {
            anims.clear();
        }
    }
}