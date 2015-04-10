package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Turtle extends Enemy
{

    public static final float VELOCITY = 1.5f;
    public static final float VELOCITY_TURN = 0.75f;
    public static final float POS_Z = 0.091f;

    private boolean turn;
    private float turnStartTime;

    private boolean turned = false;

    boolean isShell = false;

    boolean isBoss;

    public Turtle(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        setupBoundingBox();
    }

    @Override
    public void loadTextures()
    {
        isBoss = textureAtlas.contains("red");
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureRegion> rightFrames = new Array<TextureRegion>();
        Array<TextureRegion> leftFrames = new Array<TextureRegion>();

        for(int i = isBoss ? 0 : 1; i < (isBoss ? 3 : 9); i++)
        {
            TextureRegion region = atlas.findRegion("walk-" + i);
            rightFrames.add(region);
            TextureRegion regionL = new TextureRegion(region);
            regionL.flip(true, false);
            leftFrames.add(regionL);
        }


        Assets.animations.put(textureAtlas, new Animation(0.07f, rightFrames));
        Assets.animations.put(textureAtlas + "_l", new Animation(0.07f, leftFrames));
        if(!isBoss)Assets.loadedRegions.put(textureAtlas + ":turn", atlas.findRegion("turn"));

    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame;
        frame = (turn && !isBoss) ? Assets.loadedRegions.get(textureAtlas + ":turn")
                : Assets.animations.get(direction == Direction.right ? textureAtlas : textureAtlas + "_l").getKeyFrame(stateTime, true);
        Utility.draw(spriteBatch, frame, bounds.x, bounds.y, bounds.height);
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

        checkCollisionWithBlocks(deltaTime);

        if(stateTime - turnStartTime > 0.15f)
        {
            turnStartTime = 0;
            turn = false;
        }

		switch(direction)
		{
			case right:
				velocity.set(velocity.x =- (turn ? VELOCITY_TURN : VELOCITY), velocity.y, velocity.z);
				break;
			case left:
				velocity.set(velocity.x =+ (turn ? VELOCITY_TURN : VELOCITY), velocity.y, velocity.z);
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
			if(((object instanceof Sprite && ((Sprite)object).type == Sprite.Type.massive
					&& object.body.y + object.body.height > body.y + 0.1f)
					|| object instanceof EnemyStopper
					|| (object instanceof Enemy && object != this))
                    && !turned)
			{
				//CollisionManager.resolve_objects(this, object, true);
                handleCollision(ContactType.stopper);
			}
		}
	}

	@Override
	public void handleCollision(ContactType contactType)
	{
		switch(contactType)
		{
			case stopper:
				direction = direction == Direction.right ? Direction.left : Direction.right;
                if (!isBoss)
                {
                    turnStartTime = stateTime;
                    turn = true;
                }
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
}
