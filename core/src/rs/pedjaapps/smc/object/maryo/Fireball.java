package rs.pedjaapps.smc.object.maryo;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 19.8.15..
 */
public class Fireball extends DynamicObject
{
    public static final float POSITION_Z = 0.095f;
    public static final float VELOCITY_X = 7f;
    public static final float VELOCITY_Y = 5f;
    public Direction direction = Direction.right;
    public float velY = -1;
    ParticleEffect trail, explosion;
    private boolean destroyed;
    private Animation animation;

    public Fireball(World world, Vector3 position)
    {
        super(world, new Vector2(.23f, .23f), position);
        position.z = POSITION_Z;
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        trail.setPosition(mColRect.x, mColRect.y + mColRect.height * 0.5f);
        trail.draw(spriteBatch);
        if (!destroyed)
        {
            TextureRegion region = animation.getKeyFrame(stateTime, true);
            Utility.draw(spriteBatch, region, mDrawRect.x, mDrawRect.y, mDrawRect.height);
        }
        else
        {
            explosion.setPosition(mColRect.x + (velocity.x > 0 ? mColRect.width : 0), mColRect.y + mColRect.height * 0.5f);
            explosion.draw(spriteBatch);
        }
    }

    @Override
    public void _update(float delta)
    {
        trail.update(delta);
        if(!destroyed)
        {
            velocity.x = direction == Direction.right ? VELOCITY_X : -VELOCITY_X;
            if (velY != -1)
            {
                velocity.y = velY;
                velY = -1;
            }

            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(delta);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            // checking collisions with the surrounding blocks depending on Bob's velocity
            checkCollisionWithBlocks(delta, true, true, false, false);

            // apply damping to halt Maryo nicely
            velocity.x *= velocityDump;

            // ensure terminal velocity is not exceeded
            //x
            if (velocity.x > maxVelocity())
                velocity.x = maxVelocity();
            if (velocity.x < -maxVelocity())
                velocity.x = -maxVelocity();
        }
        else
        {
            if(explosion.isComplete())
            {
                world.trashObjects.add(this);
                world.FIREBALL_POOL.free(this);
            }
            explosion.update(delta);
        }

        stateTime += delta;
    }

    @Override
    public float maxVelocity()
    {
        return VELOCITY_X;
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical)
    {
        if(destroyed)return false;
        if (object instanceof Sprite)
        {
            if (((Sprite) object).type == Sprite.Type.massive)
            {
                if (vertical)
                {
                    velY = mColRect.y < object.mColRect.y ? 0 : VELOCITY_Y;
                }
                else
                {
                    if (mColRect.y > groundY) destroy();
                }
                return true;
            }
            else if (((Sprite) object).type == Sprite.Type.halfmassive)
            {
                if (vertical && mColRect.y + mColRect.height > object.mColRect.y + object.mColRect.height)
                {
                    velY = VELOCITY_Y;
                    return true;
                }
            }
        }
        else if (object instanceof Enemy)
        {

            if(((Enemy) object).mFireResistant != 1)
                ((Enemy) object).downgradeOrDie(this, false);
            else
            {
                //repelled sound
            }
            destroy();
        }
        return false;
    }

    @Override
    protected boolean handleDroppedBelowWorld()
    {
        destroy();
        return true;
    }

    @Override
    public void initAssets()
    {
        if (animation == null)
        {
            TextureAtlas atlas = Assets.manager.get("data/animation/fireball.pack", TextureAtlas.class);
            Array<TextureAtlas.AtlasRegion> regions = atlas.getRegions();

            animation = new Animation(0.05f, regions);
        }
        trail = new ParticleEffect(Assets.manager.get("data/animation/particles/fireball_emitter.p", ParticleEffect.class));
        explosion = new ParticleEffect(Assets.manager.get("data/animation/particles/fireball_explosion_emitter.p", ParticleEffect.class));
    }

    @Override
    public void dispose()
    {
        animation = null;
        trail.dispose();
        trail = null;
        explosion.dispose();
        explosion = null;
    }

    public void destroy()
    {
        destroyed = true;
        trail.allowCompletion();
        explosion.reset();
        explosion.getEmitters().get(0).getAngle().setHighMin(velocity.x > 0 ? 270 : -90);
        explosion.getEmitters().get(0).getAngle().setHighMax(velocity.x > 0 ? 90 : 90);
    }

    public void reset()
    {
        velocity.set(0, 0, 0);
        destroyed = false;
        trail.reset();
        trail.setPosition(mColRect.x, mColRect.y + mColRect.height * 0.5f);
        explosion.reset();
    }
}
