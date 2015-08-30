package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.Assets;
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
public class Star extends Item
{
    public static final float POSITION_Z = 0.053f;
    public static final float VELOCITY_X = 3f;
    public static final float VELOCITY_Y = 10f;
    public static final float DEF_SIZE = 0.49f;

    boolean moving;
    public float velY = -1;

    public enum Direction
    {
        right, left, up, down
    }

    private Direction direction = Direction.right;

    public Star(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        position.z = POSITION_Z;
        textureName = "data/game/items/star.png";
    }

    @Override
    public void initAssets()
    {
        texture = Assets.manager.get(textureName);
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        if(!visible)return;
        Utility.draw(spriteBatch, texture, position.x, position.y, mDrawRect.height);
    }

    @Override
    public void updateItem(float delta)
    {
        super.updateItem(delta);
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
                isInBox = false;
                popFromBox = false;
                moving = true;
                velocity.x = direction == Direction.right ? VELOCITY_X : -VELOCITY_X;
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

            checkCollisionWithBlocks(delta, true, true, false, false);

            switch(direction)
            {
                case right:
                    velocity.x = VELOCITY_X;
                    break;
                case left:
                    velocity.x = -VELOCITY_X;
                    break;
            }
            if (velY != -1)
            {
                velocity.y = velY;
                velY = -1;
            }
        }
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical)
    {
        if(object instanceof Sprite)
        {
            if (((Sprite)object).type == Sprite.Type.massive)
            {
                if(vertical)
                {
                    velY = mColRect.y < object.mColRect.y ? 0 : VELOCITY_Y;
                    return true;
                }
                else if (mColRect.y > groundY)
                {
                    if (velocity.x < 0)
                    {
                        direction = Direction.right;
                    }
                    else
                    {
                        direction = Direction.left;
                    }
                    return true;
                }
            }
            else if (((Sprite) object).type == Sprite.Type.halfmassive)
            {
                if (vertical && mColRect.y + mColRect.height > object.mColRect.y + object.mColRect.height)
                {
                    velY = VELOCITY_Y;
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public float maxVelocity()
    {
        return VELOCITY_X;
    }

    @Override
    public void hitPlayer()
    {
        if(isInBox)return;
        playerHit = true;
        //performCollisionAction();
    }

    @Override
    public void popOutFromBox(float popTargetPositionY)
    {
        super.popOutFromBox(popTargetPositionY);
        visible = true;
        popFromBox = true;
        velocity.y = VELOCITY_Y;
        originalPosY = position.y;
    }

    //protected abstract void performCollisionAction();
}
