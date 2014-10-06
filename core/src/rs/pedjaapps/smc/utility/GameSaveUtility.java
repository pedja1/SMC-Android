package rs.pedjaapps.smc.utility;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import rs.pedjaapps.smc.model.Maryo;
import java.util.HashMap;

public class GameSaveUtility
{
	private static GameSaveUtility instance = null;
	
	public Save save;
	
	private GameSaveUtility()
	{
		save = read();
	}

	public void startLevelFresh()
	{
		/*if(save.lifes < 3)*/save.lifes = 3;
	}
	
	public void reset()
	{
		save.coins = 0;
		save.points = 0;
		save.lifes = 0;
		save.playerState = Maryo.MarioState.small;
	}
	
	public static GameSaveUtility getInstance()
	{
		if(instance ==  null)
		{
			instance = new GameSaveUtility();
		}
		return instance;
	}
	
	public Save read()
	{
		//read from prefs and deserialize to save
		return Save.readFromString(PrefsManager.getSaveGame());
	}
	
	public void save()
	{
		// serialize save game and store to prefs
		PrefsManager.setSaveGame(Save.writeToString(save));
	}
	
	public void dispose()
	{
		save();
		save = null;
		instance = null;
	}
	
	
	public static class Save
	{
		//persistent
		public int currentLevel = 0;
		
		//in memory only
		
		public int coins;
		public Maryo.MarioState playerState = Maryo.MarioState.small;
		public int lifes;
		public int points;
		
		//copy constructor, only persistent objects are copied
		public Save(Save save)
		{
			playerState = save.playerState;
			currentLevel = save.currentLevel;
		}
		
		public Save()
		{
			
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
			//String state = map.get("state");
			//save.playerState = state == null ? Maryo.MarioState.small: Maryo.MarioState.valueOf(state);
			save.currentLevel = Utility.parseInt(map.get("level"), 0);
			return save;
		}

		public static String writeToString(Save save)
		{
			if(save == null)return  null;
			String input = /*"state=" + save.playerState.toString()
							+ "\n*/"level=" + save.currentLevel;
			return Utility.base64Encode(input);
		}

		@Override
		public String toString()
		{
			return "state: " + playerState + ", currentLevel: " + currentLevel;
		}
	}
}
