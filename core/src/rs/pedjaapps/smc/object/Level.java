package rs.pedjaapps.smc.object;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rs.pedjaapps.smc.utility.LevelLoader;
import rs.pedjaapps.smc.view.Background;

/**
 * Created by pedja on 1/31/14.
 */
public class Level
{
    public float width;
    public float height;
    private List<GameObject> gameObjects;
    public List<GameObject> collisionObjects;
    public Vector3 spanPosition;
	public Background background;
    public Array<String> music;
	public String levelName;

	public static final String LEVEL_EXT = ".smclvl";

	public Level(String levelName)
	{
		this.gameObjects = new ArrayList<>(5000);//set initial capacity so that we avoid alloc during game loop
		this.collisionObjects = new ArrayList<>(1000);//set initial capacity so that we avoid alloc during game loop
		this.levelName = levelName;
	}

	private boolean isCollidable(GameObject go)
	{
		return go instanceof DynamicObject || go instanceof Box
				|| go instanceof LevelEntry || go instanceof LevelExit
				|| (go instanceof Sprite && (((Sprite)go).type == Sprite.Type.halfmassive || ((Sprite)go).type == Sprite.Type.massive));
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

	public void sort()
	{
		Collections.sort(gameObjects, new LevelLoader.ZSpriteComparator());
	}

	public void add(GameObject go)
	{
		gameObjects.add(go);
		if(isCollidable(go))
			collisionObjects.add(go);
	}

	public void remove(GameObject go)
	{
		gameObjects.remove(go);
		if(isCollidable(go))
			collisionObjects.remove(go);
	}

	public int cSize()
	{
		return collisionObjects.size();
	}

	public int gSize()
	{
		return gameObjects.size();
	}

	public GameObject cGet(int location)
	{
		return collisionObjects.get(location);
	}

	public GameObject gGet(int location)
	{
		return gameObjects.get(location);
	}
}
