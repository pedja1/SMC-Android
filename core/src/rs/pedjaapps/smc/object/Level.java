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
    public Vector3 spanPosition;//i think its a camera position
	public Background bg1;
	public Background bg2;
    public BackgroundColor bgColor;
    public Array<String> music;
	
	public static final String[] levels = {"data/"};

	public Level()
	{
		this.gameObjects = new ArrayList<GameObject>();
	}
}
