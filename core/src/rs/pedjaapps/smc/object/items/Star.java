package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSaveUtility;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class Star extends Item
{
    public static final float GLIM_COLOR_START_ALPHA = 0f;
    public static final float GLIM_COLOR_MAX_ALPHA = 0.95f;
    public static final float POSITION_Z = 0.053f;
    public static final float VELOCITY_X = 3f;
    public static final float VELOCITY_Y = 10f;
    public static final float DEF_SIZE = 0.65625f;

    boolean moving;
    public float velY = -1;

    private Direction direction = Direction.right;

    private final Color glimColor = new Color(0.160784314f, 0.654901961f, 1f, GLIM_COLOR_START_ALPHA);
    private float glimCounter;
    private boolean glimMode = true;
    ParticleEffect trail;

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
        trail = new ParticleEffect(Assets.manager.get("data/animation/particles/star_trail.p", ParticleEffect.class));
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        if (!visible) return;
        trail.setPosition(mColRect.x + mColRect.width * 0.5f, mColRect.y + mColRect.height * 0.5f);
        trail.draw(spriteBatch);
        spriteBatch.setShader(Shader.NORMAL_BLEND_SHADER);

        if (glimMode)
        {
            glimColor.a = glimCounter;
            if (glimCounter > GLIM_COLOR_MAX_ALPHA)
            {
                glimMode = false;
                glimCounter = GLIM_COLOR_MAX_ALPHA;
            }
        }
        else
        {
            glimColor.a = glimCounter;
            if (glimCounter < GLIM_COLOR_START_ALPHA)
            {
                glimMode = true;
                glimCounter = GLIM_COLOR_START_ALPHA;
            }
        }
        spriteBatch.setColor(glimColor);

        float width = Utility.getWidth(texture, mDrawRect.height);
        float originX = width * 0.5f;
        float originY = mDrawRect.height * 0.5f;
        spriteBatch.draw(texture, mDrawRect.x, mDrawRect.y, originX, originY, width, mDrawRect.height,
                1, 1, mRotationZ, 0, 0, texture.getWidth(), texture.getHeight(), false, false);

        spriteBatch.setShader(null);
        spriteBatch.setColor(Color.WHITE);
    }

    @Override
    public void updateItem(float delta)
    {
        super.updateItem(delta);
        if (popFromBox)
        {
            // scale velocity to frame units
            velocity.scl(delta);

            // update position
            position.add(velocity);
            mColRect.y = position.y;
            updateBounds();

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);

            if (position.y >= popTargetPosY)
            {
                isInBox = false;
                popFromBox = false;
                moving = true;
                velocity.x = direction == Direction.right ? VELOCITY_X : -VELOCITY_X;
            }
        }
        else if (moving)
        {
            trail.update(delta);
            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(delta);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            checkCollisionWithBlocks(delta, true, true, false, false);

            switch (direction)
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
        if (glimMode)
        {
            glimCounter += (delta * 3f);
        }
        else
        {
            glimCounter -= (delta * 3f);
        }
        getRotation(delta);
    }

    private void getRotation(float delta)
    {
        float circumference = (float) Math.PI * (mColRect.width);
        float deltaVelocity = VELOCITY_X * delta;
        float step = (circumference / deltaVelocity);
        float frameRotation = 360 / step;//degrees
        frameRotation *= 0.5f;

        if(velocity.y > 0.0f)
        {
            mRotationZ += frameRotation;
        }
        // rotate back to 0 if falling
        else
        {
            frameRotation *= 0.9f;
            if(mRotationZ > 5.0f && mRotationZ <= 175.0f)
            {
                mRotationZ -= frameRotation;
            }
            else if(mRotationZ < 355 && mRotationZ > 185)
            {
                mRotationZ += frameRotation;
            }
        }
        if(mRotationZ > 360)
        {
            mRotationZ = mRotationZ - 360;
        }
        if(mRotationZ < -360)
        {
            mRotationZ = 0 - mRotationZ;
        }
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical)
    {
        if (object instanceof Sprite)
        {
            if (((Sprite) object).type == Sprite.Type.massive)
            {
                if (vertical)
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
        if (isInBox) return;
        playerHit = true;
        //TODO play ""game/star.ogg","
        //TODO play music for star, or whatewer
        GameSaveUtility.getInstance().save.points += 1000;
        ((GameScreen)world.screen).killPointsTextHandler.add(1000, position.x, position.y + mDrawRect.height);
        world.maryo.starPicked();
        world.trashObjects.add(this);
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

    @Override
    public void dispose()
    {
        super.dispose();
        trail.dispose();
        trail = null;
    }
}
