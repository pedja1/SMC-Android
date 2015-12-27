package rs.pedjaapps.smc.object;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.view.Background;
import rs.pedjaapps.smc.view.Parallax;

/**
 * Created by pedja on 1/31/14.
 */
public class Level
{
    public Array<GameObject> gameObjects;
    public Vector3 spanPosition;
	public Background background;
	public Parallax parallaxClouds, parallaxGround1, parallaxGround2;
    public String music;

	public static final String LEVEL_EXT = ".smclvl";

	public Level()
	{
		this.gameObjects = new Array<>(1000);//set initial capacity so that we avoid alloc during game loop
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
		parallaxClouds.dispose();
		parallaxClouds = null;
		parallaxGround1.dispose();
		parallaxGround1 = null;
		parallaxGround2.dispose();
		parallaxGround2 = null;
	}
}
