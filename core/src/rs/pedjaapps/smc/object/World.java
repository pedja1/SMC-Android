package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private Rectangle worldBounds = new Rectangle();

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
    public void drawVisibleObjects(OrthographicCamera cam, SpriteBatch batch)
    {
        visibleObjects.clear();
        setRectToVisibleCamArea(worldBounds, cam);
        for (int i = 0, size = level.gameObjects.size(); i < size; i++)
        {
            GameObject object = level.gameObjects.get(i);
            Rectangle bounds = object.mDrawRect;
            if (bounds.overlaps(worldBounds))
            {
                visibleObjects.add(object);
                object._render(batch);
            }
        }
    }

    public static void setRectToVisibleCamArea(Rectangle worldBounds, OrthographicCamera cam) {
        float camX = cam.position.x;
        float camY = cam.position.y;
        float camWidth = (cam.viewportWidth * cam.zoom);
        float camHeight = (cam.viewportHeight * cam.zoom);
        float wX = camX - camWidth * 0.5f - 1;
        float wY = camY - camHeight * 0.5f - 1;
        float wW = camWidth + 1;
        float wH = camHeight + 1;
        worldBounds.set(wX, wY, wW, wH);
    }

    public void createMaryoRectWithOffset(Rectangle offsetBounds, float offset)
	{
        float offsetX = Math.max(offset, Constants.CAMERA_WIDTH);
        float offsetY = Math.max(offset * 0.5f, Constants.CAMERA_HEIGHT);
		float wX = maryo.mColRect.x - offsetX;
        float wY = maryo.mColRect.y - offsetY;
        float wW = maryo.mColRect.x + maryo.mColRect.width + offsetX * 2;
        float wH = maryo.mColRect.y + maryo.mColRect.height + offsetY * 2;
		offsetBounds.set(wX, wY, wW, wH);
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
     *
     */
    public boolean isObjectVisible(GameObject object, OrthographicCamera cam)
    {
        setRectToVisibleCamArea(worldBounds, cam);
        return object.mDrawRect.overlaps(worldBounds);
    }

    public void dispose()
    {
        level.dispose();
        maryo.dispose();
        maryo = null;
    }
}
