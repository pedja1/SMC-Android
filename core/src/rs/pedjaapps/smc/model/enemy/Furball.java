package rs.pedjaapps.smc.model.enemy;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import rs.pedjaapps.smc.Assets;
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

    public Furball(Vector3 position, float width, float height)
    {
        super(position, width, height);
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
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame = turn ? Assets.loadedRegions.get(textureAtlas + ":turn")
                : Assets.animations.get(direction == Direction.right ? textureAtlas : textureAtlas + "_l").getKeyFrame(stateTime, true);

        //spriteBatch.draw(frame, body.getPosition().x - getBounds().width/2, body.getPosition().y - getBounds().height/2, bounds.width, bounds.height);
        Utility.draw(spriteBatch, frame, position.x - bounds.width / 2, position.y - bounds.height / 2, bounds.height);
    }

    public void update(float deltaTime)
    {
        stateTime += deltaTime;
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
