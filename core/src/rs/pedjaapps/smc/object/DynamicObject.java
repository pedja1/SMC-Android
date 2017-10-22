package rs.pedjaapps.smc.object;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.List;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.PrefsManager;

public abstract class DynamicObject extends GameObject
{
    protected static final float DEF_MAX_VEL = 4f;
    protected static final float DEF_VEL_DUMP = .8f;

    // Nachbarn: maximale Geschwindigkeit liegt derzeit bei 4, es kann also pro Sekunde maximal
    // 8 aufeinander zubewegt werden. Gravity ist 20.
    // Bei einem Grenzwert von 5 reicht also Refresh alle 600 ms
    // zur Sicherheit wird alle 400ms refresht
    protected static final float FREQ_REFRESH_NEIGHBOURLIST = .2f;
    protected static final float MAX_VELOCITY = 5;
    protected static final float THRESHOLD_NEIGHBOURS_Y = (-Constants.GRAVITY + MAX_VELOCITY)
            * FREQ_REFRESH_NEIGHBOURLIST * 1.5f;
    protected static final float THRESHOLD_NEIGHBOURS_X = MAX_VELOCITY * 2 * FREQ_REFRESH_NEIGHBOURLIST * 1.5f;

    public float stateTime;
    public boolean grounded = false;
    protected float groundY;
    protected boolean ppEnabled = true;
    protected float velocityDump = DEF_VEL_DUMP;
    protected GameObject closestObject = null;

    protected Array<GameObject> neighbours = new Array<>();
    protected Rectangle tmpRect = new Rectangle();
    protected float neighboursArrayAge = FREQ_REFRESH_NEIGHBOURLIST;

    private long lasHitSoundPlayed;

    public enum Direction {
        right, left
    }

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

        // checking collisions with the surrounding blocks depending on Maryo's velocity
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
        refreshNeighbourList(delta);
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

        if((checkX || checkY) && ppEnabled)
        {
            //List<GameObject> surroundingObjects = world.level.gameObjects;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, size = neighbours.size; i < size; i++)
            {
                GameObject object = neighbours.get(i);
                if (object != null && mColRect.overlaps(object.mColRect) && object instanceof Sprite
                        && ((Sprite) object).type == Sprite.Type.massive)
                {
                    float diffLeft = (mColRect.x + mColRect.width) - object.mColRect.x;
                    float diffRight = (object.mColRect.x + object.mColRect.width) - mColRect.x;
                    float diffTop = (object.mColRect.y + object.mColRect.height) - mColRect.y;
                    float diffBottom = (mColRect.y + mColRect.height) - object.mColRect.y;

                    int smallestIdx = findSmallestDiffIndex(diffLeft, diffRight, diffTop, diffBottom);

                    switch (smallestIdx)
                    {
                        case 0:
                            position.x -= diffLeft;
                            mColRect.x = position.x;
                            break;
                        case 1:
                            position.x += diffRight;
                            mColRect.x = position.x;
                            break;
                        case 2:
                            position.y += diffTop;
                            mColRect.y = position.y;
                            break;
                        case 3:
                            position.y -= diffBottom;
                            mColRect.y = position.y;
                            break;
                    }
                    updateBounds();

                    //example if player stuck on object on his right side below him
                    //diffLeft: 0.019233704
                    //diffRight: 1.040142
                    //diffTop: 0.055658817
                    //diffBottom: 1.3949661
                }
            }
        }
    }

    protected void refreshNeighbourList(float delta) {
        neighboursArrayAge += delta;

        if (neighboursArrayAge >= FREQ_REFRESH_NEIGHBOURLIST) {
            neighboursArrayAge = 0;
            neighbours.clear();
            List<GameObject> surroundingObjects = world.level.gameObjects;
            tmpRect.set(mColRect.x - THRESHOLD_NEIGHBOURS_X, mColRect.y - THRESHOLD_NEIGHBOURS_Y,
                    mColRect.width + 2 * THRESHOLD_NEIGHBOURS_X, mColRect.height + 2 * THRESHOLD_NEIGHBOURS_Y);
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, size = surroundingObjects.size(); i < size; i++) {
                GameObject go = surroundingObjects.get(i);
                if (tmpRect.overlaps(go.mColRect)) {
                    neighbours.add(go);
                }
            }
        }
    }

    private int findSmallestDiffIndex(float diffLeft, float diffRight, float diffTop, float diffBottom)
    {
        int index;
        float smallest;
        if(diffLeft < diffRight)
        {
            smallest = diffLeft;
            index = 0;
        }
        else
        {
            smallest = diffRight;
            index = 1;
        }
        if(smallest > diffTop)
        {
            smallest = diffTop;
            index = 2;
        }
        if(smallest > diffBottom)
        {
            index = 3;
        }
        return index;
    }

    protected boolean checkY()
    {
        boolean collides = false;
        // the same thing but on the vertical Y axis
        tmpRect.set(mColRect.x, 0, mColRect.width, mColRect.y);
        float tmpGroundY = 0;
        float distance = mColRect.y;
        float oldY = mColRect.y;

        mColRect.y += velocity.y;

        boolean found = false;

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = neighbours.size; i < size; i++)
        //for (GameObject object : surroundingObjects)
        {
            GameObject object = neighbours.get(i);
            if (mColRect.overlaps(object.mColRect))
            {
                boolean tmp = handleCollision(object, true);
                if(tmp)
                    collides = true;
            }

            //checkGround
            if (object instanceof Sprite
                    && (((Sprite) object).type == Sprite.Type.massive || ((Sprite) object).type == Sprite.Type.halfmassive)
                    && tmpRect.overlaps(object.mColRect))
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
                    closestObject = object;
                    found = true;
                }
            }
        }
        if(!found)
            closestObject = null;
        groundY = tmpGroundY;
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

        //List<GameObject> surroundingObjects = world.level.gameObjects;//world.getSurroundingObjects(this, 1);
        // if m collides, make his horizontal velocity 0
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = neighbours.size; i < size; i++)
        //for (GameObject object : surroundingObjects)
        {
            GameObject object = neighbours.get(i);
            if (mColRect.overlaps(object.mColRect))
            {
                boolean tmp = handleCollision(object, false);
                if(tmp)
                    collides = true;
            }
        }
        if (mColRect.x < 0 || mColRect.x + mColRect.width > world.level.width)
        {
            boolean tmp = handleLevelEdge();
            if(tmp)
                collides = true;
        }

        // reset the x position of the collision box
        mColRect.x = position.x;
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

    protected boolean handleLevelEdge()
    {
        velocity.x = 0;
        return false;
    }

    protected boolean handleCollision(GameObject object, boolean vertical)
	{
		if(object instanceof Sprite && ((Sprite)object).type == Sprite.Type.massive)
		{
			if(vertical)
			{
                if(velocity.y > 0 && this instanceof Maryo)
                {
                    ((Maryo)this).jumpPeakReached = true;
                    if(System.currentTimeMillis() - lasHitSoundPlayed > 200)
                    {
                        Sound sound = world.screen.game.assets.manager.get(Assets.SOUND_WALL_HIT);
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
