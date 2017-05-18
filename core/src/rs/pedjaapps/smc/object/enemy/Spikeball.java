package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Copyright (c) 2016 "Predrag Čokulov,"
 * pedjaapps [https://pedjaapps.net]
 * <p>
 * This file is part of SMC-Android.
 * <p>
 * SMC-Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Spikeball extends Enemy
{
    private static final float VELOCITY_NORMAL = 0.83f;
    private static final float VELOCITY_RUNNING = 2f;
    private static final float POS_Z = 0.09f;


    private static final int STATE_STAYING = 0;
    private static final int STATE_WALKING = 1;
    private static final int STATE_RUNNING = 2;

    private static final int MAX_STAY_TIME = 2;
    private static final int MAX_WALK_TIME = 8;
    private static final int MAX_RUN_TIME = 4;

    private Animation<TextureRegion> walkAnimation;
    private TextureRegion tTurn, tDead;

    private int state;

    private float stayCounter, walkCounter, runCounter;
    private boolean stayDirectionSet;

    public Spikeball(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        mKillPoints = 400;
        mFireResistant = 1;
        mIceResistance = 1f;
        mCanBeHitFromShell = 0;
        position.z = POS_Z;
        state = STATE_WALKING;
        walkCounter = MathUtils.random(0, 2.6f);
    }

    @Override
    public void initAssets()
    {
        TextureAtlas atlas = world.screen.game.assets.manager.get(textureAtlas);
        Array<TextureRegion> walkFrames = new Array<>();

        for (int i = 1; i < 9; i++)
        {
            TextureRegion region = atlas.findRegion("walk", i);
            walkFrames.add(region);
        }

        walkAnimation = new Animation<>(0.07f, walkFrames);

        tTurn = atlas.findRegion("turn");
        tDead = atlas.findRegion("turn");
    }

    @Override
    public void dispose()
    {
        walkAnimation = null;
        tTurn = null;
        tDead = null;
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        if (!stayDirectionSet && state == STATE_STAYING)
        {
            Utility.draw(spriteBatch, tTurn, mDrawRect.x, mDrawRect.y, mDrawRect.height);
        }
        else
        {
            TextureRegion frame = turn ? tTurn : walkAnimation.getKeyFrame(stateTime, true);
            frame.flip(direction == Direction.left, false);
            Utility.draw(spriteBatch, frame, mDrawRect.x, mDrawRect.y, mDrawRect.height);
            frame.flip(direction == Direction.left, false);
        }
    }

    @Override
    public boolean canBeKilledByJumpingOnTop()
    {
        return false;
    }

    @Override
    public void update(float deltaTime)
    {
        stateTime += deltaTime;

        // Setting initial vertical acceleration
        acceleration.y = Constants.GRAVITY;

        // Convert acceleration to frame time
        acceleration.scl(deltaTime);

        // apply acceleration to change velocity
        velocity.add(acceleration);

        checkCollisionWithBlocks(deltaTime, !deadByBullet, !deadByBullet);

        if (stateTime - turnStartTime > 0.15f)
        {
            turnStartTime = 0;
            turn = false;
        }

        float currentVelocity;
        if (state == STATE_STAYING)
        {
            currentVelocity = 0;
            stayCounter += deltaTime;
            if (stayCounter >= 1.3f && stayDirectionSet)
            {
                stayDirectionSet = true;
                boolean rand = MathUtils.randomBoolean();
                setDirection(rand ? Direction.left : Direction.right);
            }
            if (stayCounter >= MAX_STAY_TIME)
            {
                state = STATE_RUNNING;
                stayCounter = 0;
                stayDirectionSet = false;
            }
        }
        else if (state == STATE_WALKING)
        {
            currentVelocity = VELOCITY_NORMAL;
            walkCounter += deltaTime;
            if (walkCounter >= MAX_WALK_TIME)
            {
                state = STATE_STAYING;
                walkCounter = 0;
            }
        }
        else //if(state == STATE_RUNNING)
        {
            currentVelocity = VELOCITY_RUNNING;
            runCounter += deltaTime;
            if (runCounter >= MAX_RUN_TIME)
            {
                state = STATE_WALKING;
                walkCounter = MathUtils.random(0, 2.6f);
                runCounter = 0;
            }
        }

        if (!deadByBullet)
        {
            switch (direction)
            {
                case right:
                    velocity.x = -(turn ? currentVelocity * 0.5f : currentVelocity);
                    break;
                case left:
                    velocity.x = +(turn ? currentVelocity * 0.5f : currentVelocity);
                    break;
            }
        }
        turned = false;
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical)
    {
        super.handleCollision(object, vertical);
        if (!vertical)
        {
            if (((object instanceof Sprite && ((Sprite) object).type == Sprite.Type.massive
                    && object.mColRect.y + object.mColRect.height > mColRect.y + 0.1f)
                    || object instanceof EnemyStopper
                    || (object instanceof Enemy && this != object && !(object instanceof Flyon)))
                    && !turned)
            {
                //CollisionManager.resolve_objects(this, object, true);
                handleCollision(ContactType.stopper);
            }
        }
        return false;
    }

    @Override
    public void handleCollision(ContactType contactType)
    {
        switch (contactType)
        {
            case stopper:
                turn();
                break;
        }
    }

    @Override
    public int hitByPlayer(Maryo maryo, boolean vertical)
    {
        return HIT_RESOLUTION_PLAYER_DIED;
    }

    @Override
    protected TextureRegion getDeadTextureRegion()
    {
        return tDead;
    }
}
