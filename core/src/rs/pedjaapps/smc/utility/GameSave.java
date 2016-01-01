package rs.pedjaapps.smc.utility;

import java.util.HashMap;

import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.object.maryo.Maryo;

public class GameSave
{
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

	public static class Save
	{
		//in memory only
		public int coins;
		public int points;
		public Maryo.MaryoState playerState = Maryo.MaryoState.small;
		public int lifes;
		public Item item;
		
		public Save()
		{

		}
		
		public static Save readFromString(String serializedSave)
		{
			if(serializedSave == null) return new Save();
			serializedSave = Utility.base64Decode(serializedSave);
			HashMap<String, String> map = new HashMap<>();
			String[] values = serializedSave.split("\n");
			for(String s : values)
			{
				String[] keyValue = s.split("=");
				if(keyValue.length != 2)continue;
				map.put(keyValue[0], keyValue[1]);
			}
			Save save = new Save();
			//save.points = Utility.parseInt(map.get("points"), 0);
			//save.coins = Utility.parseInt(map.get("coins"), 0);
			return save;
		}

		public static String writeToString(Save save)
		{
			if(save == null)return  null;

			//noinspection StringBufferReplaceableByString
			StringBuilder builder = new StringBuilder();
			//builder.append("\n").append("points=").append(save.points);
			//builder.append("\n").append("coins=").append(save.coins);

			return Utility.base64Encode(builder.toString());
		}
	}
}
