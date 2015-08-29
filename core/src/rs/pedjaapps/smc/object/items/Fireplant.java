package rs.pedjaapps.smc.object.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.GameSaveUtility;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 29.3.15..
 */
public class Fireplant extends BoxItem
{
    public static final int POINTS = 700;
    public static final float VELOCITY_POP = 1.6f;
    public static final float DEF_SIZE = 0.59375f;
    public Fireplant(World world, Vector2 size, Vector3 position, Box box)
    {
        super(world, size, position, box);
        textureAtlas = "data/game/items/fireplant.pack";
    }

    @Override
    public void initAssets()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Animation animation = new Animation(2f, atlas.getRegions());
        animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        Assets.animations.put(textureAtlas, animation);
    }

    @Override
    public void updateItem(float delta)
    {
        super.updateItem(delta);
        if(popFromBox)
        {
            // scale velocity to frame units
            velocity.scl(delta);

            // update position
            position.add(velocity);
            mColRect.y = position.y;
            updateBounds();

            // un-scale velocity (not in frame time)
            velocity.scl(1 / delta);

            if(position.y >= popTargetPosY)
            {
                popFromBox = false;
            }
        }
    }

    @Override
    public void popOutFromBox(float popTargetPosY)
    {
        super.popOutFromBox(popTargetPosY);
        visible = true;
        popFromBox = true;
        velocity.y = VELOCITY_POP;
        originalPosY = position.y;
        ParticleEffect effect = Assets.manager.get("data/animation/particles/fireplant_emitter.p", ParticleEffect.class);
        effect.setPosition(position.x + mDrawRect.width / 2, position.y + mDrawRect.height / 2);
        effect.start();
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        if(!visible)return;
        TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);
        Utility.draw(spriteBatch, frame, position.x, position.y, mDrawRect.height);

        ParticleEffect effect = Assets.manager.get("data/animation/particles/fireplant_emitter.p", ParticleEffect.class);
        effect.setPosition(position.x + mDrawRect.width / 2, position.y + mDrawRect.height / 2);
        effect.draw(spriteBatch, Gdx.graphics.getDeltaTime());
    }

    @Override
    public void hitPlayer()
    {
        playerHit = true;
        world.maryo.upgrade(Maryo.MaryoState.fire, false, this);
        box.itemObject = null;
        GameSaveUtility.getInstance().save.points += POINTS;
    }
}
