package rs.pedjaapps.smc.object.items.mushroom;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.items.Item;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public abstract class Mushroom extends Item
{
    public static final float VELOCITY = 1.5f;
    public static final float VELOCITY_POP = 1.6f;
    public static final float DEF_SIZE = 0.546875f;

    protected boolean grounded = false;
    protected int mPickPoints;

    boolean moving;

    public enum Direction
    {
        right, left
    }

    private Direction direction = Direction.right;

    public Mushroom(World world, Vector3 position, float width, float height)
    {
        super(world, position, width, height);
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
        if(moving)
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
                    velocity.x = VELOCITY;
                    break;
                case left:
                    velocity.x = -VELOCITY;
                    break;
            }
        }
    }

    @Override
    protected boolean handleDroppedBelowWorld()
    {
        world.level.gameObjects.removeValue(this, true);
        return false;
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical)
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
        return false;
    }

    @Override
    public void hitPlayer()
    {
        playerHit = true;
        performCollisionAction();
        if(mPickPoints > 0)
            ((GameScreen)world.screen).killPointsTextHandler.add(mPickPoints, position.x, position.y + mDrawRect.height);
    }

    @Override
    public float maxVelocity()
    {
        return VELOCITY;
    }

    protected abstract void performCollisionAction();
}
