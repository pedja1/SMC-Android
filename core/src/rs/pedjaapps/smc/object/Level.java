package rs.pedjaapps.smc.object;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import java.util.*;
import rs.pedjaapps.smc.view.*;

/**
 * Created by pedja on 1/31/14.
 */
public class Level
{
    public float width;
    public float height;
    public List<GameObject> gameObjects;
    public Vector3 spanPosition;
	public Background background;
    public Array<String> music;
	public String levelName;

	public static final String LEVEL_EXT = ".smclvl";

	public Level(String levelName)
	{
		this.gameObjects = new ArrayList<>(5000);//set initial capacity so that we avoid alloc during game loop
		this.levelName = levelName;
	}

	public void dispose()
	{
		for(GameObject go : gameObjects)
		{
			go.dispose();
		}
		gameObjects = null;
		background.dispose();
		background = null;
	}
}
