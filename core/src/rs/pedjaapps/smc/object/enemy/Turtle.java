package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 * Copyright pedja
 */
public class Turtle extends Enemy
{
    private static final float SHELL_TIMEOUT_SEC = 5;
    private final float mVelocity;
    private static final float VELOCITY_TURN = 0.75f;
    private final float mVelocityShell;
    static final float POS_Z = 0.091f;

    private float mShellRotation, mShelledTime;

    public boolean isShell = false, isShellMoving = false;
    private Animation walkAnimation;
    private TextureRegion tTurn, tShell, tDead;

    Turtle(World world, Vector2 size, Vector3 position, String color)
    {
        super(world, size, position);
        if(!"green".equals(color))
        {
            mKillPoints = 50;
            mVelocity = 2f;
            mVelocityShell = 5.8f;
        }
        else
        {
            mKillPoints = 150;
            mVelocity = 2.5f;
            mVelocityShell = 7.1f;
        }
        setupBoundingBox();
        world.screen.game.assets.manager.load("data/sounds/enemy/turtle/shell/hit.mp3", Sound.class);
        world.screen.game.assets.manager.load("data/sounds/enemy/turtle/hit.mp3", Sound.class);
    }

    @Override
    public void initAssets()
    {
        TextureAtlas atlas = world.screen.game.assets.manager.get(textureAtlas);
        Array<TextureRegion> walkFrames = new Array<>();

        for(int i = 1; i < 9; i++)
        {
            TextureRegion region = atlas.findRegion("walk", i);
            walkFrames.add(region);
        }


        walkAnimation = new Animation(0.07f, walkFrames);
        tTurn = atlas.findRegion("turn");
        tShell = atlas.findRegion("shell");
        tDead = atlas.findRegion("walk", 1);

    }

    @Override
    public void dispose()
    {
        walkAnimation = null;
        tDead = null;
        tTurn = null;
        tShell = null;
    }

    @Override
    public boolean isBullet()
    {
        return true;
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame;
        if(!isShell && turn)
        {
            frame = tTurn;
        }
        else
        {
            if(isShell)
            {
                frame = tShell;
            }
            else
            {
                frame = walkAnimation.getKeyFrame(stateTime, true);
            }
        }
        if(frame != null)
        {
            float width = Utility.getWidth(frame, mDrawRect.height);
            float originX = width * 0.5f;
            float originY = mDrawRect.height * 0.5f;
            frame.flip(!isShell && !turn && direction == Direction.right, false);
            spriteBatch.draw(frame, mDrawRect.x, mDrawRect.y, originX, originY, width, mDrawRect.height, 1, 1, mShellRotation);
            frame.flip(!isShell && !turn && direction == Direction.right, false);
        }
    }

    @Override
    public boolean canBeKilledByJumpingOnTop()
    {
        return true;
    }

    private float getRotation(float delta)
    {
        if(isShellMoving)
        {
            float circumference = (float) Math.PI * (mColRect.width);
            float deltaVelocity = mVelocityShell * delta;

            float step = circumference / deltaVelocity;


            float frameRotation = 360 / step;//degrees
            mShellRotation += frameRotation;
            if(mShellRotation > 360)mShellRotation = mShellRotation - 360;
        }
        else
        {
            mShellRotation = 0;
        }
        return direction == Direction.left ? mShellRotation : -mShellRotation;
    }

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

        if(stateTime - turnStartTime > 0.15f)
        {
            turnStartTime = 0;
            turn = false;
        }

        /*if (isShell && !isShellMoving)
        {
            mShelledTime -= deltaTime;
            if(mShelledTime <= 0)
            {
                isShell = false;
                isShellMoving = false;

                mDrawRect.height = mDrawRect.height / 0.60f;
                mDrawRect.width = mDrawRect.width / 0.60f;
                mColRect.height = mDrawRect.height;
                mColRect.width = mDrawRect.width;
            }
        }*/

        if (!deadByBullet)
        {
            switch(direction)
            {
                case right:
                    velocity.set(velocity.x = +getVelocityX(), velocity.y, velocity.z);
                    break;
                case left:
                    velocity.set(velocity.x = -getVelocityX(), velocity.y, velocity.z);
                    break;
            }
        }
        turned = false;
        mShellRotation = getRotation(deltaTime);
    }

    private float getVelocityX()
    {
        if(isShell)
        {
            if(isShellMoving)
            {
                return mVelocityShell;
            }
            else
            {
                return 0;
            }
        }
        else
        {
            if(turn)
            {
                return VELOCITY_TURN;
            }
            else
            {
                return mVelocity;
            }
        }
    }

    @Override
	protected boolean handleCollision(GameObject object, boolean vertical)
	{
        super.handleCollision(object, vertical);
		if(!vertical)
		{
			if(((object instanceof Sprite && ((Sprite)object).type == Sprite.Type.massive
					&& object.mColRect.y + object.mColRect.height > mColRect.y + 0.1f)
					|| (object instanceof EnemyStopper && !isShellMoving))
                    && !turned)
			{
				//CollisionManager.resolve_objects(this, object, true);
                handleCollision(ContactType.stopper);
			}
            else if(object instanceof Enemy && object != this && isShell && isShellMoving && ((Enemy)object).handleCollision)
            {
                ((Enemy)object).downgradeOrDie(this, false, false);
            }
            else if(object instanceof Enemy && object != this && !isShell && !(object instanceof Flyon))
            {
                turn();
            }
            else if(object instanceof Box && isShell && isShellMoving)
            {
                ((Box) object).activate();
            }
		}
        return false;
	}

	@Override
	public void handleCollision(ContactType contactType)
	{
		switch(contactType)
		{
			case stopper:
				turn();
				break;
            case player:

                break;
		}
	}

    private void setupBoundingBox()
    {
        if(!isShell) mColRect.height = mColRect.height - 0.2f;
    }

    @Override
    public void updateBounds()
    {
        if(!isShell)
        {
            mDrawRect.height = mColRect.height + 0.2f;
            super.updateBounds();
        }
        else
        {
            mDrawRect.x = (mColRect.x) - ((mDrawRect.width - mColRect.width) - mColRect.width / 2);
            mDrawRect.y = (mColRect.y) - ((mDrawRect.height - mColRect.height) - mColRect.height / 2);
        }
    }

    @Override
    public int hitByPlayer(Maryo maryo, boolean vertical)
    {
        if (maryo.velocity.y < 0 && vertical && maryo.mColRect.y > mColRect.y)//enemy death from above
        {
            //transform to shell if not shell
            //if shell make it move
            // if shell and moving make it stop
            if (!isShell)
            {
                mShelledTime = SHELL_TIMEOUT_SEC;
                isShell = true;
                velocity.x = 0;
                mDrawRect.height = mDrawRect.height * 0.60f;
                mDrawRect.width = mDrawRect.width * 0.60f;
                mColRect.height = mDrawRect.height / 2;
                mColRect.width = mDrawRect.width / 2;
                return HIT_RESOLUTION_ENEMY_DIED;
            }
            else
            {
                direction = (maryo.position.x + maryo.mColRect.width * 0.5f) > (position.x + mColRect.width * 0.5f) ? Direction.right : Direction.left;
                isShellMoving = !isShellMoving;
            }
            return HIT_RESOLUTION_CUSTOM;
        }
        else
        {
            String soundFile;
            if(isShell)
            {
                soundFile = "data/sounds/enemy/turtle/shell/hit.mp3";
            }
            else
            {
                soundFile = "data/sounds/enemy/turtle/hit.mp3";
            }
            AssetManager assetManager = world.screen.game.assets.manager;
            if(assetManager.isLoaded(soundFile))
            {
                SoundManager.play(assetManager.get(soundFile, Sound.class));
            }
            return HIT_RESOLUTION_PLAYER_DIED;
        }
    }

    @Override
    protected TextureRegion getDeadTextureRegion()
    {
        return tDead;
    }

    @Override
    protected String getDeadSound()
    {
        return "data/sounds/stomp_4.mp3";
    }
}
