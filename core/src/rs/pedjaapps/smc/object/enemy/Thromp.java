package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Thromp extends Enemy
{
    public static final float POSITION_Z = 0.093f;
    public String direction;
    private float maxDistance;
    private float speed;
    private Vector3 mOriginPosition;
    private boolean forward = true, staying = true;
    private float rotation;
    private TextureRegion tDefault, tActive;
    private Rectangle tmpRect = new Rectangle();

    public Thromp(World world, Vector2 size, Vector3 position, float maxDistance, float speed, String direction)
    {
        super(world, size, position);
        this.maxDistance = maxDistance;
        this.speed = 3f;//speed;
        this.direction = direction;
        mOriginPosition = new Vector3(position);
        if("left".equals(direction))
        {
            rotation = 90f;
        }
        else if("right".equals(direction))
        {
            rotation = 270f;
        }
        else if("up".equals(direction))
        {
            rotation = 180f;
        }
        mKillPoints = 200;
        mFireResistant = 1;
        position.z = POSITION_Z;
    }

    @Override
    protected TextureRegion getDeadTextureRegion()
    {
        return tDefault;
    }

    @Override
    public void initAssets()
    {
        TextureAtlas atlas = world.screen.game.assets.manager.get(textureAtlas);
        tDefault = atlas.findRegion("default");
        tActive = atlas.findRegion("active");
    }

    @Override
    public void dispose()
    {
        tDefault = null;
        tActive = null;
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        TextureRegion frame = staying ? tDefault : tActive;
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
                staying = false;
                forward = true;
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
                    velocity.y = speed;
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
                    velocity.y = -speed;
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
                    velocity.x = speed;
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
                    velocity.x = speed;
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
            tmpRect.set(mColRect.x + mColRect.width, mColRect.y, maxDistance, mColRect.height);
        }
        return maryo.mColRect.overlaps(tmpRect);
    }

    @Override
    protected String getDeadSound()
    {
        return "data/sounds/enemy/thromp/die.mp3";
    }
}
