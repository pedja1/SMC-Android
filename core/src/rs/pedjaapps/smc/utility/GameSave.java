package rs.pedjaapps.smc.utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.object.maryo.Maryo;

public class GameSave
{
    public static final List<String> LEVELS = Arrays.asList("lvl_1", "lvl_2", "lvl_3", "lvl_4", "lvl_5",
			"lvl_6", "lvl_7", "lvl_8", "lvl_9", "pasol_3", "stephane_1", "tower_1", "eatomania", "lvl_10",
			"pasol_1", "flippa_3", "dj_kirby_1", "jungle_1", "keywest_1", "underground", "clear_night", "wn_01",
			"wn_02", "wn_03", "wn_04", "wn_05", "wn_06", "wn_07", "sauer2_1", "sauer2_2", "sauer2_3", "sauer2_4",
			"sauer2_5", "sauer2_6", "sauer2_7");
	
	public static Save save;

	public static void init()
	{
		save = read();
	}

	public static void startLevelFresh()
	{
		reset();
	}
	
	public static void reset()
	{
		save.lifes = 3;
		save.playerState = Maryo.MaryoState.small;
		save.item = null;
	}

	public static Save read()
	{
		//read from prefs and deserialize to save
		return Save.readFromString(PrefsManager.getSaveGame());
	}
	
	public static void save()
	{
		// serialize save game and store to prefs
		PrefsManager.setSaveGame(Save.writeToString(save));
	}
	
	public static void dispose()
	{
		save();
		save = null;
	}

    public static boolean isUnlocked(String levelName)
    {
        return save.unlockedLevels.contains(levelName);
        //return true;
    }

    /**
     * Get next level, or null if no more levels*/
    public static String getNextLevel(String currentLevel)
    {
        for(int i = 0; i < LEVELS.size(); i++)
        {
            String level = LEVELS.get(i);
            if(level.equals(currentLevel))
            {
                if(i + 1 < LEVELS.size())
                {
                    String nextLevel = LEVELS.get(i + 1);
                    save.unlockedLevels.add(nextLevel);
                    save();
                    return nextLevel;
                }
                return null;
            }
        }
        return null;
    }
	
	public static class Save
	{
		//persistent
		Set<String> unlockedLevels;
		public int coins;
		public int points;
		
		//in memory only
		public Maryo.MaryoState playerState = Maryo.MaryoState.small;
		public int lifes;
		public Item item;
		
		
		//copy constructor, only persistent objects are copied
		public Save(Save save)
		{
			unlockedLevels = save.unlockedLevels;
		}
		
		public Save()
		{
			unlockedLevels = new HashSet<>(LEVELS.size());
		}
		
		public static Save readFromString(String serializedSave)
		{
			if(serializedSave == null) return new Save();
			serializedSave = Utility.base64Decode(serializedSave);
			HashMap<String, String> map = new HashMap<String, String>();
			String[] values = serializedSave.split("\n");
			for(String s : values)
			{
				String[] keyValue = s.split("=");
				if(keyValue.length != 2)continue;
				map.put(keyValue[0], keyValue[1]);
			}
			Save save = new Save();
            String uLevls = map.get("unlocked_levels");
            if(uLevls != null)
            {
                String[] unlockedLevels = uLevls.split(",");
                Collections.addAll(save.unlockedLevels, unlockedLevels);
            }
			save.points = Utility.parseInt(map.get("points"), 0);
			save.coins = Utility.parseInt(map.get("coins"), 0);
			return save;
		}

		public static String writeToString(Save save)
		{
			if(save == null)return  null;

            StringBuilder builder = new StringBuilder();
            builder.append("unlocked_levels=");

            for(String level : save.unlockedLevels)
            {
                builder.append(level).append(",");
            }
			builder.append("\n").append("points=").append(save.points);
			builder.append("\n").append("coins=").append(save.coins);
			
			return Utility.base64Encode(builder.toString());
		}
	}
}
