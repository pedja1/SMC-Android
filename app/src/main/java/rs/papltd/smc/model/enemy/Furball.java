package rs.papltd.smc.model.enemy;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import rs.papltd.smc.*;
import rs.papltd.smc.utility.*;
import com.badlogic.gdx.physics.box2d.BodyDef.*;
import rs.papltd.smc.model.enemy.Enemy.*;

/**
 * Created by pedja on 18.5.14..
 */
public class Furball extends Enemy
{

	@Override
	public BodyDef.BodyType getBodyType()
	{
		return BodyDef.BodyType.DynamicBody;
	}

    public static final float VELOCITY = 1.5f;
    public static final float VELOCITY_TURN = 0.75f;
    public static final float POS_Z = 0.09f;

    private boolean turn;
    private float turnStartTime;

    public Furball(World world, Vector3 position, float width, float height)
    {
        super(world, position, width, height);
    }

    @Override
    public void loadTextures()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureRegion> rightFrames = /*atlas.getRegions();//*/new Array<>();
        Array<TextureRegion> leftFrames = /*atlas.getRegions();//*/new Array<>();

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
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame = turn ? Assets.loadedRegions.get(textureAtlas + ":turn")
                : Assets.animations.get(direction == Direction.right ? textureAtlas : textureAtlas + "_l").getKeyFrame(stateTime, true);

        //spriteBatch.draw(frame, body.getPosition().x - getBounds().width/2, body.getPosition().y - getBounds().height/2, bounds.width, bounds.height);
        Utility.draw(spriteBatch, frame, body.getPosition().x - bounds.width / 2, body.getPosition().y - bounds.height / 2, bounds.height);
    }

    public void update(float deltaTime)
    {
        stateTime += deltaTime;
        if(stateTime - turnStartTime > 0.15f)
        {
            turnStartTime = 0;
            turn = false;
        }
        Vector2 position = body.getPosition();
        Vector2 velocity = body.getLinearVelocity();

		switch(direction)
		{
			case right:
				body.setLinearVelocity(velocity.x =- (turn ? VELOCITY_TURN : VELOCITY), velocity.y);
				break;
			case left:
				body.setLinearVelocity(velocity.x =+ (turn ? VELOCITY_TURN : VELOCITY), velocity.y);
				break;
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
				break;
		}
	}
}
