package rs.pedjaapps.smc.object;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pedja on 1/31/14.
 */
public class Level
{
    public float width;
    public float height;
    public List<GameObject> gameObjects;
    public Vector3 spanPosition;
	public Background bg1;
	public Background bg2;
    public BackgroundColor bgColor;
    public Array<String> music;
	public String levelName;

	public static final String LEVEL_EXT = ".smclvl";

	public static final String[] levels = {/*"test",*/ "lvl_1", "lvl_2", "lvl_3", "lvl_4", "lvl_5", "lvl_6", "lvl_7", "lvl_8", "lvl_9", "lvl_10"};

	public Level(String levelName)
	{
		this.gameObjects = new ArrayList<>(5000);//set initial capacity so that we avoid alloc during game loop
		this.levelName = levelName;
	}
}
