package rs.pedjaapps.smc;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import rs.pedjaapps.smc.object.*;

public class LevelEditor extends Game
{
	private String[] args;

	public LevelEditor(String[] args)
	{
		this.args = args;
	}

	@Override
	public void create()
	{
		Array<GameObject> gameObjects = null;
		FileHandle saveFile;
		if(args.length > 0)
		{
			saveFile = Gdx.files.absolute(args[0]);
		}
		else
		{
			saveFile = Gdx.files.absolute("level.json");
		}
		if (saveFile.exists())
		{
			String json = saveFile.readString();
			gameObjects = parseLevelFromJSON(json);
		}
		System.out.println("Save File path: " + saveFile.file().getAbsolutePath());
		setScreen(new LevelEditorScreen(gameObjects, saveFile));
	}

	private static Array<GameObject> parseLevelFromJSON(String jsonString)
	{
		Json json = new Json();
		return json.fromJson(LevelRegion.class, jsonString).gameObjects;
	}


	@Override
	public void resume()
	{
	}

	@Override
	public void pause()
	{

	}

    @Override
    public void dispose()
    {
        super.dispose();
    }

}
