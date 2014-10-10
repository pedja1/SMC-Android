package rs.pedjaapps.smc.model;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;

/**
 * Created by pedja on 18.5.14..
 */
public abstract class GameObject
{
    public Rectangle bounds = new Rectangle();//used for draw
    public Rectangle body = new Rectangle();//used for collision detection
	public Vector3 position = new Vector3();
    protected Vector3 velocity = new Vector3();
    protected Vector3 acceleration = new Vector3();
    protected World world;
    boolean isFront = false;// is sprite drawn after player, so that it appears like player walks behind it
    
	public enum WorldState
    {
        IDLE, WALKING, JUMPING, DYING, DUCKING
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
        walk_left, walk_right
    }

    public GameObject(World world, Vector2 size, Vector3 position)
    {
        this.bounds = new Rectangle(position.x, position.y, size.x, size.y);
        body = new Rectangle(bounds);
		this.position = position;
        this.world = world;
    }
	
	public void updateBounds()
    {
        bounds.x = body.x;
        bounds.y = body.y;
    }

    public Rectangle getBounds()
    {
        return bounds;
    }

    public void setBounds(Rectangle bounds)
    {
        this.bounds = bounds;
    }

    public boolean isFront()
    {
        return isFront;
    }

    public void setFront(boolean isFront)
    {
        this.isFront = isFront;
    }
	
	public Vector3 getPosition()
    {
        return position;
    }

    public void setPosition(Vector3 position)
    {
        this.position = position;
    }

    public Vector3 getVelocity()
    {
        return velocity;
    }

    public void setVelocity(Vector3 velocity)
    {
        this.velocity = velocity;
    }

    public void setVelocity(float x, float y)
    {
        velocity.x = x;
		velocity.y = y;
    }

    public Rectangle getBody()
    {
        return body;
    }

    public void setBody(Rectangle body)
    {
        this.body = body;
    }

    public abstract void render(SpriteBatch spriteBatch);
    public abstract void update(float delta);
    public abstract void loadTextures();
}
