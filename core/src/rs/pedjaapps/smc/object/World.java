package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

import rs.pedjaapps.smc.object.items.Coin;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.screen.AbstractScreen;
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
    public Level level = new Level();

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
            return new Sprite(World.this, new Vector3(), 0, 0, null);
        }
    };

    public Pool<Coin> COIN_POOL = new Pool<Coin>()
    {
        @Override
        protected Coin newObject()
        {
            return new Coin(World.this, new Vector3(), 0, 0);
        }
    };

	public Rectangle createMaryoRectWithOffset(OrthographicCamera cam, float offset)
	{
        float offsetX = Math.max(offset, Constants.CAMERA_WIDTH/*(cam.viewportWidth * cam.zoom)*/);
        float offsetY = Math.max(offset * 0.5f, Constants.CAMERA_HEIGHT/*(cam.viewportHeight * cam.zoom)*/);
		float wX = maryo.mColRect.x - offsetX;
        float wY = maryo.mColRect.y - offsetY;
        float wW = maryo.mColRect.x + maryo.mColRect.width + offsetX * 2;
        float wH = maryo.mColRect.y + maryo.mColRect.height + offsetY * 2;
        Rectangle offsetBounds = RECT_POOL.obtain();
		offsetBounds.set(wX, wY, wW, wH);
     	return offsetBounds;
	}
	
    // --------------------
    public World(AbstractScreen screen)
    {
		this.screen = screen;
    }

    public void dispose()
    {
        level.dispose();
        maryo.dispose();
        maryo = null;
    }
}
