package rs.papltd.smc.model.enemy;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import rs.papltd.smc.*;
import rs.papltd.smc.utility.*;

/**
 * Created by pedja on 18.5.14..
 */
public class Furball extends Enemy
{
    public static final float VELOCITY = 3f;

    public Furball(World world, Vector3 position, float width, float height)
    {
        super(world, position, width, height);
        velocity.set(0, VELOCITY);
    }

    @Override
    public void loadTextures()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureAtlas.AtlasRegion> frames = new Array<TextureAtlas.AtlasRegion>();


        Assets.animations.put(textureAtlas, new Animation(0.25f, frames));
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);

        //spriteBatch.draw(frame, body.getPosition().x - getBounds().width/2, body.getPosition().y - getBounds().height/2, bounds.width, bounds.height);
        Utility.draw(spriteBatch, frame, body.getPosition().x - bounds.width / 2, body.getPosition().y - bounds.height / 2, bounds.height);
    }

    public void update(float deltaTime)
    {
        stateTime += deltaTime;
        Vector2 position = body.getPosition();
        Vector2 velocity = body.getLinearVelocity();

        /*long timeNow = System.currentTimeMillis();
        if((topReached && timeNow - maxPositionReachedTs < STAY_TOP_TIME))
        {
            body.setLinearVelocity(0, 0);
            return;
        }
        else
        {
            if(position.y > 5)
            {
                maxPositionReachedTs = System.currentTimeMillis();
                goingUp = false;
                topReached = true;
            }
            else
            {
                topReached = false;
                maxPositionReachedTs = 0;
            }
        }
        if((bottomReached && timeNow - minPositionReachedTs < STAY_BOTTOM_TIME))
        {
            body.setLinearVelocity(0, 0);
            return;
        }
        else
        {
            if(position.y <= 1.5f)
            {
                minPositionReachedTs = System.currentTimeMillis();
                goingUp = true;
                bottomReached = true;
            }
            else
            {
                bottomReached = false;
                minPositionReachedTs = 0;
            }
        }
        if(goingUp)
        {
            body.setLinearVelocity(velocity.x, velocity.y =+((Constants.CAMERA_HEIGHT - position.y)/3f));
        }
        else
        {
            //body.setLinearDamping(5);
            body.setLinearVelocity(velocity.x, velocity.y =-((Constants.CAMERA_HEIGHT - position.y)/3f));
        }*/

    }
}
