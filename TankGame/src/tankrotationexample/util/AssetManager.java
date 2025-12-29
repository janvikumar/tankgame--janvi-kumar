package tankrotationexample.util;

import tankrotationexample.game.GameWorld;
import tankrotationexample.game.Sound;
import tankrotationexample.game.Tank;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.*;
import java.io.BufferedInputStream;
import java.io.InputStream;


import static javax.swing.UIManager.put;
import static jdk.xml.internal.SecuritySupport.getClassLoader;

public class AssetManager {
    private static final Map<String, BufferedImage> sprites = new HashMap<>();
    private static final Map<String, Sound> sounds = new HashMap<>();
    private static final Map<String, List<BufferedImage>> animations = new HashMap<>();

    private static BufferedImage loadImage(final String path) throws IOException {
        return ImageIO.read(
                Objects.requireNonNull(
                        AssetManager.class.getClassLoader().getResource(path),
                        "Could not find %s".formatted(path)
                )
                );

    }
    private static Sound loadSound(String path)
            throws IOException, UnsupportedAudioFileException, LineUnavailableException{

        InputStream raw = Objects.requireNonNull(
                AssetManager.class.getClassLoader().getResourceAsStream(path),
                "Could not find sound: " + path
                );
        BufferedInputStream buffered = new BufferedInputStream(raw);
        AudioInputStream ais = AudioSystem.getAudioInputStream(buffered);
        Clip c = AudioSystem.getClip();
        c.open(ais);
        return new Sound (c);
    }

    private static void loadSprites() {
        try {
            AssetManager.sprites.put("tank1", loadImage("tank/tank1.png"));
            AssetManager.sprites.put("tank2", loadImage("tank/tank2.png"));
            AssetManager.sprites.put("bullet", loadImage("bullet/bullet.jpg"));
            AssetManager.sprites.put("rocket1", loadImage("bullet/rocket1.png"));
            AssetManager.sprites.put("rocket2", loadImage("bullet/rocket2.png"));
            AssetManager.sprites.put("floor", loadImage("floor/bg.bmp"));
            AssetManager.sprites.put("menu", loadImage("menu/title.png"));
            AssetManager.sprites.put("health", loadImage("powerups/health.png"));
            AssetManager.sprites.put("speed", loadImage("powerups/speed.png"));
            AssetManager.sprites.put("shield", loadImage("powerups/shield.png"));
            AssetManager.sprites.put("unbreak", loadImage("wall/wall1.png"));
            AssetManager.sprites.put("break", loadImage("wall/wall2.png"));



            //AssetManager.sprites.put("menu", loadImage("tank/menu.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    private static void loadAnimations() {
        (new HashMap<String,Integer>() {{
            put("bullethit", 24);
            put("bulletshoot",24);
            put("explosion_lg",6);
            put("explosion_sm",6);
            put("powerpick",32);
            put("puffsmoke",32);
            put("rocketflame",16);
            put("rockethit",32);

        }}).forEach((animationName, frameCount)->{
           String baseName= "animations/%s/%s_%04d.png";
           List<BufferedImage> frames = new ArrayList<>(frameCount);
           try {
               for (int i = 0; i < frameCount; i++) {
                   frames.add(loadImage(baseName.formatted(animationName, animationName, i)));
               }
           } catch (IOException e){
               throw new RuntimeException(e);
           }
           AssetManager.animations.put(animationName, frames);
        });
    }
        private static void loadSounds() {
        try {
            AssetManager.sounds.put("bullethit", loadSound("sounds/bullet.wav"));
            AssetManager.sounds.put("bulletshoot", loadSound("sounds/bullet_shoot.wav"));
            AssetManager.sounds.put("explosion", loadSound("sounds/explosion.wav"));
            AssetManager.sounds.put("pickup", loadSound("sounds/pickup.wav"));
            AssetManager.sounds.put("shooting", loadSound("sounds/shotfiring.wav"));
            AssetManager.sounds.put("bg", loadSound("sounds/Music.mid"));
        }catch (UnsupportedAudioFileException | LineUnavailableException |IOException e){
            System.out.println("could not load sounds");
            throw new RuntimeException(e);
        }



    }
    public static void loadAssets() {
        loadSprites();
       loadAnimations();
        loadSounds();
        System.out.println();
    }


    public static BufferedImage getSprite(String key) {
        BufferedImage img = sprites.get(key);
        if (img == null) throw new IllegalArgumentException("Key not found :: %s".formatted(key));
        return img;
    }
    public static Sound getSound(String key) {
        if (!AssetManager.sounds.containsKey(key)) {
            throw new IllegalArgumentException("Sound  not found :: %s".formatted(key));
        }
        return AssetManager.sounds.get(key);

    }
    public static List<BufferedImage> getAnimation(String key) {
        List<BufferedImage> frames = animations.get(key);
        if (frames == null) {
            throw new IllegalArgumentException("Animation not found :: %s".formatted(key));
        }
        return frames;
    }

}

