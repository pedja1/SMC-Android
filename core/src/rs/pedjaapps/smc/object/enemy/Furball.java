package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Furball extends Enemy
{

    public static final float VELOCITY = 1.5f;
    public static final float VELOCITY_TURN = 0.75f;
    public static final float POS_Z = 0.09f;

    private boolean turn;
    private float turnStartTime;

    private boolean turned = false;
    boolean dying = false;
    int killPoints = 10;
    boolean fireResistant = false;
    float iceResistance = 0.0f;
    boolean canBeHitFromShell = true;

    //only for boss
    int downgradeCount;
    int maxDowngradeCount = 5;

    enum Type
    {
        brown, blue, boss
    }

    Type type = Type.brown;

    private String keyDead, keyLeft, keyDeadRight, keyTurn, keyHit;

    public Furball(World world, Vector2 size, Vector3 position, int maxDowngradeCount)
    {
        super(world, size, position);
        setupBoundingBox();
        this.maxDowngradeCount = maxDowngradeCount;
    }

    @Override
    public void initAssets()
    {
        keyDead = textureAtlas + ":dead";
        keyDeadRight = textureAtlas + ":dead_r";
        keyLeft = textureAtlas + "_l";
        keyTurn = textureAtlas + ":turn";
        keyHit = textureAtlas + ":hit";
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
        Assets.animations.put(keyLeft, new Animation(0.07f, leftFrames));
        Assets.loadedRegions.put(keyTurn, atlas.findRegion("turn"));
        TextureRegion tmp = atlas.findRegion("dead");
        Assets.loadedRegions.put(keyDead, tmp);
        tmp = new TextureRegion(tmp);
        tmp.flip(true, false);
        Assets.loadedRegions.put(keyDeadRight, tmp);

        if(textureAtlas.contains("brown"))
        {
            type = Type.brown;
            killPoints = 10;
            fireResistant = false;
            iceResistance = .0f;
            canBeHitFromShell = true;
        }
        else if(textureAtlas.contains("blue"))
        {
            type = Type.blue;
            killPoints = 50;
            fireResistant = false;
            iceResistance = .9f;
            canBeHitFromShell = true;
        }
        else if(textureAtlas.contains("boss"))
        {
            type = Type.boss;
            killPoints = 2500;
            fireResistant = true;
            iceResistance = 1f;
            canBeHitFromShell = false;
            Assets.loadedRegions.put(keyHit, atlas.findRegion("hit"));
        }
    }

    @Override
    public void draw(SpriteBatch spriteBatch)
    {
        TextureRegion frame;
        if (!dying)
        {
            frame = turn ? Assets.loadedRegions.get(keyTurn)
                    : Assets.animations.get(direction == Direction.right ? textureAtlas : keyLeft).getKeyFrame(stateTime, true);
            Utility.draw(spriteBatch, frame, mDrawRect.x, mDrawRect.y, mDrawRect.height);
        }
        else
        {
            frame = direction == Direction.right ? Assets.loadedRegions.get(keyDead) : Assets.loadedRegions.get(keyDeadRight);
            spriteBatch.draw(frame, mDrawRect.x , mDrawRect.y , mDrawRect.width, mDrawRect.height);
        }
    }

    @Override
    public void update(float deltaTime)
    {
        stateTime += deltaTime;
        if(dying)
        {
            //resize it by state time
            mDrawRect.height -= Gdx.graphics.getFramesPerSecond() * 0.00035;//TODO ovako zavisi brzina animacije od fps-a
            mDrawRect.width -= Gdx.graphics.getFramesPerSecond() * 0.000175;
            if(mDrawRect.height < 0)world.trashObjects.add(this);
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
	protected void handleCollision(GameObject object, boolean vertical)
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
	}

	@Override
	public void handleCollision(Enemy.ContactType contactType)
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
            System.out.println("hitByPlayer");
            ((GameScreen)world.screen).killPointsTextHandler.add(killPoints, position.x, position.y + mDrawRect.height);
            if (type == Type.boss && downgradeCount >= maxDowngradeCount)
            {
                downgradeCount++;
                return HIT_RESOLUTION_ENEMY_DIED;
            }
            stateTime = 0;
            handleCollision = false;
            dying = true;
            Sound sound = Assets.manager.get("data/sounds/enemy/furball/die.ogg");
            if (sound != null && Assets.playSounds)
            {
                sound.play();
            }
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
        return Assets.loadedRegions.get(keyDead);
    }

    @Override
    public void downgradeOrDie(GameObject killedBy)
    {
        super.downgradeOrDie(killedBy);
        ((GameScreen)world.screen).killPointsTextHandler.add(killPoints, position.x, position.y + mDrawRect.height);
    }
}
