package rs.pedjaapps.smc.object;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

import rs.pedjaapps.smc.object.items.Coin;
import rs.pedjaapps.smc.utility.Constants;


public class World
{
    private static World instance;

	public Screen screen;
    /**
     * Our player controlled hero *
     */
    public Player player;
    /**
     * A world has a level through which Mario needs to go through *
     */
    public Level level;

	/**
	 * 
	 */

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

    public static Pool<Color> COLOR_POOL = new Pool<Color>()
    {
        @Override
        protected Color newObject()
        {
            return new Color();
        }
    };

    public Pool<Sprite> SPRITE_POOL = new Pool<Sprite>()
    {
        @Override
        protected Sprite newObject()
        {
            return new Sprite(0, 0, 0, 0);
        }
    };

    public Pool<Coin> COIN_POOL = new Pool<Coin>()
    {
        @Override
        protected Coin newObject()
        {
            return new Coin(0, 0, 0, 0, false);
        }
    };

	public Rectangle createMaryoRectWithOffset(OrthographicCamera cam, float offset)
	{
        float offsetX = Math.max(offset, Constants.CAMERA_WIDTH/*(cam.viewportWidth * cam.zoom)*/);
        float offsetY = Math.max(offset * 0.5f, Constants.CAMERA_HEIGHT/*(cam.viewportHeight * cam.zoom)*/);
		float wX = player.collider.x - offsetX;
        float wY = player.collider.y - offsetY;
        float wW = player.collider.x + player.collider.width + offsetX * 2;
        float wH = player.collider.y + player.collider.height + offsetY * 2;
        Rectangle offsetBounds = RECT_POOL.obtain();
		offsetBounds.set(wX, wY, wW, wH);
     	return offsetBounds;
	}
	
    // --------------------
    private World(Screen screen)
    {
		this.screen = screen;
        level = new Level();
    }

    public static World getInstance()
    {
        if(instance == null)
            throw new IllegalStateException("Create instance first, call create(Screen)");
        return instance;
    }

    public static void create(Screen screen)
    {
        if(instance != null)
            throw new IllegalStateException("You can only create one instance. Call dispose first");
        instance = new World(screen);
    }

    public void dispose()
    {
        level.dispose();
        player.dispose();
        player = null;
    }
}
