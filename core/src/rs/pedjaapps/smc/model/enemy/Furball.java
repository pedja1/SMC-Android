package rs.pedjaapps.smc.model.enemy;

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
import rs.pedjaapps.smc.model.GameObject;
import rs.pedjaapps.smc.model.Sprite;
import rs.pedjaapps.smc.model.World;
import rs.pedjaapps.smc.utility.CollisionManager;
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

    public Furball(World world, Vector2 size, Vector3 position, int maxDowngradeCount)
    {
        super(world, size, position);
        setupBoundingBox();
        this.maxDowngradeCount = maxDowngradeCount;
    }

    @Override
    public void loadTextures()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureRegion> rightFrames = /*atlas.getRegions();//*/new Array<TextureRegion>();
        Array<TextureRegion> leftFrames = /*atlas.getRegions();//*/new Array<TextureRegion>();

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
        TextureRegion tmp = atlas.findRegion("dead");
        Assets.loadedRegions.put(textureAtlas + ":dead", tmp);
        tmp = new TextureRegion(tmp);
        tmp.flip(true, false);
        Assets.loadedRegions.put(textureAtlas + ":dead_r", tmp);

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
            Assets.loadedRegions.put(textureAtlas + ":hit", atlas.findRegion("hit"));
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame;
        if (!dying)
        {
            frame = turn ? Assets.loadedRegions.get(textureAtlas + ":turn")
                    : Assets.animations.get(direction == Direction.right ? textureAtlas : textureAtlas + "_l").getKeyFrame(stateTime, true);
            Utility.draw(spriteBatch, frame, bounds.x, bounds.y, bounds.height);
        }
        else
        {
            frame = direction == Direction.right ? Assets.loadedRegions.get(textureAtlas + ":dead") : Assets.loadedRegions.get(textureAtlas + ":dead_r");
            spriteBatch.draw(frame, bounds.x ,bounds.y ,bounds.width, bounds.height);
        }
    }

    public void update(float deltaTime)
    {
        stateTime += deltaTime;
        if(dying)
        {
            //resize it by state time
            bounds.height -= Gdx.graphics.getFramesPerSecond() * 0.00035;
            bounds.width -= Gdx.graphics.getFramesPerSecond() * 0.000175;
            if(bounds.height < 0)world.trashObjects.add(this);
            return;
        }

		// Setting initial vertical acceleration 
        acceleration.y = Constants.GRAVITY;

        // Convert acceleration to frame time
        acceleration.scl(deltaTime);

        // apply acceleration to change velocity
        velocity.add(acceleration);

        checkCollisionWithBlocks(deltaTime);

        if(stateTime - turnStartTime > 0.15f)
        {
            turnStartTime = 0;
            turn = false;
        }

		switch(direction)
		{
			case right:
				setVelocity(velocity.x =- (turn ? VELOCITY_TURN : VELOCITY), velocity.y);
				break;
			case left:
				setVelocity(velocity.x =+ (turn ? VELOCITY_TURN : VELOCITY), velocity.y);
				break;
		}
		turned = false;
    }
	
	@Override
	protected void handleCollision(GameObject object, boolean vertical)
	{
        super.handleCollision(object, vertical);
		if(!vertical)
		{
			if(((object instanceof Sprite && ((Sprite)object).getType() == Sprite.Type.massive
					&& object.getBody().y + object.getBody().height > body.y + 0.1f)
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
        body.height = body.height - 0.2f;
    }

    @Override
    public void updateBounds()
    {
        bounds.height = body.height + 0.2f;
        super.updateBounds();
    }

    @Override
    public void hitByPlayer()
    {
        if(type == Type.boss && downgradeCount >= maxDowngradeCount)
        {
            downgradeCount++;
            return;
        }
        stateTime = 0;
        handleCollision = false;
        dying = true;
        Sound sound = Assets.manager.get("data/sounds/enemy/furball/die.ogg");
        if (sound != null && Assets.playSounds)
        {
            sound.play();
        }
    }
}
