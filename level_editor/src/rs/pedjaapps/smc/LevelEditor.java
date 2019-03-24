package rs.pedjaapps.smc;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.util.List;

import rs.pedjaapps.smc.object.*;
import rs.pedjaapps.smc.utility.LevelLoader;

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
		List<GameObject> gameObjects = null;
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
			LevelLoader levelLoader = new LevelLoader("level.json");
			levelLoader.parseLevel(new World());
			gameObjects = levelLoader.level.gameObjects;
		}
		System.out.println("Save File path: " + saveFile.file().getAbsolutePath());
		setScreen(new LevelEditorScreen(gameObjects, saveFile));
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