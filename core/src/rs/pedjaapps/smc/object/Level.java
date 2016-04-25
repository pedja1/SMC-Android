package rs.pedjaapps.smc.object;

import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.view.Background;
import rs.pedjaapps.smc.view.Parallax;

/**
 * Created by pedja on 1/31/14.
 */
public class Level
{
    public Array<GameObject> gameObjects;
	public Background backgroundColor, background, background2, background3;
	public Parallax parallaxClouds, parallaxGround;
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
		background2.dispose();
		background2 = null;
		background3.dispose();
		background3 = null;
		backgroundColor.dispose();
		backgroundColor = null;
		parallaxClouds.dispose();
		parallaxClouds = null;
		parallaxGround.dispose();
		parallaxGround = null;
	}
}
