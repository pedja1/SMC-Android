package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class Star extends BoxItem
{
    public static final float VELOCITY_X = 1.5f;
    public static final float VELOCITY_POP = 1.6f;
    public static final float DEF_SIZE = 0.49f;

    protected boolean grounded = false;

    boolean moving;

    public enum Direction
    {
        right, left
    }

    private Direction direction = Direction.right;

    public Star(World world, Vector2 size, Vector3 position, Box box)
    {
        super(world, size, position, box);
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        if(!visible)return;
        Texture txt = Assets.manager.get(textureName);
        Utility.draw(spriteBatch, txt, position.x, position.y, mDrawRect.height);
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
        if(popFromBox)
        {
            // scale velocity to frame units
            velocity.scl(delta);

            // update position
            position.add(velocity);
            mColRect.y = position.y;
            updateBounds();

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);

            if(position.y >= popTargetPosY)
            {
                popFromBox = false;
                moving = true;
                //velocity.x = direction == Direction.right ? VELOCITY : -VELOCITY;
            }
        }
        else if(moving)
        {
            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(delta);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            checkCollisionWithBlocks(delta);

            switch(direction)
            {
                case right:
                    //velocity.x = VELOCITY;
                    break;
                case left:
                    //velocity.x = -VELOCITY;
                    break;
            }
        }
    }

    /** Collision checking **/
    protected void checkCollisionWithBlocks(float delta)
    {
        // scale velocity to frame units
        velocity.scl(delta);

        // we first check the movement on the horizontal X axis

        // simulate movement on the X
        mColRect.x += velocity.x;

        List<GameObject> surroundingObjects = world.level.gameObjects;
        // if m collides, make his horizontal velocity 0
        for (GameObject object : surroundingObjects)
        {
            if (object == null) continue;
            if (mColRect.overlaps(object.mColRect))
            {
                handleCollision(object, false);
            }
        }
        if(mColRect.x < 0 || mColRect.x + mColRect.width > world.level.width)
        {
            velocity.x = 0;
        }

        // reset the x position of the collision box
        mColRect.x = position.x;

        // the same thing but on the vertical Y axis

        mColRect.y += velocity.y;

        for (GameObject object : surroundingObjects)
        {
            if (object == null) continue;
            if (mColRect.overlaps(object.mColRect))
            {
                handleCollision(object, true);
            }
        }
        if(mColRect.y < 0)
        {
            handleDroppedBelowWorld();
        }

        // reset the collision box's position on Y
        mColRect.y = position.y;

        // update position
        position.add(velocity);
        mColRect.x = position.x;
        mColRect.y = position.y;
        updateBounds();

        // un-scale velocity (not in frame time)
        velocity.scl(1 / delta);
    }

    protected void handleDroppedBelowWorld()
    {
        world.trashObjects.add(this);
    }

    protected void handleCollision(GameObject object, boolean vertical)
    {
        if(object instanceof Sprite
                && ((Sprite)object).type == Sprite.Type.massive)
        {
            if(vertical)
            {
                if (velocity.y < 0)
                {
                    grounded = true;
                }
                velocity.y = 0;
            }
            else
            {
                if(object.position.y + object.mDrawRect.height / 2 > position.y)
                {
                    direction = direction == Direction.right ? Direction.left : Direction.right;
                    velocity.x = velocity.x > 0 ? -velocity.x : Math.abs(velocity.x);
                }
            }
        }
    }

    @Override
    public void hitPlayer()
    {
        playerHit = true;
        //performCollisionAction();
    }

    @Override
    public void popOutFromBox(float popTargetPositionY)
    {
        super.popOutFromBox(popTargetPositionY);
        visible = true;
        popFromBox = true;
        velocity.y = VELOCITY_POP;
        originalPosY = position.y;
    }

    //protected abstract void performCollisionAction();
}
