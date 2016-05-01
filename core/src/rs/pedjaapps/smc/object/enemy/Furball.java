package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Copyright (c) 2016 "Predrag Čokulov,"
 * pedjaapps [https://pedjaapps.net]
 *
 * This file is part of SMC-Android.
 *
 * SMC-Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.*/
public class Furball extends Enemy
{
    public static final float VELOCITY = 1.5f;
    public static final float VELOCITY_TURN = 0.75f;
    public static final float POS_Z = 0.09f;
    private boolean dying = false;

    //only for boss
    private int downgradeCount;
    private int maxDowngradeCount = 5;

    enum Type
    {
        brown, blue, boss
    }

    private Type type = Type.brown;

    private Animation walkAnimation;
    private TextureRegion tTurn, tDead, tHit;

    public Furball(World world, Vector2 size, Vector3 position, int maxDowngradeCount)
    {
        super(world, size, position);
        setupBoundingBox();
        this.maxDowngradeCount = maxDowngradeCount;
    }

    @Override
    public void initAssets()
    {
        TextureAtlas atlas = world.screen.game.assets.manager.get(textureAtlas);
        Array<TextureRegion> walkFrames = new Array<TextureRegion>();

        for(int i = 1; i < 9; i++)
        {
            TextureRegion region = atlas.findRegion("walk", i);
            walkFrames.add(region);
        }

        walkAnimation = new Animation(0.07f, walkFrames);

        tTurn = atlas.findRegion("turn");
        tDead = atlas.findRegion("dead");

        if(textureAtlas.contains("brown"))
        {
            type = Type.brown;
            mKillPoints = 10;
            mFireResistant = 0;
            mIceResistance = .0f;
            mCanBeHitFromShell = 1;
        }
        else if(textureAtlas.contains("blue"))
        {
            type = Type.blue;
            mKillPoints = 50;
            mFireResistant = 0;
            mIceResistance = .9f;
            mCanBeHitFromShell = 1;
        }
        else if(textureAtlas.contains("boss"))
        {
            type = Type.boss;
            mKillPoints = 2500;
            mFireResistant = 1;
            mIceResistance = 1f;
            mCanBeHitFromShell = 0;
            tHit = atlas.findRegion("hit");
        }
    }

    @Override
    public void dispose()
    {
        walkAnimation = null;
        tTurn = null;
        tHit = null;
        tDead = null;
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame;
        if (!dying)
        {
            frame = turn ? tTurn : walkAnimation.getKeyFrame(stateTime, true);
            frame.flip(direction == Direction.left, false);
            Utility.draw(spriteBatch, frame, mDrawRect.x, mDrawRect.y, mDrawRect.height);
            frame.flip(direction == Direction.left, false);
        }
        else
        {
            frame = tDead;
            frame.flip(direction == Direction.left, false);
            spriteBatch.draw(frame, mDrawRect.x , mDrawRect.y , mDrawRect.width, mDrawRect.height);
            frame.flip(direction == Direction.left, false);
        }
    }

    @Override
    public boolean canBeKilledByJumpingOnTop()
    {
        return true;
    }

    @Override
    public void update(float deltaTime)
    {
        stateTime += deltaTime;
        if(dying)
        {
            //resize it by state time
            mDrawRect.height -= 1.26f * deltaTime;
            mDrawRect.width -= 0.63f * deltaTime;
            if(mDrawRect.height < 0)
                world.trashObjects.add(this);
            return;
        }

		// Setting initial vertical acceleration 
        acceleration.y = Constants.GRAVITY;

        // Convert acceleration to frame time
        acceleration.scl(deltaTime);

        // apply acceleration to change velocity
        velocity.add(acceleration);

        checkCollisionWithBlocks(deltaTime, !deadByBullet, !deadByBullet);

        if(stateTime - turnStartTime > 0.15f)
        {
            turnStartTime = 0;
            turn = false;
        }

        if (!deadByBullet)
        {
            switch(direction)
            {
                case right:
                    velocity.set(velocity.x =- (turn ? VELOCITY_TURN : VELOCITY), velocity.y, velocity.z);
                    break;
                case left:
                    velocity.set(velocity.x =+ (turn ? VELOCITY_TURN : VELOCITY), velocity.y, velocity.z);
                    break;
            }
        }
        turned = false;
    }
	
	@Override
	protected boolean handleCollision(GameObject object, boolean vertical)
	{
        super.handleCollision(object, vertical);
		if(!vertical)
		{
			if(((object instanceof Sprite && ((Sprite)object).type == Sprite.Type.massive
					&& object.mColRect.y + object.mColRect.height > mColRect.y + 0.1f)
					|| object instanceof EnemyStopper
					|| (object instanceof Enemy && this != object))
                    && !turned)
			{
				//CollisionManager.resolve_objects(this, object, true);
                handleCollision(Enemy.ContactType.stopper);
			}
		}
        return false;
	}

	@Override
	public void handleCollision(Enemy.ContactType contactType)
	{
		switch(contactType)
		{
			case stopper:
				turn();
				break;
		}
	}

    private void setupBoundingBox()
    {
        mColRect.height = mColRect.height - 0.2f;
    }

    @Override
    public void updateBounds()
    {
        mDrawRect.height = mColRect.height + 0.2f;
        super.updateBounds();
    }

    @Override
    public int hitByPlayer(Maryo maryo, boolean vertical)
    {
        if (maryo.velocity.y < 0 && vertical && maryo.mColRect.y > mColRect.y)//enemy death from above
        {
            if (type == Type.boss && downgradeCount >= maxDowngradeCount)
            {
                downgradeCount++;
                return HIT_RESOLUTION_CUSTOM;
            }
            ((GameScreen)world.screen).killPointsTextHandler.add(mKillPoints, position.x, position.y + mDrawRect.height);
            stateTime = 0;
            handleCollision = false;
            dying = true;
            Sound sound = world.screen.game.assets.manager.get("data/sounds/enemy/furball/die.mp3");
            SoundManager.play(sound);
            return HIT_RESOLUTION_ENEMY_DIED;
        }
        else
        {
            return HIT_RESOLUTION_PLAYER_DIED;
        }
    }

    @Override
    protected TextureRegion getDeadTextureRegion()
    {
        return tDead;
    }
}
