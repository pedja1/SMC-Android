package rs.pedjaapps.smc.object;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.Constants;

public abstract class DynamicObject extends GameObject
{
	public float stateTime;
	
	private static final float ACCELERATION     = 20f;
    protected static final float DEF_MAX_VEL = 4f;
    protected static final float DEF_VEL_DUMP = .9f;

	public boolean grounded = false;

    long lasHitSoundPlayed;

    protected float groundY;

    public enum Direction
    {
        right, left
    }

    protected float velocityDump = DEF_VEL_DUMP;
	
	public DynamicObject(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
    }
	
	protected void updatePosition(float deltaTime)
	{
		velocity.scl(deltaTime);

		position.add(velocity);
        mColRect.x = position.x;
        mColRect.y = position.y;
        updateBounds();

		velocity.scl(1 / deltaTime);
	}
	
	@Override
    public void _update(float delta)
    {
        // Setting initial vertical acceleration 
        acceleration.y = Constants.GRAVITY;

        // Convert acceleration to frame time
        acceleration.scl(delta);

        // apply acceleration to change velocity
        velocity.add(acceleration);

        // checking collisions with the surrounding blocks depending on Bob's velocity
        checkCollisionWithBlocks(delta);

        // apply damping to halt Maryo nicely 
        velocity.x *= velocityDump;

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

        // update position
        position.add(velocity);
        mColRect.x = position.x;
        mColRect.y = position.y;
        updateBounds();

        // un-scale velocity (not in frame time)
        velocity.scl(1 / delta);
    }

    protected boolean checkY()
    {
        boolean collides = false;
        // the same thing but on the vertical Y axis
        Rectangle rect = World.RECT_POOL.obtain();
        rect.set(mColRect.x, 0, mColRect.width, mColRect.y);
        float tmpGroundY = 0;
        float distance = mColRect.y;
        float oldY = mColRect.y;

        mColRect.y += velocity.y;

        List<GameObject> surroundingObjects = world.level.gameObjects;//world.getSurroundingObjects(this, 1);
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < surroundingObjects.size(); i++)
        //for (GameObject object : surroundingObjects)
        {
            GameObject object = surroundingObjects.get(i);
            if (object == null) continue;
            if (mColRect.overlaps(object.mColRect, this instanceof Maryo))
            {
                boolean tmp = handleCollision(object, true);
                if(tmp)
                    collides = true;
            }
            else if ((object instanceof Box && ((Box) object).itemObject != null && mColRect.overlaps(((Box) object).itemObject.mColRect)))
            {
                boolean tmp = handleCollision(((Box) object).itemObject, true);
                if(tmp)
                    collides = true;
            }

            //checkGround
            if (object instanceof Sprite
                    && (((Sprite) object).type == Sprite.Type.massive || ((Sprite) object).type == Sprite.Type.halfmassive)
                    && rect.overlaps(object.mColRect))
            {
                if (((Sprite) object).type == Sprite.Type.halfmassive && oldY < object.mColRect.y + object.mColRect.height)
                {
                    continue;
                }
                float tmpDistance = oldY - (object.mColRect.y + object.mColRect.height);
                if (tmpDistance < distance)
                {
                    distance = tmpDistance;
                    tmpGroundY = object.mColRect.y + object.mColRect.height;
                }
            }
        }
        groundY = tmpGroundY;
        World.RECT_POOL.free(rect);
        if (mColRect.y < 0)
        {
            boolean tmp = handleDroppedBelowWorld();
            if(tmp)
                collides = true;
        }

        // reset the collision box's position on Y
        mColRect.y = position.y;
        return collides;
    }

    protected boolean checkX()
    {
        boolean collides = false;
        // we first check the movement on the horizontal X axis

        // simulate maryos's movement on the X
        mColRect.x += velocity.x;

        List<GameObject> surroundingObjects = world.level.gameObjects;//world.getSurroundingObjects(this, 1);
        // if m collides, make his horizontal velocity 0
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < surroundingObjects.size(); i++)
        //for (GameObject object : surroundingObjects)
        {
            GameObject object = surroundingObjects.get(i);
            if (object == null) continue;
            if (mColRect.overlaps(object.mColRect, this instanceof Maryo))
            {
                boolean tmp = handleCollision(object, false);
                if(tmp)
                    collides = true;
            }
            else if ((object instanceof Box && ((Box) object).itemObject != null && mColRect.overlaps(((Box) object).itemObject.mColRect)) && !((Box)object).itemObject.popFromBox)
            {
                boolean tmp = handleCollision(((Box) object).itemObject, false);
                if(tmp)
                    collides = true;
            }
        }
        if (mColRect.x < 0 || mColRect.x + mColRect.width > world.level.width)
        {
            velocity.x = 0;
            collides = true;
        }

        // reset the x position of the collision box
        mColRect.x = position.x;
        return collides;
    }

    protected boolean handleDroppedBelowWorld()
    {
        //TODO for now only prevent it from dropping below
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
                if(velocity.y > 0 && this instanceof Maryo && !(object instanceof Box))
                {
                    if(System.currentTimeMillis() - lasHitSoundPlayed > 200)
                    {
                        Sound sound = Assets.manager.get("data/sounds/wall_hit.wav");
                        if (sound != null && Assets.playSounds)
                        {
                            sound.play();
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
				velocity.x = 0;
			}
            return true;
		}
		else if(object instanceof Sprite && ((Sprite)object).type == Sprite.Type.halfmassive)
		{
			if(velocity.y < 0 && position.y > object.position.y + object.mColRect.height)
			{
				grounded = true;
				velocity.y = 0;
                return true;
			}
		}
        return false;
	}

	public abstract float maxVelocity();
}
