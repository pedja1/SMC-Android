package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import rs.pedjaapps.smc.Rect;
import rs.pedjaapps.smc.screen.AbstractScreen;

/**
 * Created by pedja on 18.5.14..
 */
public abstract class GameObject
{
    public Rect mDrawRect = new Rect();//used for draw
    public Rect mColRect = new Rect();//used for collision detection
	public Vector3 position = new Vector3();
	public Vector3 prevPosition = new Vector3();
	public Vector3 interpPosition = new Vector3();
    public Vector3 velocity = new Vector3();
    public Vector3 acceleration = new Vector3();
    protected World world;
    public boolean isFront = false;// is sprite drawn after player, so that it appears like player walks behind it
    
	public enum WorldState
    {
        IDLE, WALKING, JUMPING, DYING, DUCKING, CLIMBING
    }

    public enum TKey
    {
        stand_right("stand-right"),
        walk_right_1("walk-right-1"),
        walk_right_2("walk-right-2"),
        stand_left("stand-left"),
        jump_right("jump-right"),
        jump_left("jump-left"),
        fall_right("fall-right"),
        fall_left("fall-left"),
        dead_right("dead-right"),
        dead_left("dead-left"),
        duck_right("duck-right"),
        duck_left("duck-left"),
        climb_left("climb-left"),
        climb_right("climb-right"),
        one("1"),
        two("2"),
        three("3"),;

        String mValue;
        TKey(String value)
        {
            mValue = value;
        }

        @Override
        public String toString()
        {
            return mValue;
        }
    }

    public enum AKey
    {
        walk_left, walk_right, climb
    }

    public GameObject(World world, Vector2 size, Vector3 position)
    {
        this.mDrawRect = new Rect(position.x, position.y, size.x, size.y);
        mColRect = new Rect(mDrawRect);
		this.position = position;
        this.interpPosition.set(position);
        this.world = world;
    }
	
	public void updateBounds()
    {
        mDrawRect.x = world.screen.getTimeStep() == AbstractScreen.FIXED_TIMESTEP ? interpPosition.x : mColRect.x;
        mDrawRect.y = world.screen.getTimeStep() == AbstractScreen.FIXED_TIMESTEP ? interpPosition.y : mColRect.y;
    }

    public abstract void render(SpriteBatch spriteBatch);
    public abstract void update(float delta);
    public abstract void initAssets();
}
