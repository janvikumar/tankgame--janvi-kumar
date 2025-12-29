package tankrotationexample.util;

import tankrotationexample.GameConstants;
import tankrotationexample.game.GameObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class MapLoader {
    private static Map<String,String> maps = new HashMap<>(){{

            put("level1","maps/level1.csv");
            put("level2","maps/level2.csv");
            put("level3","maps/level3.csv");

            }};


    public static List<GameObject> loadMapObjects(final String level) {
        System.out.println("MapLoader loading [" + level + "] -> " + maps.get(level));
        final List<GameObject> list = new ArrayList<>(1151); //might be an overe
        String leveltoLoad = maps.get(level);
        InputStreamReader isr = new InputStreamReader(
                AssetManager.class.getClassLoader()
                        .getResourceAsStream(maps.get(level)
                        )
        );

        List<GameObject> objects = new ArrayList<>();

        try (BufferedReader mapReader = new BufferedReader(isr)) {
            int row = 0;
            String line;

            while ((line = mapReader.readLine()) != null && row < GameConstants.MAP_ROWS) {
                line = line.trim();
                if (line.isEmpty()) continue;
                // told to comment outString trimmed = line.trim();

                String[] items = line.split("\\s*,\\s*");// comma OR whitespace/tab

                if (row == GameConstants.MAP_ROWS - 1) {
                    System.out.println("LAST ROW raw line = [" + line + "]");
                    System.out.println("LAST ROW items length = " + items.length);
                    System.out.println("LAST ROW first='" + items[0] + "' last='" + items[items.length - 1] + "'");
                }
                for (int col = 0; col < items.length && col < GameConstants.MAP_COLS; col++) {
                    String raw = items[col];
                    String cleaned = raw
                            .replace("\uFEFF", "")
                            .replace("\r", "")
                            .trim();

                    if (cleaned.isEmpty()) continue;


                    int tile;
                    try {
                        tile = Integer.parseInt(cleaned);
                    } catch (NumberFormatException nfe) {
                        System.out.println(
                                "BAD TOKEN row=" + row +
                                        " col=" + col +
                                        " raw=[" + raw + "] cleaned=[" + cleaned + "]"
                        );
                        continue;
                    }

                    if (tile == 0) continue;

                    if (row == GameConstants.MAP_ROWS - 1 && (col == 0 || col == 22 || col == 44)) {
                        System.out.println("BOTTOM PARSE row=" + row + " col=" + col +
                                " tile='" + tile + "' x=" + (col * GameConstants.TILE_SIZE) +
                                " y=" + (row * GameConstants.TILE_SIZE));
                    }

                  //  if (type.equals("0")) continue; // || type.equals("4") || type.equals("5") || type.equals("6")) continue;

                    int x = col * GameConstants.TILE_SIZE;
                    int y = row * GameConstants.TILE_SIZE;

                    String type = String.valueOf(tile);
                    // translate numeric tank spawns -> string ids
                    if (tile == 7) type = "tank1";
                    if (tile == 8) type = "tank2";
                    try {
                        GameObject obj = GameObject.newInstance(type, x, y);
                        objects.add(obj);
                        if (type.equals("tank1") || type.equals("tank2")) {
                            System.out.println("SPAWN " + type + " at x=" + x + " y=" + y + " (row=" + row + " col=" + col + ")");
                        }

                        if (row == GameConstants.MAP_ROWS - 1 && (col == 0 || col == 22 || col == 44)) {
                            System.out.println("BOTTOM ADDED row=" + row + " col=" + col +
                                    " -> " + obj.getClass().getName() + " x=" + obj.getX() + " y=" + obj.getY());
                        }

                    } catch (IllegalArgumentException ex) {
                        System.out.println("BAD TILE type='" + tile + "' row=" + row + " col=" + col + " raw='" + items[col] + "'");
                        throw ex;
                    }
                }

                row++;
            }
            System.out.println("Map rows read = " + row);
            System.out.println("Expected rows = " + GameConstants.MAP_ROWS);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return objects;
    }

}
