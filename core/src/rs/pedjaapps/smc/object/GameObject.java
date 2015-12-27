package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by pedja on 18.5.14..
 */
public abstract class GameObject
{
    public Rectangle mDrawRect;//used for draw
    public Rectangle mColRect;//used for collision detection
	public Vector3 position;
    public Vector3 velocity;
    public Vector3 acceleration;
    protected World world;
    public boolean isFront = false;// is sprite drawn after player, so that it appears like player walks behind it
    public float mRotationX, mRotationY, mRotationZ;//degrees
    
	public enum WorldState
    {
        WALKING, JUMPING, DYING, DUCKING
    }

    public enum TKey
    {
        stand_right("stand_right"),
        walk_right_1("walk_right-1"),
        walk_right_2("walk_right-2"),
        jump_right("jump_right"),
        fall_right("fall_right"),
        dead_right("dead_right"),
        duck_right("duck_right"),
        climb_left("climb_left"),
        climb_right("climb_right"),
        throw_right_1("throw_right_1"),
        throw_right_2("throw_right_2"),
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
        walk, _throw
    }

    public GameObject(World world, Vector2 size, Vector3 position)
    {
        this.mDrawRect = World.RECT_POOL.obtain().set(position.x, position.y, size.x, size.y);
        mColRect = World.RECT_POOL.obtain().set(mDrawRect);
		this.position = position;
        this.world = world;
        velocity = World.VECTOR3_POOL.obtain();
        acceleration = World.VECTOR3_POOL.obtain();
    }
	
	public void updateBounds()
    {
        mDrawRect.x = mColRect.x;
        mDrawRect.y = mColRect.y;
    }

    public abstract void _render(SpriteBatch spriteBatch);
    public abstract void _update(float delta);
    public abstract void initAssets();

    public void dispose()
    {
        World.RECT_POOL.free(mDrawRect);
        World.RECT_POOL.free(mColRect);
        World.VECTOR3_POOL.free(position);
        World.VECTOR3_POOL.free(acceleration);
        World.VECTOR3_POOL.free(velocity);
    }

    /**whether this object acts as bullet when hitting other objects (enemies, mario)*/
    public boolean isBullet()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "GameObject{" +
                "\n\tmDrawRect=" + mDrawRect +
                "\n\t mColRect=" + mColRect +
                "\n\t position=" + position +
                "\n\t velocity=" + velocity +
                "\n\t acceleration=" + acceleration +
                "\n\t world=" + world +
                "\n\t isFront=" + isFront +
                "\n\t mRotationX=" + mRotationX +
                "\n\t mRotationY=" + mRotationY +
                "\n\t mRotationZ=" + mRotationZ +
                "\n}";
    }
}
