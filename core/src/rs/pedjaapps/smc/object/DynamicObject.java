package rs.pedjaapps.smc.object;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.PrefsManager;

public abstract class DynamicObject extends GameObject
{
	protected float stateTime;
	
	private static final float ACCELERATION     = 20f;
    protected static final float DEF_MAX_VEL = 4f;
    protected static final float DEF_VEL_DUMP = .9f;

	public boolean grounded = false;

    long lasHitSoundPlayed;

    public Vector2 velocity;
    public Vector2 acceleration;

    protected float groundY;

    public DynamicObject(float x, float y, float width, float height)
    {
        super(x, y, width, height);
        velocity = new Vector2(0, 0);
        acceleration = new Vector2(0, 0);
    }

    public DynamicObject()
    {
    }

    public enum Direction
    {
        right, left
    }

    protected float velocityDump = DEF_VEL_DUMP;
    protected GameObject closestObject = null;

	@Override
    protected void _update(float delta)
    {
        // Setting initial vertical acceleration 
        acceleration.y = Constants.GRAVITY;

        // Convert acceleration to frame time
        acceleration.scl(delta);

        // apply acceleration to change velocity
        velocity.add(acceleration);

        // checking collisions with the surrounding blocks depending on Maryo's velocity
        checkCollisionWithBlocks(delta);

        // apply damping to halt Maryo nicely 
        if(!(this instanceof Player))velocity.x *= velocityDump;

        // ensure terminal velocity is not exceeded
        //x
        if (velocity.x > maxVelocity())
            velocity.x = maxVelocity();
        if (velocity.x < -maxVelocity())
            velocity.x = -maxVelocity();

        //y
        /*if (velocity.y < Constants.GRAVITY) {
            velocity.y = Constants.GRAVITY;
        }*/

        stateTime += delta;
    }

    protected void checkCollisionWithBlocks(float delta)
    {
        checkCollisionWithBlocks(delta, true, true);
    }

    protected void checkCollisionWithBlocks(float delta, boolean checkX, boolean checkY)
    {
        checkCollisionWithBlocks(delta, checkX, checkY, true, true);
    }

    /** Collision checking **/
    protected void checkCollisionWithBlocks(float delta, boolean checkX, boolean checkY, boolean xFirst, boolean checkSecondIfFirstCollides)
    {
        // scale velocity to frame units 
        velocity.scl(delta);

        if(xFirst)
        {
            boolean first = false;
            if (checkX)
            {
                first = checkX();
            }

            if (checkY)
            {
                if(!checkSecondIfFirstCollides)
                {
                    if(!first)
                    {
                        checkY();
                    }
                }
                else
                {
                    checkY();
                }
            }
        }
        else
        {
            boolean first = false;
            if (checkY)
            {
                first = checkY();
            }

            if (checkX)
            {
                if(!checkSecondIfFirstCollides)
                {
                    if(!first)
                    {
                        checkX();
                    }
                }
                else
                {
                    checkX();
                }
            }
        }

		/*if(this instanceof Maryo && Math.abs(velocity.x) > 0.09f)
		{
			System.out.println("physics warning - vel.x: " + velocity.x + ", delta: " + delta);
		}*/
        // update position
        position.add(velocity);

        // un-scale velocity (not in frame time)
        velocity.scl(1 / delta);
    }

    protected boolean checkY()
    {
        boolean collides = false;
        // the same thing but on the vertical Y axis
        Rectangle rect = World.RECT_POOL.obtain();
        rect.set(collider.x, 0, collider.width, collider.y);
        float tmpGroundY = 0;
        float distance = collider.y;
        float oldY = collider.y;

        float tmpY = collider.y;
        collider.y += velocity.y;

        Array<GameObject> surroundingObjects = World.getInstance().level.gameObjects;//world.getSurroundingObjects(this, 1);
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = surroundingObjects.size; i < size; i++)
        //for (GameObject object : surroundingObjects)
        {
            GameObject object = surroundingObjects.get(i);
            if (object == null || collider == null || object.collider == null) continue;
            if (collider.overlaps(object.collider))
            {
                boolean tmp = handleCollision(object, true);
                if(tmp)
                    collides = true;
            }

            //checkGround
            if (object instanceof Sprite
                    && (((Sprite) object).type == Sprite.Type.massive || ((Sprite) object).type == Sprite.Type.halfmassive)
                    && rect.overlaps(object.collider))
            {
                if (((Sprite) object).type == Sprite.Type.halfmassive && oldY < object.collider.y + object.collider.height)
                {
                    continue;
                }
                float tmpDistance = oldY - (object.collider.y + object.collider.height);
                if (tmpDistance < distance)
                {
                    distance = tmpDistance;
                    tmpGroundY = object.collider.y + object.collider.height;
                    closestObject = object;
                }
            }
        }
        groundY = tmpGroundY;
        World.RECT_POOL.free(rect);
        if (collider.y < 0)
        {
            boolean tmp = handleDroppedBelowWorld();
            if(tmp)
                collides = true;
        }

        // reset the collision box's position on Y
        collider.y = tmpY;
        return collides;
    }

    protected boolean checkX()
    {
        boolean collides = false;
        // we first check the movement on the horizontal X axis

        // simulate movement on the X
        float tmpX = collider.x;
        collider.x += velocity.x;

        Array<GameObject> surroundingObjects = World.getInstance().level.gameObjects;//world.getSurroundingObjects(this, 1);
        // if m collides, make his horizontal velocity 0
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = surroundingObjects.size; i < size; i++)
        //for (GameObject object : surroundingObjects)
        {
            GameObject object = surroundingObjects.get(i);
            if (object == null || collider == null || object.collider == null) continue;
            if (collider.overlaps(object.collider))
            {
                boolean tmp = handleCollision(object, false);
                if(tmp)
                    collides = true;
            }
        }

        // reset the x position of the collision box
        collider.x = tmpX;
        return collides;
    }

    protected boolean handleDroppedBelowWorld()
    {
        if (velocity.y < 0)
        {
            grounded = true;
        }
        velocity.y = 0;
        return false;
    }

    protected boolean handleCollision(GameObject object, boolean vertical)
	{
		if(object instanceof Sprite && ((Sprite)object).type == Sprite.Type.massive)
		{
			if(vertical)
			{
                if(velocity.y > 0 && this instanceof Player)
                {
                    if(System.currentTimeMillis() - lasHitSoundPlayed > 200)
                    {
                        Sound sound = Assets.manager.get("data/sounds/wall_hit.mp3");
                        if (sound != null && PrefsManager.isPlaySounds())
                        {
                            SoundManager.play(sound);
                            lasHitSoundPlayed = System.currentTimeMillis();
                        }
                    }
                }
				if (velocity.y < 0) 
				{
					grounded = true;
				}
                velocity.y = 0;
			}
			else
			{
                if(this instanceof Player)
                {
                    ((Player)this).die();
                }
                else
                {
                    velocity.x = 0;
                }
			}
            return true;
		}
		else if(object instanceof Sprite && ((Sprite)object).type == Sprite.Type.halfmassive)
		{
			if(velocity.y < 0 && position.y > object.position.y + object.collider.height)
			{
				grounded = true;
				velocity.y = 0;
                return true;
			}
		}
        return false;
	}

	public abstract float maxVelocity();

    @Override
    public void reset()
    {
        super.reset();
        velocity.set(0, 0);
        acceleration.set(0, 0);
    }
}
