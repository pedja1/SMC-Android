package rs.papltd.smc.model.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import rs.papltd.smc.Assets;
import rs.papltd.smc.utility.Constants;
import rs.papltd.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Flyon extends Enemy
{
    public static final float FLYON_VELOCITY = 3f;
    private boolean goingUp = true, topReached, bottomReached;
    private long maxPositionReachedTs = 0;
    private long minPositionReachedTs = 0;
    private static final long STAY_TOP_TIME = 300;//2 seconds
    private static final long STAY_BOTTOM_TIME = 2500;//3 seconds

    public Flyon(World world, Vector2 position, float width, float height)
    {
        super(world, position, width, height);
        body.setGravityScale(0);
        velocity.set(0, FLYON_VELOCITY);
    }

    @Override
    public void loadTextures()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureAtlas.AtlasRegion> frames = atlas.getRegions();
        //frames.add(atlas.findRegion(TKey.two.toString()));

        Assets.animations.put(textureAtlas, new Animation(0.25f, frames));
    }

    @Override
    public void render(SpriteBatch spriteBatch, float deltaTime)
    {
        update(deltaTime);
        TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);

        //spriteBatch.draw(frame, body.getPosition().x - getBounds().width/2, body.getPosition().y - getBounds().height/2, bounds.width, bounds.height);
        Utility.draw(spriteBatch, frame, body.getPosition().x - getBounds().width / 2, body.getPosition().y - getBounds().height / 2, bounds.height);
        updateStateTime(deltaTime);
    }

    private void update(float deltaTime)
    {
        stateTime += deltaTime;
        Vector2 position = body.getPosition();
        Vector2 velocity = body.getLinearVelocity();

        long timeNow = System.currentTimeMillis();
        if((topReached && timeNow - maxPositionReachedTs < STAY_TOP_TIME))
        {
            //body.applyForceToCenter(0, /*+world.getGravity().y*/20, true);
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
            if(position.y <= 1f)
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
        }

    }
}
