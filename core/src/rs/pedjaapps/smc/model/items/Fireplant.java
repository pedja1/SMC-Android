package rs.pedjaapps.smc.model.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.model.Maryo;
import rs.pedjaapps.smc.model.World;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSaveUtility;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 29.3.15..
 */
public class Fireplant extends Item
{
    public static final int POINTS = 700;
    public static final float VELOCITY_POP = 1.6f;
    public static final float DEF_SIZE = 0.59375f;
    public Fireplant(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        textureAtlas = "data/game/items/fireplant.pack";
    }

    @Override
    public void loadTextures()
    {
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Assets.animations.put(textureAtlas, new Animation(1.33f, atlas.getRegions()));
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
        if(popFromBox)
        {
            // scale velocity to frame units
            velocity.scl(delta);

            // update position
            position.add(velocity);
            body.y = position.y;
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
        effect.setPosition(position.x + bounds.width / 2, position.y + bounds.height / 2);
        effect.start();
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        if(!visible)return;
        TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);
        Utility.draw(spriteBatch, frame, position.x, position.y, bounds.height);

        ParticleEffect effect = Assets.manager.get("data/animation/particles/fireplant_emitter.p", ParticleEffect.class);
        effect.setPosition(position.x + bounds.width / 2, position.y + bounds.height / 2);
        effect.draw(spriteBatch, Gdx.graphics.getDeltaTime());
    }

    @Override
    public void hitPlayer()
    {
        playerHit = true;
        world.getMario().upgrade(Maryo.MaryoState.fire);
        position.set(-1, -1, -1);//has the effect of removing item (if its not on screen it wont be drawn)
        GameSaveUtility.getInstance().save.points += POINTS;
    }
}
