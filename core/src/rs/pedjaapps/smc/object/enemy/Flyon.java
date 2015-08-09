package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Flyon extends Enemy
{
    public static String DEAD_KEY;
    public static final float FLYON_VELOCITY = 3f;
    private boolean goingUp = true, topReached, bottomReached;
    private long maxPositionReachedTs = 0;
    private long minPositionReachedTs = 0;
    private static final long STAY_TOP_TIME = 300;//2 seconds
    private static final long STAY_BOTTOM_TIME = 2500;//3 seconds

    public Flyon(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
    }

    @Override
    protected TextureRegion getDeadTextureRegion()
    {
        return Assets.loadedRegions.get(DEAD_KEY);
    }

    @Override
    public void initAssets()
    {
        DEAD_KEY = textureAtlas + ":dead";
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureAtlas.AtlasRegion> frames = atlas.getRegions();
        //frames.add(atlas.findRegion(TKey.two.toString()));
        Assets.loadedRegions.put(DEAD_KEY, frames.get(3));
        Assets.animations.put(textureAtlas, new Animation(0.25f, frames));
    }

    @Override
    public void draw(SpriteBatch spriteBatch)
    {
        TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);
        Utility.draw(spriteBatch, frame, bounds.x, bounds.y, bounds.height);
    }

    public void update(float deltaTime)
    {
        /*// Setting initial vertical acceleration
        acceleration.y = Constants.GRAVITY;

        // Convert acceleration to frame time
        acceleration.scl(deltaTime);

        // apply acceleration to change velocity
        velocity.add(acceleration);*/
        if (deadByBullet)
        {
            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(deltaTime);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            checkCollisionWithBlocks(deltaTime, false, false);
            return;
        }

        stateTime += deltaTime;

        long timeNow = System.currentTimeMillis();
        if ((topReached && timeNow - maxPositionReachedTs < STAY_TOP_TIME))
        {
            velocity.set(0, -Constants.GRAVITY, velocity.z);
            return;
        }
        else
        {
            if (position.y > 5)
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
        if ((bottomReached && timeNow - minPositionReachedTs < STAY_BOTTOM_TIME))
        {
            velocity.set(0, 0, velocity.z);
            return;
        }
        else
        {
            if (position.y <= 1.5f)
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
        if (goingUp)
        {
            velocity.set(0, velocity.y = +((Constants.CAMERA_HEIGHT - position.y) / 3f), velocity.z);
        }
        else
        {
            velocity.set(0, velocity.y = -((Constants.CAMERA_HEIGHT - position.y) / 3f), velocity.z);
        }

        updatePosition(deltaTime);
    }
}
