package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Flyon extends Enemy
{
    private static final float STAY_BOTTOM_TIME = 2.5f;
    public String direction;
    public float maxDistance;
    public float speed;
    private Vector3 mOriginPosition;
    private boolean forward = true;
    public boolean staying = true;
    private float waitTime;
    private float rotation;
    private Animation<TextureRegion> animation;
    private Rectangle tmpRect = new Rectangle();

    public Flyon(World world, Vector2 size, Vector3 position, float maxDistance, float speed, String direction)
    {
        super(world, size, position);
        this.maxDistance = maxDistance;
        this.speed = 3f;//speed;
        this.direction = direction;
        if("left".equals(direction))
        {
            rotation = 90f;
        }
        else if("right".equals(direction))
        {
            rotation = 270f;
        }
        else if("down".equals(direction))
        {
            rotation = 180f;
        }
        mKillPoints = 100;
        mDrawRect.y = mColRect.y = position.y = position.y - .1f;
        mOriginPosition = new Vector3(position);
        ppEnabled = false;
    }

    @Override
    protected TextureRegion getDeadTextureRegion()
    {
        return animation.getKeyFrames()[3];
    }

    @Override
    public void initAssets()
    {
        TextureAtlas atlas = world.screen.game.assets.manager.get(textureAtlas);
        TextureRegion[] frames = new TextureRegion[4];
        frames[0] = atlas.findRegion("closed", 1);
        frames[1] = atlas.findRegion("closed", 2);
        frames[2] = atlas.findRegion("open", 1);
        frames[3] = atlas.findRegion("open", 2);
        animation = new Animation<>(0.13f, frames);
    }

    @Override
    public void dispose()
    {
        animation = null;
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame = animation.getKeyFrame(staying ? 0 : stateTime, true);
        float width = Utility.getWidth(frame, mDrawRect.height);
        float originX = width * 0.5f;
        float originY = mDrawRect.height * 0.5f;
        spriteBatch.draw(frame, mDrawRect.x, mDrawRect.y, originX, originY, width, mDrawRect.height, 1, 1, rotation);
    }

    @Override
    public boolean canBeKilledByJumpingOnTop()
    {
        return false;
    }

    public void update(float deltaTime)
    {
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
        if(staying)
        {
            if(checkMaryoInFront())
            {
                waitTime = 0;//wait again
            }
            waitTime += deltaTime;
            if(waitTime >= STAY_BOTTOM_TIME)
            {
                staying = false;
                forward = true;
                waitTime = 0;
            }
        }
        else if("up".equals(direction))
        {
            float remainingDistance = mOriginPosition.y + maxDistance - position.y;
            if(forward)
            {
                if (remainingDistance <= 0)
                {
                    forward = false;
                }
                else
                {
                    float distancePercent = 100 / maxDistance * remainingDistance;
                    velocity.y = speed / (100 / distancePercent);
                    if (velocity.y < 0.3f) velocity.y = 0.3f;
                }
            }
            else
            {
                if (remainingDistance >= maxDistance)
                {
                    velocity.y = 0;
                    staying = true;
                    forward = true;
                }
                else
                {
                    float distancePercent = 100 / maxDistance * remainingDistance;
                    velocity.y = -(speed / (100 / distancePercent));
                    if (velocity.y > -0.3f) velocity.y = -0.3f;
                }
            }
        }
        else if("down".equals(direction))
        {
            float remainingDistance = maxDistance - (mOriginPosition.y - position.y);
            if(forward)
            {
                if (remainingDistance <= 0)
                {
                    forward = false;
                }
                else
                {
                    float distancePercent = 100 / maxDistance * remainingDistance;
                    velocity.y = -(speed / (100 / distancePercent));
                    if (velocity.y > -0.3f) velocity.y = -0.3f;
                }
            }
            else
            {
                if (remainingDistance >= maxDistance)
                {
                    velocity.y = 0;
                    staying = true;
                    forward = true;
                }
                else
                {
                    float distancePercent = 100 / maxDistance * remainingDistance;
                    velocity.y = (speed / (100 / distancePercent));
                    if (velocity.y < 0.3f) velocity.y = 0.3f;
                }
            }
        }
        else if("right".equals(direction))
        {
            float remainingDistance = (mOriginPosition.x + maxDistance) - position.x;
            if(forward)
            {
                if (remainingDistance <= 0)
                {
                    forward = false;
                }
                else
                {
                    float distancePercent = 100 / maxDistance * remainingDistance;
                    velocity.x = (speed / (100 / distancePercent));
                    if (velocity.x < 0.3f) velocity.x = 0.3f;
                }
            }
            else
            {
                if (remainingDistance >= maxDistance)
                {
                    velocity.x = 0;
                    staying = true;
                    forward = true;
                }
                else
                {
                    float distancePercent = 100 / maxDistance * remainingDistance;
                    velocity.x = -(speed / (100 / distancePercent));
                    if (velocity.x > -0.3f) velocity.x = -0.3f;
                }
            }
        }
        else if("left".equals(direction))
        {
            float remainingDistance = position.x - (mOriginPosition.x - maxDistance);
            if(forward)
            {
                if (remainingDistance <= 0)
                {
                    forward = false;
                }
                else
                {
                    float distancePercent = 100 / maxDistance * remainingDistance;
                    velocity.x = -(speed / (100 / distancePercent));
                    if (velocity.x > -0.3f) velocity.x = -0.3f;
                }
            }
            else
            {
                if (remainingDistance >= maxDistance)
                {
                    velocity.x = 0;
                    staying = true;
                    forward = true;
                }
                else
                {
                    float distancePercent = 100 / maxDistance * remainingDistance;
                    velocity.x = (speed / (100 / distancePercent));
                    if (velocity.x < 0.3f) velocity.x = 0.3f;
                }
            }
        }

        updatePosition(deltaTime);
    }

    private boolean checkMaryoInFront()
    {
        Maryo maryo = world.maryo;
        if(maryo == null)return false;
        if("up".equals(direction))
        {
            tmpRect.set(mColRect.x, mColRect.y + mColRect.height, mColRect.width, maxDistance + mColRect.height);
        }
        else if("down".equals(direction))
        {
            tmpRect.set(mColRect.x, mColRect.y - maxDistance, mColRect.width, maxDistance);
        }
        else if("left".equals(direction))
        {
            tmpRect.set(mColRect.x - maxDistance, mColRect.y, maxDistance, mColRect.height);
        }
        else if("right".equals(direction))
        {
            tmpRect.set(mColRect.x + maxDistance + mColRect.width, mColRect.y, maxDistance + mColRect.width, mColRect.height);
        }
        return maryo.mColRect.overlaps(tmpRect);
    }

    @Override
    protected String getDeadSound()
    {
        return "data/sounds/enemy/flyon/die.mp3";
    }
}
