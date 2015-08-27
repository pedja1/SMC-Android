package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.screen.AbstractScreen;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Turtle extends Enemy
{
    private String KEY_TURN, KEY_LEFT, KEY_SHELL, KEY_DEAD;
    public final float mVelocity;
    public static final float VELOCITY_TURN = 0.75f;
    public final float mVelocityShell;
    public static final float POS_Z = 0.091f;

    private boolean turn;
    private float turnStartTime;

    private float mShellRotation;

    public final int mKillPoints;

    private boolean turned = false;

    boolean isShell = false, isShellMoving = false;

    public Turtle(World world, Vector2 size, Vector3 position, String color)
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
    }

    @Override
    public void initAssets()
    {
        KEY_TURN = textureAtlas + ":turn";
        KEY_LEFT = textureAtlas + "_l";
        KEY_SHELL = textureAtlas + ":shell";
        KEY_DEAD = textureAtlas + ":dead";
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureRegion> rightFrames = new Array<TextureRegion>();
        Array<TextureRegion> leftFrames = new Array<TextureRegion>();

        for(int i = 1; i < 9; i++)
        {
            TextureRegion region = atlas.findRegion("walk-" + i);
            rightFrames.add(region);
            TextureRegion regionL = new TextureRegion(region);
            regionL.flip(true, false);
            leftFrames.add(regionL);
        }


        Assets.animations.put(textureAtlas, new Animation(0.07f, rightFrames));
        Assets.animations.put(textureAtlas + "_l", new Animation(0.07f, leftFrames));
        Assets.loadedRegions.put(textureAtlas + ":turn", atlas.findRegion("turn"));
        Assets.loadedRegions.put(textureAtlas + ":shell", atlas.findRegion("shell"));
        Assets.loadedRegions.put(textureAtlas + ":dead", atlas.findRegion("walk-1"));

    }

    @Override
    public void draw(SpriteBatch spriteBatch)
    {
        TextureRegion frame;
        if(!isShell && turn)
        {
            frame = Assets.loadedRegions.get(KEY_TURN);
        }
        else
        {
            if(isShell)
            {
                frame = Assets.loadedRegions.get(KEY_SHELL);
            }
            else
            {
                frame = Assets.animations.get(direction == Direction.right ? textureAtlas : KEY_LEFT).getKeyFrame(stateTime, true);
            }
        }
        if(frame != null)
        {
            float width = Utility.getWidth(frame, mDrawRect.height);
            float originX = width * 0.5f;
            float originY = mDrawRect.height * 0.5f;
            spriteBatch.draw(frame, mDrawRect.x, mDrawRect.y, originX, originY, width, mDrawRect.height, 1, 1, mShellRotation);
        }
    }

    private float getRotation()
    {
        if(isShellMoving)
        {
            float circumference = (float) Math.PI * (mColRect.width);
            float deltaVelocity = mVelocityShell * Gdx.graphics.getDeltaTime();

            float step = circumference / deltaVelocity;


            float frameRotation = 360 / step;//degrees
            mShellRotation += frameRotation;
            if(mShellRotation > 360)mShellRotation = mShellRotation - 360;
        }
        else
        {
            mShellRotation = 0;
        }
        return direction == Direction.right ? mShellRotation : -mShellRotation;
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

        if (!deadByBullet)
        {
            switch(direction)
            {
                case right:
                    velocity.set(velocity.x = -getVelocityX(), velocity.y, velocity.z);
                    break;
                case left:
                    velocity.set(velocity.x = +getVelocityX(), velocity.y, velocity.z);
                    break;
            }
        }
        turned = false;
        mShellRotation = getRotation();
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
                ((Enemy)object).downgradeOrDie(this);
            }
		}
        else
        {

        }
        return false;
	}

	@Override
	public void handleCollision(ContactType contactType)
	{
		switch(contactType)
		{
			case stopper:
				direction = direction == Direction.right ? Direction.left : Direction.right;
                    turnStartTime = stateTime;
                    turn = true;
                velocity.x = velocity.x > 0 ? -velocity.x : Math.abs(velocity.x);
                turned = true;
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
            mDrawRect.x = (world.screen.getTimeStep() == AbstractScreen.FIXED_TIMESTEP ? interpPosition.x : mColRect.x) - ((mDrawRect.width - mColRect.width) - mColRect.width / 2);
            mDrawRect.y =(world.screen.getTimeStep() == AbstractScreen.FIXED_TIMESTEP ? interpPosition.y : mColRect.y) - ((mDrawRect.height - mColRect.height) - mColRect.height / 2);
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
                isShell = true;
                velocity.x = 0;
                mDrawRect.height = mDrawRect.height * 0.60f;
                mDrawRect.width = mDrawRect.width * 0.60f;
                mColRect.height = mDrawRect.height / 2;
                mColRect.width = mDrawRect.width / 2;
            }
            else
            {
                direction = (maryo.position.x + maryo.mColRect.width * 0.5f) > (position.x + mColRect.width * 0.5f) ? Direction.right : Direction.left;
                isShellMoving = !isShellMoving;
            }
            //TODO (v2.0)turtle should automatically transform back from shell after timeout if shell is standing
            return HIT_RESOLUTION_ENEMY_DIED;
        }
        //TODO (v2.0)player can also pick up shell if player is "not small" and if shell is not moving, if shell is moving than player dies
        else
        {

            return HIT_RESOLUTION_PLAYER_DIED;
        }
    }

    @Override
    protected TextureRegion getDeadTextureRegion()
    {
        return Assets.loadedRegions.get(KEY_DEAD);
    }
}
