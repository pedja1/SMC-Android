package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Benjamin Schulte on 25.10.2017.
 */

public class Level {
    private static List<String> levelIds;
    private static HashMap<String, Level> levels;

    public String levelId;
    public int currentScore;
    public int bestScore;
    public String textureName;
    public int world;
    public String levelName;
    public int number;

    /**
     * Get next level, or null if no more levels
     */
    public static String getNextLevel(String currentLevel) {
        for (int i = 0; i < levelIds.size(); i++) {
            String level = levelIds.get(i);
            if (level.equals(currentLevel)) {
                if (i + 1 < levelIds.size()) {
                    return levelIds.get(i + 1);
                }
                return null;
            }
        }
        return null;
    }


    public static List<String> getLevelList() {
        if (levelIds == null) {
            //beim ersten Mal initialisieren
            levels = new HashMap<>();
            levelIds = new ArrayList<>();

            JsonValue jsonLevel = new JsonReader().parse(Gdx.files.internal("data/levels/levels.json")).get("levels");

            int numWorld = 0;
            int numLevel = 0;
            for (JsonValue world = jsonLevel.child; world != null; world = world.next) {
                numWorld++;

                for (JsonValue level = world.child; level != null; level = level.next) {
                    numLevel++;
                    Level curlevel = new Level();

                    curlevel.textureName = level.getString("texture", null);
                    curlevel.levelId = level.getString("id");
                    curlevel.levelName = level.getString("name");
                    curlevel.world = numWorld;
                    curlevel.number = numLevel;

                    levelIds.add(curlevel.levelId);
                    levels.put(curlevel.levelId, curlevel);
                }
            }
        }

        return levelIds;
    }

    public static Level getLevel(String levelId) {
        if (levels == null)
            getLevelList();

        return levels.get(levelId);
    }
}
