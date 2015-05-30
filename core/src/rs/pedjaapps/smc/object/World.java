package rs.pedjaapps.smc.object;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import rs.pedjaapps.smc.screen.AbstractScreen;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.utility.Constants;


public class World
{
	public AbstractScreen screen;
    /**
     * Our player controlled hero *
     */
    public Maryo maryo;
    /**
     * A world has a level through which Mario needs to go through *
     */
    public Level level;
    private Array<GameObject> visibleObjects = new Array<>(50);
    private Array<GameObject> tmpObjects = new Array<>();

	/**
	 * 
	 */
	public final Array<GameObject> trashObjects = new Array<>();
	public final Array<GameObject> newObjects = new Array<>();

	// This is the rectangle pool used in collision detection
	// Good to avoid instantiation each frame
	public Pool<Rectangle> rectPool = new Pool<Rectangle>()
	{
		@Override
		protected Rectangle newObject() 
		{
			return new Rectangle();
		}
	};

    public Pool<Vector2> vector2Pool = new Pool<Vector2>()
    {
        @Override
        protected Vector2 newObject()
        {
            return new Vector2();
        }
    };
    
    /**
     * Return only the blocks that need to be drawn *
     * 
     */
    public Array<GameObject> getDrawableObjects(float camX, float camY/*, boolean getFront*/)
    {
        visibleObjects.clear();
        float wX = camX - Constants.CAMERA_WIDTH / 2 - 1;
        float wY = camY - Constants.CAMERA_HEIGHT / 2 - 1;
        float wW = Constants.CAMERA_WIDTH + 1;
        float wH = Constants.CAMERA_HEIGHT + 1;
        Rectangle worldBounds = rectPool.obtain();
		worldBounds.set(wX, wY, wW, wH);
        //for (GameObject object : level.gameObjects)
        for (int i = 0; i < level.gameObjects.size(); i++)
        {
            GameObject object = level.gameObjects.get(i);
            Rectangle bounds = object.bounds;
            if (bounds.overlaps(worldBounds)/* || object instanceof Enemy*/)
            {
                visibleObjects.add(object);
            }
        }
        rectPool.free(worldBounds);
        return visibleObjects;
    }

	public Array<GameObject> getSurroundingObjects(GameObject center, float offset)
    {
        tmpObjects.clear();
        float wX = center.body.x - offset;
        float wY = center.body.y - offset;
        float wW = center.body.x + center.body.width + offset * 2;
        float wH = center.body.y + center.body.height + offset * 2;
        Rectangle offsetBounds = rectPool.obtain();
		offsetBounds.set(wX, wY, wW, wH);
        for (GameObject object : level.gameObjects)
        {
            Rectangle bounds = object.bounds;
            if (bounds.overlaps(offsetBounds)/* || object instanceof Enemy*/)
            {
                tmpObjects.add(object);
            }
        }
        rectPool.free(offsetBounds);
        return tmpObjects;
    }
	
	public Rectangle createMaryoRectWithOffset(float offset)
	{
		float wX = maryo.body.x - offset;
        float wY = maryo.body.y - offset;
        float wW = maryo.body.x + maryo.body.width + offset * 2;
        float wH = maryo.body.y + maryo.body.height + offset * 2;
        Rectangle offsetBounds = rectPool.obtain();
		offsetBounds.set(wX, wY, wW, wH);
     	return offsetBounds;
	}
	
    // --------------------
    public World(AbstractScreen screen)
    {
		this.screen = screen;
    }

    public Array<GameObject> getVisibleObjects()
    {
        return visibleObjects == null ? new Array<GameObject>() : visibleObjects;
    }

    /**
     * Check if obejct is visible in current camera bounds
     * @param fallback value to return if cant determine if object is visible
     *
     */
    public boolean isObjectVisible(GameObject object, boolean fallback)
    {
        if(!(screen instanceof GameScreen))return fallback;
        float camX = ((GameScreen) screen).cam.position.x;
        float camY = ((GameScreen) screen).cam.position.y;;
        float wX = camX - Constants.CAMERA_WIDTH / 2 - 1;
        float wY = camY - Constants.CAMERA_HEIGHT / 2 - 1;
        float wW = Constants.CAMERA_WIDTH + 1;
        float wH = Constants.CAMERA_HEIGHT + 1;
        Rectangle worldBounds = rectPool.obtain();
        worldBounds.set(wX, wY, wW, wH);
        boolean result = object.bounds.overlaps(worldBounds);
        rectPool.free(worldBounds);
        return result;
    }
}
