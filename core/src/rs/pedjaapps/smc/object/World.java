package rs.pedjaapps.smc.object;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import rs.pedjaapps.smc.object.maryo.Fireball;
import rs.pedjaapps.smc.object.maryo.Iceball;
import rs.pedjaapps.smc.object.maryo.Maryo;
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

	// This is the rectangle pool used in collision detection
	// Good to avoid instantiation each frame
	public static Pool<Rectangle> RECT_POOL = new Pool<Rectangle>()
	{
		@Override
		protected Rectangle newObject()
		{
			return new Rectangle();
		}
	};

    public static Pool<Polygon> POLY_POOL = new Pool<Polygon>()
    {
        @Override
        protected Polygon newObject()
        {
            return new Polygon();
        }
    };

    public static Pool<Vector3> VECTOR3_POOL = new Pool<Vector3>()
    {
        @Override
        protected Vector3 newObject()
        {
            return new Vector3();
        }
    };

    public static Pool<Vector2> VECTOR2_POOL = new Pool<Vector2>()
    {
        @Override
        protected Vector2 newObject()
        {
            return new Vector2();
        }
    };

    public Pool<Fireball> FIREBALL_POOL = new Pool<Fireball>()
    {
        @Override
        protected Fireball newObject()
        {
            Fireball fb = new Fireball(World.this, new Vector3());
            fb.initAssets();
            return fb;
        }
    };

    public Pool<Iceball> ICEBALL_POOL = new Pool<Iceball>()
    {
        @Override
        protected Iceball newObject()
        {
            Iceball fb = new Iceball(World.this, new Vector3());
            fb.initAssets();
            return fb;
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
        Rectangle worldBounds = RECT_POOL.obtain();
		worldBounds.set(wX, wY, wW, wH);
        //for (GameObject object : level.gameObjects)
        for (int i = 0; i < level.gameObjects.size(); i++)
        {
            GameObject object = level.gameObjects.get(i);
            Rectangle bounds = object.mDrawRect;
            if (bounds.overlaps(worldBounds)/* || object instanceof Enemy*/)
            {
                visibleObjects.add(object);
            }
        }
        RECT_POOL.free(worldBounds);
        return visibleObjects;
    }

	public Array<GameObject> getSurroundingObjects(GameObject center, float offset)
    {
        tmpObjects.clear();
        float wX = center.mColRect.x - offset;
        float wY = center.mColRect.y - offset;
        float wW = center.mColRect.x + center.mColRect.width + offset * 2;
        float wH = center.mColRect.y + center.mColRect.height + offset * 2;
        Rectangle offsetBounds = RECT_POOL.obtain();
		offsetBounds.set(wX, wY, wW, wH);
        for (GameObject object : level.gameObjects)
        {
            Rectangle bounds = object.mDrawRect;
            if (bounds.overlaps(offsetBounds)/* || object instanceof Enemy*/)
            {
                tmpObjects.add(object);
            }
        }
        RECT_POOL.free(offsetBounds);
        return tmpObjects;
    }
	
	public Rectangle createMaryoRectWithOffset(float offset)
	{
		float wX = maryo.mColRect.x - offset;
        float wY = maryo.mColRect.y - offset;
        float wW = maryo.mColRect.x + maryo.mColRect.width + offset * 2;
        float wH = maryo.mColRect.y + maryo.mColRect.height + offset * 2;
        Rectangle offsetBounds = RECT_POOL.obtain();
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
        Rectangle worldBounds = RECT_POOL.obtain();
        worldBounds.set(wX, wY, wW, wH);
        boolean result = object.mDrawRect.overlaps(worldBounds);
        RECT_POOL.free(worldBounds);
        return result;
    }
}
