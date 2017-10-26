package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.screen.AbstractScreen;

public class GameSave {
    public static Save save;

    public static void init() {
        save = read();
    }

    public static void startLevelFresh() {
        reset();
    }

    public static void reset() {
        save.lifes = 3;
        save.playerState = Maryo.MaryoState.small;
        save.item = 0;
        save.coins = 0;
        save.points = 0;
    }

    public static Save read() {
        //read from prefs and deserialize to save
        return Save.readFromString(PrefsManager.getSaveGame());
    }

    public static void save() {
        // serialize save game and store to prefs
        PrefsManager.setSaveGame(Save.writeToString(save));
    }

    public static void dispose() {
        save();
        save = null;
    }

    public static boolean isUnlocked(String levelName) {
        return save.unlockedLevels.contains(levelName);
        //return true;
    }

    public static void unlockLevel(String levelName) {
        if (!save.unlockedLevels.contains(levelName)) {
            save.unlockedLevels.add(levelName);
            save();
        }
    }

    public static void addCoins(AbstractScreen screen, int coins) {
        save.coins += coins;
        if (save.coins >= 100) {
            save.coins -= 100;
            save.lifes++;
            AssetManager manager = screen.game.assets.manager;
            if (manager.isLoaded(Assets.SOUND_ITEM_LIVE_UP_2)) {
                SoundManager.play(manager.get(Assets.SOUND_ITEM_LIVE_UP_2, Sound.class));
            }
        }
    }

    public static int getCoins() {
        return save.coins;
    }

    public static void setItem(AssetManager manager, int itemType) {
        save.item = itemType;
    }

    public static int getItem() {
        return save.item;
    }

    public static class Save {
        public int points;
        //in memory only
        public Maryo.MaryoState playerState = Maryo.MaryoState.small;
        public int lifes;
        //persistent
        Set<String> unlockedLevels;
        private int coins;
        private int item;

        //copy constructor, only persistent objects are copied
        public Save(Save save) {
            unlockedLevels = save.unlockedLevels;
        }

        public Save() {
            unlockedLevels = new HashSet<>();
        }

        public static Save readFromString(String serializedSave) {
            if (serializedSave == null) return new Save();
            serializedSave = Utility.base64Decode(serializedSave);
            HashMap<String, String> map = new HashMap<String, String>();
            String[] values = serializedSave.split("\n");
            for (String s : values) {
                String[] keyValue = s.split("=");
                if (keyValue.length != 2) continue;
                map.put(keyValue[0], keyValue[1]);
            }
            Save save = new Save();
            String uLevls = map.get("unlocked_levels");
            if (uLevls != null) {
                String[] unlockedLevels = uLevls.split(",");
                Collections.addAll(save.unlockedLevels, unlockedLevels);
            }
            //save.points = Utility.parseInt(map.get("points"), 0);
            //save.coins = Utility.parseInt(map.get("coins"), 0);
            return save;
        }

        public static String writeToString(Save save) {
            if (save == null) return null;

            StringBuilder builder = new StringBuilder();
            builder.append("unlocked_levels=");

            for (String level : save.unlockedLevels) {
                builder.append(level).append(",");
            }
            //builder.append("\n").append("points=").append(save.points);
            //builder.append("\n").append("coins=").append(save.coins);

            return Utility.base64Encode(builder.toString());
        }
    }
}
